package com.tarcinapp.orchestrator.processors.system;

import com.tarcinapp.orchestrator.gateway.EntityPersistenceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Post-processor for GET /openapi.json (operationId: getOpenApiJson).
 *
 * This bean is executed AFTER the backend call.
 * Inject {@link EntityPersistenceClient} to make orchestration calls back
 * through the gateway from within this post-processor.
 *
 * <p>To override the backend response:</p>
 * <pre>
 *   exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
 *   exchange.getMessage().setBody("{ \"modified\": true }");
 * </pre>
 */
@ApplicationScoped
public class GetOpenApiJsonPostProcessor implements Processor {

    @Inject
    @RestClient
    EntityPersistenceClient gatewayClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO: implement post-processing logic for getOpenApiJson
    }
}
