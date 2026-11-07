package io.tarcinapp.orchestrator.routes.entityreactions;

import io.tarcinapp.orchestrator.processors.entityreactions.CountEntityReactionsPreProcessor;
import io.tarcinapp.orchestrator.processors.entityreactions.CountEntityReactionsPostProcessor;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel route for GET /entity-reactions/count (operationId: countEntityReactions).
 *
 * Execution flow: CountEntityReactionsPreProcessor → backend proxy → CountEntityReactionsPostProcessor
 *
 * Short-circuit: if the pre-processor sets
 *   exchange.setProperty("CamelRouteStop", Boolean.TRUE)
 * the backend call and post-processor are skipped.  The pre-processor is
 * responsible for setting the desired response body and HTTP status code:
 *   exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
 *   exchange.getMessage().setBody("{ ... }");
 */
@Priority(10)
@ApplicationScoped
public class CountEntityReactionsRoute extends RouteBuilder {

    @Inject
    CountEntityReactionsPreProcessor preProcessor;

    @Inject
    CountEntityReactionsPostProcessor postProcessor;

    @Override
    public void configure() {
        from("platform-http:/entity-reactions/count?httpMethodRestrict=GET")
            .routeId("countEntityReactions")
            .process(preProcessor)
            .choice()
                .when(exchangeProperty("CamelRouteStop").isEqualTo(Boolean.TRUE))
                    .log("Route ${routeId} short-circuited by pre-processor — skipping backend and post-processor")
                .otherwise()
                    // Strip hop-by-hop headers that must not be forwarded
                    .removeHeader("Content-Length")
                    .removeHeader("Transfer-Encoding")
                    // Proxy the request to the backend; query string is forwarded
                    // automatically because bridgeEndpoint=true reads CamelHttpQuery.
                    // throwExceptionOnFailure=false passes 4xx/5xx responses
                    // transparently instead of raising a CamelHttpOperationFailedException.
                    .toD("{{backend.base-url}}/entity-reactions/count?bridgeEndpoint=true&throwExceptionOnFailure=false")
                    .process(postProcessor)
            .end();
    }
}
