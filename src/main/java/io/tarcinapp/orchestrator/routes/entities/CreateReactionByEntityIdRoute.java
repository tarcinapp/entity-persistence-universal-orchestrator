package io.tarcinapp.orchestrator.routes.entities;

import io.tarcinapp.orchestrator.processors.entities.CreateReactionByEntityIdPreProcessor;
import io.tarcinapp.orchestrator.processors.entities.CreateReactionByEntityIdPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

/**
 * Camel route for POST /entities/{id}/reactions (operationId: createReactionByEntityId).
 *
 * Execution flow: CreateReactionByEntityIdPreProcessor → backend proxy → CreateReactionByEntityIdPostProcessor
 *
 * Short-circuit: if the pre-processor sets
 *   exchange.setProperty("CamelRouteStop", Boolean.TRUE)
 * the backend call and post-processor are skipped.  The pre-processor is
 * responsible for setting the desired response body and HTTP status code:
 *   exchange.getMessage().setHeader("CamelHttpResponseCode", 200);
 *   exchange.getMessage().setBody("{ ... }");
 */
@ApplicationScoped
public class CreateReactionByEntityIdRoute extends RouteBuilder {

    @Inject
    CreateReactionByEntityIdPreProcessor preProcessor;

    @Inject
    CreateReactionByEntityIdPostProcessor postProcessor;

    @Override
    public void configure() {
        from("platform-http:/entities/{id}/reactions?httpMethodRestrict=POST")
            .routeId("createReactionByEntityId")
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
                    .toD("{{backend.base-url}}/entities/${header.id}/reactions?bridgeEndpoint=true&throwExceptionOnFailure=false")
                    .process(postProcessor)
            .end();
    }
}
