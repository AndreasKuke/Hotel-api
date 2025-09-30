package app.resources;

import app.entities.Hotel;
import app.entities.Room;

import java.util.List;

public interface HotelResource {
    List<Hotel> getAllHotels();
    Hotel getHotelById(long id);
    Hotel createHotel(Hotel hotel);
    Hotel updateHotel(Hotel hotel);
    Hotel deleteHotel(long id);
    Hotel addRoom(Hotel hotel, Room room);
    Hotel removeRoom(Hotel hotel, Room room);
    List<Room> getRoomsForHotel(Hotel hotel);
}