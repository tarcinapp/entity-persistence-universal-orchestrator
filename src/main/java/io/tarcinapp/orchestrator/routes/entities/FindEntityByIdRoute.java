package io.tarcinapp.orchestrator.routes.entities;

import io.tarcinapp.orchestrator.processors.entities.FindEntityByIdPreProcessor;
import io.tarcinapp.orchestrator.processors.entities.FindEntityByIdPostProcessor;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel route for GET /entities/{id} (operationId: findEntityById).
 *
 * Execution flow: FindEntityByIdPreProcessor → backend proxy → FindEntityByIdPostProcessor
 *
 * Short-circuit: if the pre-processor sets
 *   exchange.setProperty("CamelRouteStop", Boolean.TRUE)
 * the backend call and post-processor are skipped.  The pre-processor is
 * responsible for setting the desired response body and HTTP status code:
 *   exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
 *   exchange.getMessage().setBody("{ ... }");
 */
@Priority(20)
@ApplicationScoped
public class FindEntityByIdRoute extends RouteBuilder {

    @Inject
    FindEntityByIdPreProcessor preProcessor;

    @Inject
    FindEntityByIdPostProcessor postProcessor;

    @Override
    public void configure() {
        from("platform-http:/entities/{id}?httpMethodRestrict=GET")
            .routeId("findEntityById")
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
                    .toD("{{backend.base-url}}${header.CamelHttpPath}?bridgeEndpoint=true&throwExceptionOnFailure=false")
                    .process(postProcessor)
            .end();
    }
}
