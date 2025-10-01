package app.controllers;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import io.restassured.response.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HotelApiTest {

    private long id;
    EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();
    EntityManager em = emf.createEntityManager();

    @BeforeAll
    static void setup() {
        ApplicationConfig.startServer(7007);
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7007;
        RestAssured.basePath = "/api/v1/hotel";
    }

    // Lav en @BeforeEach, der starter test db'en i en ønsket tilstand. @AfterEach, teardown
    @BeforeEach
    void setUp() {
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        em.close();
    }

    @Test
    void createHotel() {
        String hotel = """
            {
              "name": "Test Hotel",
              "address": "Test Street"
            }
            """;

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(hotel)
                        .when()
                        .post("/")
                        .then()
                        .statusCode(201)
                        .log().all()
                        .extract().response();

        id = response.jsonPath().getLong("id");
        System.out.println("Extracted ID: " + id);
    }


    @Test
    void getAllHotels() {
        when()
                .get("/")
                .then()
                .statusCode(200)
                .log().all()
                .body("size()", greaterThan(0));
    }

    @Test
    void getHotelById() {
        when()
                .get("/" + id)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo((int) id))
                .body("name", equalTo("Test Hotel"));

    }


    @Test
    void updateHotel() {
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
    void addRoom(){
        String room = """
        {
          "number": 456,
          "price": 150.0
        }
        """;

        // Add room to the hotel
        given()
                .contentType(ContentType.JSON)
                .body(room)
                .when()
                .post("/" + id + "/room")
                .then()
                .statusCode(201 & 200) //
                .log().all()
                .body("rooms.size()", greaterThan(0))
                .body("rooms.find { it.number == 456 }.price", equalTo(150.0f));
    }

    @Test
    void getRoomsForHotel() {
        when()
                .get("/" + id + "/rooms")
                .then()
                .statusCode(200)
                .body("$", notNullValue());

    }


    @Test
    void deleteHotel() {
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