package io.tarcinapp.orchestrator.routes.listreactions;

import io.tarcinapp.orchestrator.processors.listreactions.FindParentsByListReactionIdPreProcessor;
import io.tarcinapp.orchestrator.processors.listreactions.FindParentsByListReactionIdPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel route for GET /list-reactions/{id}/parents (operationId: findParentsByListReactionId).
 *
 * Execution flow: FindParentsByListReactionIdPreProcessor → backend proxy → FindParentsByListReactionIdPostProcessor
 *
 * Short-circuit: if the pre-processor sets
 *   exchange.setProperty("CamelRouteStop", Boolean.TRUE)
 * the backend call and post-processor are skipped.  The pre-processor is
 * responsible for setting the desired response body and HTTP status code:
 *   exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
 *   exchange.getMessage().setBody("{ ... }");
 */
@ApplicationScoped
public class FindParentsByListReactionIdRoute extends RouteBuilder {

    @Inject
    FindParentsByListReactionIdPreProcessor preProcessor;

    @Inject
    FindParentsByListReactionIdPostProcessor postProcessor;

    @Override
    public void configure() {
        from("platform-http:/list-reactions/{id}/parents?httpMethodRestrict=GET")
            .routeId("findParentsByListReactionId")
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
                    .toD("{{backend.base-url}}/list-reactions/${header.id}/parents?bridgeEndpoint=true&throwExceptionOnFailure=false")
                    .process(postProcessor)
            .end();
    }
}
