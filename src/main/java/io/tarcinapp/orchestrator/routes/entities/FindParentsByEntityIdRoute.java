package io.tarcinapp.orchestrator.routes.entities;

import io.tarcinapp.orchestrator.processors.entities.FindParentsByEntityIdPreProcessor;
import io.tarcinapp.orchestrator.processors.entities.FindParentsByEntityIdPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel route for GET /entities/{id}/parents (operationId: findParentsByEntityId).
 *
 * Execution flow: FindParentsByEntityIdPreProcessor → backend proxy → FindParentsByEntityIdPostProcessor
 *
 * Short-circuit: if the pre-processor sets
 *   exchange.setProperty("CamelRouteStop", Boolean.TRUE)
 * the backend call and post-processor are skipped.  The pre-processor is
 * responsible for setting the desired response body and HTTP status code:
 *   exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
 *   exchange.getMessage().setBody("{ ... }");
 */
@ApplicationScoped
public class FindParentsByEntityIdRoute extends RouteBuilder {

    @Inject
    FindParentsByEntityIdPreProcessor preProcessor;

    @Inject
    FindParentsByEntityIdPostProcessor postProcessor;

    @Override
    public void configure() {
        from("platform-http:/entities/{id}/parents?httpMethodRestrict=GET")
            .routeId("findParentsByEntityId")
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
                    .toD("{{backend.base-url}}/entities/${header.id}/parents?bridgeEndpoint=true&throwExceptionOnFailure=false")
                    .process(postProcessor)
            .end();
    }
}
