package app.routes;

import app.controllers.HotelController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class HotelRoutes {

    private final HotelController hotelController =  new HotelController();



    public EndpointGroup getRoutes() {
        return () -> {
            // Hotel CRUD
            get("/", hotelController::getAllHotels);
            get("/{id}", hotelController::getHotelById);
            post("/", hotelController::createHotel);
            put("/{id}", hotelController::updateHotel);
            delete("/{id}", hotelController::deleteHotel);

            // Room CRUD
            get("/{id}/rooms", hotelController::getRoomsForHotel);
            post("/{hotelId}/room", hotelController::addRoom);
            delete("/{hotelId}/room", hotelController::removeRoom);

        };
    }
}
