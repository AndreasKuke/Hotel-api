package app.mappers;

import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;

import java.util.List;
import java.util.stream.Collectors;

public class HotelRoomMapper {

    // Overloaded methods, one for Hotel and one for Rooms
    public static HotelDTO toDTO(Hotel hotel){
        List<RoomDTO> roomDTOs = hotel.rooms == null ? null :
                hotel.rooms.stream().map(HotelRoomMapper::toDTO).collect(Collectors.toList());
        return new HotelDTO(hotel.getId(), hotel.getName(), hotel.getAddress(), roomDTOs);
    }
    public static RoomDTO toDTO(Room room){
        return new RoomDTO(room.getId(), room.getHotel() != null ? room.getHotel().getId() : null, room.getNumber(), room.getPrice());
    }

    // We do the same thing converting from DTO to Entity
    public static Hotel toEntity(HotelDTO dto){
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setAddress(dto.getAddress());
        if (dto.getRooms() != null) {
            hotel.rooms = dto.getRooms().stream().map(HotelRoomMapper::toEntity).collect(Collectors.toList());
            hotel.rooms.forEach(room -> room.setHotel(hotel));
        }
        return hotel;
    }

    public static Room toEntity(RoomDTO roomDTO){
        Room room = new Room();
        room.setNumber(roomDTO.getNumber());
        room.setPrice(roomDTO.getPrice());
        return room;
    }
}
