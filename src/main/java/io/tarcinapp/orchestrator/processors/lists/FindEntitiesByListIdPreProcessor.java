package io.tarcinapp.orchestrator.processors.lists;

import io.tarcinapp.orchestrator.gateway.EntityPersistenceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Pre-processor for GET /lists/{id}/entities (operationId: findEntitiesByListId).
 *
 * This bean is executed BEFORE the backend call.
 * Inject {@link EntityPersistenceClient} to make orchestration calls back
 * through the gateway from within this pre-processor.
 *
 * <p>To short-circuit the route (skip backend + post-processor):</p>
 * <pre>
 *   exchange.setProperty("CamelRouteStop", Boolean.TRUE);
 *   exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
 *   exchange.getMessage().setBody("{ \"message\": \"handled by pre-processor\" }");
 * </pre>
 */
@ApplicationScoped
public class FindEntitiesByListIdPreProcessor implements Processor {

    @Inject
    @RestClient
    EntityPersistenceClient gatewayClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO: implement pre-processing logic for findEntitiesByListId
    }
}
