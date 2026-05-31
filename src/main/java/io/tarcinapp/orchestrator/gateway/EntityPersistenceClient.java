package io.tarcinapp.orchestrator.gateway;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * MicroProfile REST Client — Internal gateway client for the orchestrator.
 *
 * <p>This interface mirrors every endpoint exposed by the
 * entity-persistence-universal-orchestrator itself (i.e. the gateway routes
 * that front the entity-persistence-service backend).  Inject it into any
 * {@link org.apache.camel.Processor} to make orchestration calls from within
 * a pre- or post-processor.</p>
 *
 * <h3>Injection</h3>
 * <pre>
 *   &#64;Inject
 *   &#64;RestClient
 *   EntityPersistenceClient gatewayClient;
 * </pre>
 *
 * <h3>Configuration</h3>
 * <p>The base URL is controlled by
 * {@code quarkus.rest-client."entity-persistence-gateway".url} in
 * {@code application.properties}, which defaults to {@code gateway.base-url}
 * and can be overridden via the {@code GATEWAY_BASE_URL} environment variable.
 * </p>
 *
 * <h3>Tracing</h3>
 * <p>OpenTelemetry W3C Trace Context (traceparent / tracestate) and B3 headers
 * are propagated automatically on every outbound call because the
 * {@code quarkus-opentelemetry} extension instruments the Quarkus REST client.</p>
 *
 * <h3>Parameter conventions</h3>
 * <ul>
 *   <li>Query parameters that accept complex LoopBack filter/where objects are
 *       typed as {@code String} and must be JSON-serialised by the caller.</li>
 *   <li>Request bodies are typed as {@code Object}; pass a POJO, a
 *       {@code String}, or a {@code jakarta.json.JsonObject}.</li>
 *   <li>All methods return {@link Response} so callers can inspect the HTTP
 *       status code, response headers, and body.</li>
 * </ul>
 */
@RegisterRestClient(configKey = "entity-persistence-gateway")
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface EntityPersistenceClient {

    // =========================================================================
    // /entities
    // =========================================================================

    /**
     * GET /entities/count — countEntities
     *
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     */
    @GET
    @Path("/entities/count")
    Response countEntities(
            @QueryParam("set")   String set,
            @QueryParam("where") String where);

    /**
     * GET /entities — findEntities
     *
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/entities")
    Response findEntities(
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    /**
     * POST /entities — createEntity
     *
     * @param body request body
     */
    @POST
    @Path("/entities")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createEntity(Object body);

    /**
     * PATCH /entities — updateEntities
     *
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     * @param body  partial update payload
     */
    @PATCH
    @Path("/entities")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateEntities(
            @QueryParam("set")   String set,
            @QueryParam("where") String where,
            Object body);

    // ── /entities/{id} ───────────────────────────────────────────────────────

    /**
     * GET /entities/{id} — findEntityById
     *
     * @param id     entity identifier
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/entities/{id}")
    Response findEntityById(
            @PathParam("id")     String id,
            @QueryParam("filter") String filter);

    /**
     * PUT /entities/{id} — replaceEntityById
     *
     * @param id   entity identifier
     * @param body full replacement payload
     */
    @PUT
    @Path("/entities/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response replaceEntityById(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /entities/{id} — updateEntityById
     *
     * @param id   entity identifier
     * @param body partial update payload
     */
    @PATCH
    @Path("/entities/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateEntityById(
            @PathParam("id") String id,
            Object body);

    /**
     * DELETE /entities/{id} — deleteEntityById
     *
     * @param id entity identifier
     */
    @DELETE
    @Path("/entities/{id}")
    Response deleteEntityById(@PathParam("id") String id);

    // ── /entities/{id}/children ───────────────────────────────────────────────

    /**
     * POST /entities/{id}/children — createChildEntity
     *
     * @param id   parent entity identifier
     * @param body child entity payload
     */
    @POST
    @Path("/entities/{id}/children")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createChildEntity(
            @PathParam("id") String id,
            Object body);

    /**
     * GET /entities/{id}/children — findChildrenByEntityId
     *
     * @param id     parent entity identifier
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/entities/{id}/children")
    Response findChildrenByEntityId(
            @PathParam("id")      String id,
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    // ── /entities/{id}/lists ─────────────────────────────────────────────────

    /**
     * GET /entities/{id}/lists — findListsByEntityId
     *
     * @param id            entity identifier
     * @param set           JSON-encoded set filter
     * @param filter        JSON-encoded filter object
     * @param setThrough    JSON-encoded through-relation set filter
     * @param filterThrough JSON-encoded through-relation filter
     */
    @GET
    @Path("/entities/{id}/lists")
    Response findListsByEntityId(
            @PathParam("id")            String id,
            @QueryParam("set")          String set,
            @QueryParam("filter")       String filter,
            @QueryParam("setThrough")   String setThrough,
            @QueryParam("filterThrough") String filterThrough);

    // ── /entities/{id}/parents ───────────────────────────────────────────────

    /**
     * GET /entities/{id}/parents — findParentsByEntityId
     *
     * @param id     entity identifier
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/entities/{id}/parents")
    Response findParentsByEntityId(
            @PathParam("id")      String id,
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    // ── /entities/{id}/reactions ─────────────────────────────────────────────

    /**
     * POST /entities/{id}/reactions — createReactionByEntityId
     *
     * @param id   entity identifier
     * @param body reaction payload
     */
    @POST
    @Path("/entities/{id}/reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createReactionByEntityId(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /entities/{id}/reactions — updateReactionsByEntityId
     *
     * @param id    entity identifier
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     * @param body  partial update payload
     */
    @PATCH
    @Path("/entities/{id}/reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateReactionsByEntityId(
            @PathParam("id")     String id,
            @QueryParam("set")   String set,
            @QueryParam("where") String where,
            Object body);

    /**
     * GET /entities/{id}/reactions — findReactionsByEntityId
     *
     * @param id     entity identifier
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/entities/{id}/reactions")
    Response findReactionsByEntityId(
            @PathParam("id")      String id,
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    /**
     * DELETE /entities/{id}/reactions — deleteReactionsByEntityId
     *
     * @param id    entity identifier
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     */
    @DELETE
    @Path("/entities/{id}/reactions")
    Response deleteReactionsByEntityId(
            @PathParam("id")     String id,
            @QueryParam("set")   String set,
            @QueryParam("where") String where);

    // =========================================================================
    // /entity-reactions
    // =========================================================================

    /**
     * GET /entity-reactions/count — countEntityReactions
     *
     * @param set         JSON-encoded set filter
     * @param where       JSON-encoded where filter
     * @param entitySet   JSON-encoded entity set filter
     * @param entityWhere JSON-encoded entity where filter
     */
    @GET
    @Path("/entity-reactions/count")
    Response countEntityReactions(
            @QueryParam("set")         String set,
            @QueryParam("where")       String where,
            @QueryParam("entitySet")   String entitySet,
            @QueryParam("entityWhere") String entityWhere);

    /**
     * GET /entity-reactions — findEntityReactions
     *
     * @param set          JSON-encoded set filter
     * @param filter       JSON-encoded filter object
     * @param entitySet    JSON-encoded entity set filter
     * @param entityFilter JSON-encoded entity filter object
     */
    @GET
    @Path("/entity-reactions")
    Response findEntityReactions(
            @QueryParam("set")          String set,
            @QueryParam("filter")       String filter,
            @QueryParam("entitySet")    String entitySet,
            @QueryParam("entityFilter") String entityFilter);

    /**
     * POST /entity-reactions — createEntityReaction
     *
     * @param body entity-reaction payload
     */
    @POST
    @Path("/entity-reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createEntityReaction(Object body);

    /**
     * PATCH /entity-reactions — updateEntityReactions
     *
     * @param set       JSON-encoded set filter
     * @param where     JSON-encoded where filter
     * @param entitySet JSON-encoded entity set filter
     * @param body      partial update payload
     */
    @PATCH
    @Path("/entity-reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateEntityReactions(
            @QueryParam("set")       String set,
            @QueryParam("where")     String where,
            @QueryParam("entitySet") String entitySet,
            Object body);

    // ── /entity-reactions/{id} ────────────────────────────────────────────────

    /**
     * GET /entity-reactions/{id} — findEntityReactionById
     *
     * @param id     entity-reaction identifier
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/entity-reactions/{id}")
    Response findEntityReactionById(
            @PathParam("id")      String id,
            @QueryParam("filter") String filter);

    /**
     * PUT /entity-reactions/{id} — replaceEntityReactionById
     *
     * @param id   entity-reaction identifier
     * @param body full replacement payload
     */
    @PUT
    @Path("/entity-reactions/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response replaceEntityReactionById(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /entity-reactions/{id} — updateEntityReactionById
     *
     * @param id   entity-reaction identifier
     * @param body partial update payload
     */
    @PATCH
    @Path("/entity-reactions/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateEntityReactionById(
            @PathParam("id") String id,
            Object body);

    /**
     * DELETE /entity-reactions/{id} — deleteEntityReactionById
     *
     * @param id entity-reaction identifier
     */
    @DELETE
    @Path("/entity-reactions/{id}")
    Response deleteEntityReactionById(@PathParam("id") String id);

    // ── /entity-reactions/{id}/children ───────────────────────────────────────

    /**
     * POST /entity-reactions/{id}/children — createChildEntityReaction
     *
     * @param id   parent entity-reaction identifier
     * @param body child entity-reaction payload
     */
    @POST
    @Path("/entity-reactions/{id}/children")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createChildEntityReaction(
            @PathParam("id") String id,
            Object body);

    /**
     * GET /entity-reactions/{id}/children — findChildrenByEntityReactionId
     *
     * @param id           parent entity-reaction identifier
     * @param set          JSON-encoded set filter
     * @param filter       JSON-encoded filter object
     * @param entitySet    JSON-encoded entity set filter
     * @param entityFilter JSON-encoded entity filter object
     */
    @GET
    @Path("/entity-reactions/{id}/children")
    Response findChildrenByEntityReactionId(
            @PathParam("id")            String id,
            @QueryParam("set")          String set,
            @QueryParam("filter")       String filter,
            @QueryParam("entitySet")    String entitySet,
            @QueryParam("entityFilter") String entityFilter);

    // ── /entity-reactions/{id}/parents ────────────────────────────────────────

    /**
     * GET /entity-reactions/{id}/parents — findParentsByEntityReactionId
     *
     * @param id           entity-reaction identifier
     * @param set          JSON-encoded set filter
     * @param filter       JSON-encoded filter object
     * @param entitySet    JSON-encoded entity set filter
     * @param entityFilter JSON-encoded entity filter object
     */
    @GET
    @Path("/entity-reactions/{id}/parents")
    Response findParentsByEntityReactionId(
            @PathParam("id")            String id,
            @QueryParam("set")          String set,
            @QueryParam("filter")       String filter,
            @QueryParam("entitySet")    String entitySet,
            @QueryParam("entityFilter") String entityFilter);

    // =========================================================================
    // /list-reactions
    // =========================================================================

    /**
     * GET /list-reactions/count — countListReactions
     *
     * @param set      JSON-encoded set filter
     * @param where    JSON-encoded where filter
     * @param listSet  JSON-encoded list set filter
     * @param listWhere JSON-encoded list where filter
     */
    @GET
    @Path("/list-reactions/count")
    Response countListReactions(
            @QueryParam("set")       String set,
            @QueryParam("where")     String where,
            @QueryParam("listSet")   String listSet,
            @QueryParam("listWhere") String listWhere);

    /**
     * GET /list-reactions — findListReactions
     *
     * @param set        JSON-encoded set filter
     * @param filter     JSON-encoded filter object
     * @param listSet    JSON-encoded list set filter
     * @param listFilter JSON-encoded list filter object
     */
    @GET
    @Path("/list-reactions")
    Response findListReactions(
            @QueryParam("set")        String set,
            @QueryParam("filter")     String filter,
            @QueryParam("listSet")    String listSet,
            @QueryParam("listFilter") String listFilter);

    /**
     * POST /list-reactions — createListReaction
     *
     * @param body list-reaction payload
     */
    @POST
    @Path("/list-reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createListReaction(Object body);

    /**
     * PATCH /list-reactions — updateListReactions
     *
     * @param set     JSON-encoded set filter
     * @param where   JSON-encoded where filter
     * @param listSet JSON-encoded list set filter
     * @param body    partial update payload
     */
    @PATCH
    @Path("/list-reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateListReactions(
            @QueryParam("set")     String set,
            @QueryParam("where")   String where,
            @QueryParam("listSet") String listSet,
            Object body);

    // ── /list-reactions/{id} ──────────────────────────────────────────────────

    /**
     * GET /list-reactions/{id} — findListReactionById
     *
     * @param id     list-reaction identifier
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/list-reactions/{id}")
    Response findListReactionById(
            @PathParam("id")      String id,
            @QueryParam("filter") String filter);

    /**
     * PUT /list-reactions/{id} — replaceListReactionById
     *
     * @param id   list-reaction identifier
     * @param body full replacement payload
     */
    @PUT
    @Path("/list-reactions/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response replaceListReactionById(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /list-reactions/{id} — updateListReactionById
     *
     * @param id   list-reaction identifier
     * @param body partial update payload
     */
    @PATCH
    @Path("/list-reactions/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateListReactionById(
            @PathParam("id") String id,
            Object body);

    /**
     * DELETE /list-reactions/{id} — deleteListReactionById
     *
     * @param id list-reaction identifier
     */
    @DELETE
    @Path("/list-reactions/{id}")
    Response deleteListReactionById(@PathParam("id") String id);

    // ── /list-reactions/{id}/children ─────────────────────────────────────────

    /**
     * POST /list-reactions/{id}/children — createChildListReaction
     *
     * @param id   parent list-reaction identifier
     * @param body child list-reaction payload
     */
    @POST
    @Path("/list-reactions/{id}/children")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createChildListReaction(
            @PathParam("id") String id,
            Object body);

    /**
     * GET /list-reactions/{id}/children — findChildrenByListReactionId
     *
     * @param id         parent list-reaction identifier
     * @param set        JSON-encoded set filter
     * @param filter     JSON-encoded filter object
     * @param listSet    JSON-encoded list set filter
     * @param listFilter JSON-encoded list filter object
     */
    @GET
    @Path("/list-reactions/{id}/children")
    Response findChildrenByListReactionId(
            @PathParam("id")          String id,
            @QueryParam("set")        String set,
            @QueryParam("filter")     String filter,
            @QueryParam("listSet")    String listSet,
            @QueryParam("listFilter") String listFilter);

    // ── /list-reactions/{id}/parents ──────────────────────────────────────────

    /**
     * GET /list-reactions/{id}/parents — findParentsByListReactionId
     *
     * @param id         list-reaction identifier
     * @param set        JSON-encoded set filter
     * @param filter     JSON-encoded filter object
     * @param listSet    JSON-encoded list set filter
     * @param listFilter JSON-encoded list filter object
     */
    @GET
    @Path("/list-reactions/{id}/parents")
    Response findParentsByListReactionId(
            @PathParam("id")          String id,
            @QueryParam("set")        String set,
            @QueryParam("filter")     String filter,
            @QueryParam("listSet")    String listSet,
            @QueryParam("listFilter") String listFilter);

    // =========================================================================
    // /lists
    // =========================================================================

    /**
     * GET /lists/count — countLists
     *
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     */
    @GET
    @Path("/lists/count")
    Response countLists(
            @QueryParam("set")   String set,
            @QueryParam("where") String where);

    /**
     * GET /lists — findLists
     *
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/lists")
    Response findLists(
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    /**
     * POST /lists — createList
     *
     * @param body list payload
     */
    @POST
    @Path("/lists")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createList(Object body);

    /**
     * PATCH /lists — updateLists
     *
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     * @param body  partial update payload
     */
    @PATCH
    @Path("/lists")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateLists(
            @QueryParam("set")   String set,
            @QueryParam("where") String where,
            Object body);

    // ── /lists/{id} ──────────────────────────────────────────────────────────

    /**
     * GET /lists/{id} — findListById
     *
     * @param id     list identifier
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/lists/{id}")
    Response findListById(
            @PathParam("id")      String id,
            @QueryParam("filter") String filter);

    /**
     * PUT /lists/{id} — replaceListById
     *
     * @param id   list identifier
     * @param body full replacement payload
     */
    @PUT
    @Path("/lists/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response replaceListById(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /lists/{id} — updateListById
     *
     * @param id   list identifier
     * @param body partial update payload
     */
    @PATCH
    @Path("/lists/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateListById(
            @PathParam("id") String id,
            Object body);

    /**
     * DELETE /lists/{id} — deleteListById
     *
     * @param id list identifier
     */
    @DELETE
    @Path("/lists/{id}")
    Response deleteListById(@PathParam("id") String id);

    // ── /lists/{id}/children ─────────────────────────────────────────────────

    /**
     * POST /lists/{id}/children — createChildList
     *
     * @param id   parent list identifier
     * @param body child list payload
     */
    @POST
    @Path("/lists/{id}/children")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createChildList(
            @PathParam("id") String id,
            Object body);

    /**
     * GET /lists/{id}/children — findChildrenByListId
     *
     * @param id     parent list identifier
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/lists/{id}/children")
    Response findChildrenByListId(
            @PathParam("id")      String id,
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    // ── /lists/{id}/entities ─────────────────────────────────────────────────

    /**
     * POST /lists/{id}/entities — createEntityByListId
     *
     * @param id   list identifier
     * @param body entity payload
     */
    @POST
    @Path("/lists/{id}/entities")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createEntityByListId(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /lists/{id}/entities — updateEntitiesByListId
     *
     * @param id           list identifier
     * @param set          JSON-encoded set filter
     * @param where        JSON-encoded where filter
     * @param setThrough   JSON-encoded through-relation set filter
     * @param whereThrough JSON-encoded through-relation where filter
     * @param body         partial update payload
     */
    @PATCH
    @Path("/lists/{id}/entities")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateEntitiesByListId(
            @PathParam("id")            String id,
            @QueryParam("set")          String set,
            @QueryParam("where")        String where,
            @QueryParam("setThrough")   String setThrough,
            @QueryParam("whereThrough") String whereThrough,
            Object body);

    /**
     * GET /lists/{id}/entities — findEntitiesByListId
     *
     * @param id            list identifier
     * @param set           JSON-encoded set filter
     * @param filter        JSON-encoded filter object
     * @param setThrough    JSON-encoded through-relation set filter
     * @param filterThrough JSON-encoded through-relation filter object
     */
    @GET
    @Path("/lists/{id}/entities")
    Response findEntitiesByListId(
            @PathParam("id")             String id,
            @QueryParam("set")           String set,
            @QueryParam("filter")        String filter,
            @QueryParam("setThrough")    String setThrough,
            @QueryParam("filterThrough") String filterThrough);

    /**
     * DELETE /lists/{id}/entities — deleteEntitiesByListId
     *
     * @param id           list identifier
     * @param set          JSON-encoded set filter
     * @param where        JSON-encoded where filter
     * @param setThrough   JSON-encoded through-relation set filter
     * @param whereThrough JSON-encoded through-relation where filter
     */
    @DELETE
    @Path("/lists/{id}/entities")
    Response deleteEntitiesByListId(
            @PathParam("id")            String id,
            @QueryParam("set")          String set,
            @QueryParam("where")        String where,
            @QueryParam("setThrough")   String setThrough,
            @QueryParam("whereThrough") String whereThrough);

    // ── /lists/{id}/parents ───────────────────────────────────────────────────

    /**
     * GET /lists/{id}/parents — findParentsByListId
     *
     * @param id     list identifier
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/lists/{id}/parents")
    Response findParentsByListId(
            @PathParam("id")      String id,
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    // ── /lists/{id}/reactions ─────────────────────────────────────────────────

    /**
     * POST /lists/{id}/reactions — createReactionByListId
     *
     * @param id   list identifier
     * @param body reaction payload
     */
    @POST
    @Path("/lists/{id}/reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createReactionByListId(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /lists/{id}/reactions — updateReactionsByListId
     *
     * @param id    list identifier
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     * @param body  partial update payload
     */
    @PATCH
    @Path("/lists/{id}/reactions")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateReactionsByListId(
            @PathParam("id")     String id,
            @QueryParam("set")   String set,
            @QueryParam("where") String where,
            Object body);

    /**
     * GET /lists/{id}/reactions — findReactionsByListId
     *
     * @param id     list identifier
     * @param set    JSON-encoded set filter
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/lists/{id}/reactions")
    Response findReactionsByListId(
            @PathParam("id")      String id,
            @QueryParam("set")    String set,
            @QueryParam("filter") String filter);

    /**
     * DELETE /lists/{id}/reactions — deleteReactionsByListId
     *
     * @param id    list identifier
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     */
    @DELETE
    @Path("/lists/{id}/reactions")
    Response deleteReactionsByListId(
            @PathParam("id")     String id,
            @QueryParam("set")   String set,
            @QueryParam("where") String where);

    // =========================================================================
    // /relations
    // =========================================================================

    /**
     * GET /relations/count — countRelations
     *
     * @param set         JSON-encoded set filter
     * @param where       JSON-encoded where filter
     * @param listWhere   JSON-encoded list where filter
     * @param listSet     JSON-encoded list set filter
     * @param entityWhere JSON-encoded entity where filter
     * @param entitySet   JSON-encoded entity set filter
     */
    @GET
    @Path("/relations/count")
    Response countRelations(
            @QueryParam("set")         String set,
            @QueryParam("where")       String where,
            @QueryParam("listWhere")   String listWhere,
            @QueryParam("listSet")     String listSet,
            @QueryParam("entityWhere") String entityWhere,
            @QueryParam("entitySet")   String entitySet);

    /**
     * GET /relations — findRelations
     *
     * @param set          JSON-encoded set filter
     * @param filter       JSON-encoded filter object
     * @param listFilter   JSON-encoded list filter object
     * @param listSet      JSON-encoded list set filter
     * @param entityFilter JSON-encoded entity filter object
     * @param entitySet    JSON-encoded entity set filter
     */
    @GET
    @Path("/relations")
    Response findRelations(
            @QueryParam("set")          String set,
            @QueryParam("filter")       String filter,
            @QueryParam("listFilter")   String listFilter,
            @QueryParam("listSet")      String listSet,
            @QueryParam("entityFilter") String entityFilter,
            @QueryParam("entitySet")    String entitySet);

    /**
     * POST /relations — createRelation
     *
     * @param body relation payload
     */
    @POST
    @Path("/relations")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createRelation(Object body);

    /**
     * PATCH /relations — updateRelations
     *
     * @param set   JSON-encoded set filter
     * @param where JSON-encoded where filter
     * @param body  partial update payload
     */
    @PATCH
    @Path("/relations")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateRelations(
            @QueryParam("set")   String set,
            @QueryParam("where") String where,
            Object body);

    // ── /relations/{id} ───────────────────────────────────────────────────────

    /**
     * GET /relations/{id} — findRelationById
     *
     * @param id     relation identifier
     * @param filter JSON-encoded filter object
     */
    @GET
    @Path("/relations/{id}")
    Response findRelationById(
            @PathParam("id")      String id,
            @QueryParam("filter") String filter);

    /**
     * PUT /relations/{id} — replaceRelationById
     *
     * @param id   relation identifier
     * @param body full replacement payload
     */
    @PUT
    @Path("/relations/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response replaceRelationById(
            @PathParam("id") String id,
            Object body);

    /**
     * PATCH /relations/{id} — updateRelationById
     *
     * @param id   relation identifier
     * @param body partial update payload
     */
    @PATCH
    @Path("/relations/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateRelationById(
            @PathParam("id") String id,
            Object body);

    /**
     * DELETE /relations/{id} — deleteRelationById
     *
     * @param id relation identifier
     */
    @DELETE
    @Path("/relations/{id}")
    Response deleteRelationById(@PathParam("id") String id);

    // =========================================================================
    // /ping  &  /system-info
    // =========================================================================

    /**
     * GET /ping — ping
     */
    @GET
    @Path("/ping")
    Response ping();

    /**
     * GET /system-info — getSystemInfo
     */
    @GET
    @Path("/system-info")
    Response getSystemInfo();
}
