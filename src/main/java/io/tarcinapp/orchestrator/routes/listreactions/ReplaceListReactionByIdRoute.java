package io.tarcinapp.orchestrator.routes.listreactions;

import io.tarcinapp.orchestrator.processors.listreactions.ReplaceListReactionByIdPreProcessor;
import io.tarcinapp.orchestrator.processors.listreactions.ReplaceListReactionByIdPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel route for PUT /list-reactions/{id} (operationId: replaceListReactionById).
 *
 * Execution flow: ReplaceListReactionByIdPreProcessor → backend proxy → ReplaceListReactionByIdPostProcessor
 *
 * Short-circuit: if the pre-processor sets
 *   exchange.setProperty("CamelRouteStop", Boolean.TRUE)
 * the backend call and post-processor are skipped.  The pre-processor is
 * responsible for setting the desired response body and HTTP status code:
 *   exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
 *   exchange.getMessage().setBody("{ ... }");
 */
@ApplicationScoped
public class ReplaceListReactionByIdRoute extends RouteBuilder {

    @Inject
    ReplaceListReactionByIdPreProcessor preProcessor;

    @Inject
    ReplaceListReactionByIdPostProcessor postProcessor;

    @Override
    public void configure() {
        from("platform-http:/list-reactions/{id}?httpMethodRestrict=PUT")
            .routeId("replaceListReactionById")
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
                    .toD("{{backend.base-url}}/list-reactions/${header.id}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                    .process(postProcessor)
            .end();
    }
}
