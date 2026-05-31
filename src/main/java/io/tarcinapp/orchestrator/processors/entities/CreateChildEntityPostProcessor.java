package io.tarcinapp.orchestrator.processors.entities;

import io.tarcinapp.orchestrator.gateway.EntityPersistenceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Post-processor for POST /entities/{id}/children (operationId: createChildEntity).
 *
 * This bean is executed AFTER the backend has responded.
 * Inject {@link EntityPersistenceClient} to make orchestration calls back
 * through the gateway from within this post-processor.
 *
 * <p>Useful exchange accessors:</p>
 * <pre>
 *   String responseBody = exchange.getMessage().getBody(String.class);
 *   int statusCode = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
 * </pre>
 */
@ApplicationScoped
public class CreateChildEntityPostProcessor implements Processor {

    @Inject
    @RestClient
    EntityPersistenceClient gatewayClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO: implement post-processing logic for createChildEntity
    }
}
