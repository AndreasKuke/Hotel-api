package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelApiTest {

    private EntityManagerFactory emf;

    @BeforeAll
    void setup() {
        ApplicationConfig.startServer(7007);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7007;
        RestAssured.basePath = "/api/v1/hotel";
        emf = HibernateConfig.getEntityManagerFactoryForTest();
    }

    @AfterAll
    void tearDownAll() {
        if (emf != null && emf.isOpen()) emf.close();
    }

    // Utility method to create a hotel and return its ID
    long createTestHotel(String name, String address) {
        String hotel = String.format("""
            {
              "name": "%s",
              "address": "%s"
            }
            """, name, address);

        return given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when()
                .post("/")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    void createHotel() {
        String hotel = """
            {
              "name": "Test Hotel",
              "address": "Test Street"
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(hotel)
                .when()
                .post("/")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Hotel"))
                .body("address", equalTo("Test Street"));
    }

    @Test
    void getAllHotels() {
        // Ensure at least one hotel exists
        createTestHotel("AllHotels Hotel", "Some Street");

        when()
                .get("/")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void getHotelById() {
        long id = createTestHotel("Hotel By ID", "Id Street");

        when()
                .get("/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo((int) id))
                .body("name", equalTo("Hotel By ID"));
    }

    @Test
    void updateHotel() {
        long id = createTestHotel("Old Name", "Old Address");

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "name": "Updated Hotel",
                  "address": "Updated Street"
                }
            """)
                .when()
                .put("/" + id)
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Hotel"))
                .body("address", equalTo("Updated Street"));
    }

    @Test
    void addRoom() {
        long id = createTestHotel("Room Hotel", "Room Street");
        String room = """
        {
          "number": 456,
          "price": 150.0
        }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(room)
                .when()
                .post("/" + id + "/room")
                .then()
                .statusCode(anyOf(equalTo(201), equalTo(200)))
                .body("rooms.size()", greaterThan(0))
                .body("rooms.find { it.number == 456 }.price", equalTo(150.0f));
    }

    @Test
    void getRoomsForHotel() {
        long id = createTestHotel("RoomsFetchHotel", "RoomsFetchStreet");
        String room = """
        {
          "number": 789,
          "price": 180.0
        }
        """;
        // Add a room so rooms endpoint is not empty
        given()
                .contentType(ContentType.JSON)
                .body(room)
                .when()
                .post("/" + id + "/room")
                .then()
                .statusCode(anyOf(equalTo(201), equalTo(200)));

        when()
                .get("/" + id + "/rooms")
                .then()
                .statusCode(200)
                .body("$", notNullValue());
    }

    @Test
    void deleteHotel() {
        long id = createTestHotel("Delete Hotel", "Delete Street");

        when()
                .delete("/" + id)
                .then()
                .statusCode(200);

        // Confirm it's deleted
        when()
                .get("/" + id)
                .then()
                .statusCode(404);
    }
}