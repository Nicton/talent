package api;

import model.Player;
import model.PlayerCreateRequest;
import model.PlayerLookupRequest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class PlayersApi extends ApiClient {
    private static final String CREATE_PATH = "/api/automationTask/create";
    private static final String GET_ONE_PATH = "/api/automationTask/getOne";
    private static final String GET_ALL_PATH = "/api/automationTask/getAll";
    private static final String DELETE_ONE_PATH = "/api/automationTask/deleteOne/{id}";

    private final String token;

    public PlayersApi(String token) {
        this.token = token;
    }

    public Response createRaw(PlayerCreateRequest request) {
        return givenBearer(token).body(request).when().post(CREATE_PATH);
    }

    public Player create(PlayerCreateRequest request) {
        return createRaw(request)
                .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/player.schema.json"))
                .extract().as(Player.class);
    }

    public Response getOneRaw(PlayerLookupRequest request) {
        return givenBearer(token).body(request).when().post(GET_ONE_PATH);
    }

    public Player getOne(PlayerLookupRequest request, int expectedStatusCode) {
        return getOneRaw(request)
                .then()
                .statusCode(expectedStatusCode)
                .body(matchesJsonSchemaInClasspath("schemas/player.schema.json"))
                .extract().as(Player.class);
    }

    public Response getAllRaw() {
        return givenBearer(token).when().get(GET_ALL_PATH);
    }

    public List<Player> getAll() {
        Response response = getAllRaw().then().statusCode(200).extract().response();
        Object root = response.jsonPath().get("$");
        if (root instanceof List<?>) {
            return response.as(new TypeRef<>() {
            });
        }
        return List.of(response.as(Player.class));
    }

    public Response deleteOneRaw(int id) {
        return givenBearer(token).pathParam("id", id).when().delete(DELETE_ONE_PATH);
    }

    public Player deleteOne(int id) {
        return deleteOneRaw(id)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/player.schema.json"))
                .extract().as(Player.class);
    }
}
