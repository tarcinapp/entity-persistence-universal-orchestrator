package io.tarcinapp.orchestrator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.apache.camel.component.platform.http.PlatformHttpComponent;
import org.apache.camel.quarkus.main.events.BeforeStart;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.jboss.logging.Logger;

/**
 * Configures the Camel {@code platform-http} component with a custom
 * {@link DefaultHeaderFilterStrategy} before routes start accepting requests.
 *
 * <h3>Problem</h3>
 * Camel's {@code platform-http} consumer stores incoming URL query parameters
 * (e.g. {@code filter[limit]}, {@code filter[skip]}) as Exchange message headers.
 * When writing the HTTP response, Camel attempts to copy all Exchange message
 * headers — including those query-parameter headers — into the response.
 * Vert.x then throws:
 * <pre>
 *   IllegalArgumentException: a header name cannot contain some prohibited
 *   characters, such as : filter[limit]
 * </pre>
 * because {@code [} and {@code ]} are not valid HTTP header name characters
 * (RFC 7230 §3.2).
 *
 * <h3>Fix</h3>
 * Configure the component's {@code HeaderFilterStrategy} to reject (outbound)
 * any header whose name contains {@code [} or {@code ]}.
 * The query parameters remain available inside the Exchange for routing logic;
 * they are simply not written into the HTTP response.
 */
@ApplicationScoped
public class CamelPlatformHttpConfig {

    private static final Logger LOG = Logger.getLogger(CamelPlatformHttpConfig.class);

    void onBeforeStart(@Observes BeforeStart event) {
        PlatformHttpComponent phc = event.getCamelContext()
                .getComponent("platform-http", PlatformHttpComponent.class);

        if (phc == null) {
            LOG.warn("platform-http component not found — skipping header filter strategy configuration.");
            return;
        }

        DefaultHeaderFilterStrategy strategy = new DefaultHeaderFilterStrategy();

        // Filter headers whose names contain [ or ] from being written to the
        // HTTP response.  These originate from URL query parameters such as
        // filter[limit], filter[skip], filter[where][_kind], etc.
        // RFC 7230 §3.2 forbids [ and ] in HTTP header field names, so Vert.x
        // throws IllegalArgumentException when Camel tries to set them.
        //
        // Note: DefaultHeaderFilterStrategy always filters Camel-internal headers
        // (the CAMEL_FILTER_PATTERN) independently of this pattern.
        strategy.setOutFilterPattern(".*[\\[\\]].*");

        phc.setHeaderFilterStrategy(strategy);

        LOG.debug("platform-http HeaderFilterStrategy configured to exclude headers with [ or ] characters.");
    }
}
