package app.controllers;

import app.daos.HotelDAO;
import app.dtos.HotelDTO;
import app.dtos.RoomDTO;
import app.entities.Hotel;
import app.entities.Room;
import app.mappers.HotelRoomMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class HotelController {

    private final HotelDAO hotelDAO = new HotelDAO();

    public void getAllHotels(Context ctx) {
        List<HotelDTO> hotelDTOs = hotelDAO.getAllHotels().stream()
                .map(HotelRoomMapper::toDTO)
                .collect(Collectors.toList());
        ctx.json(hotelDTOs);
    }

    public void getHotelById(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Hotel hotel = hotelDAO.getHotelById(id);
        if (hotel != null) {
            ctx.json(HotelRoomMapper.toDTO(hotel));
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("Hotel not found");
        }
    }

    public void createHotel(Context ctx) {
        HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
        Hotel hotel = HotelRoomMapper.toEntity(hotelDTO);
        Hotel created = hotelDAO.createHotel(hotel);
        ctx.status(HttpStatus.CREATED).json(HotelRoomMapper.toDTO(created));
    }

    public void updateHotel(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        HotelDTO hotelDTO = ctx.bodyAsClass(HotelDTO.class);
        Hotel hotel = hotelDAO.getHotelById(id);
        if (hotel == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Hotel not found");
            return;
        }
        hotel.setName(hotelDTO.getName());
        hotel.setAddress(hotelDTO.getAddress());
        Hotel updated = hotelDAO.updateHotel(hotel);
        ctx.json(HotelRoomMapper.toDTO(updated));
    }

    public void deleteHotel(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Hotel deleted = hotelDAO.deleteHotel(id);
        if (deleted != null) {
            ctx.json(HotelRoomMapper.toDTO(deleted));
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("Hotel not found");
        }
    }

    public void addRoom(Context ctx) {
        long hotelId = Long.parseLong(ctx.pathParam("hotelId"));
        RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Hotel not found");
            return;
        }
        Room room = HotelRoomMapper.toEntity(roomDTO);
        Hotel updated = hotelDAO.addRoom(hotel, room);
        ctx.json(HotelRoomMapper.toDTO(updated));
    }

    public void removeRoom(Context ctx) {
        long hotelId = Long.parseLong(ctx.pathParam("hotelId"));
        RoomDTO roomDTO = ctx.bodyAsClass(RoomDTO.class);
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Hotel not found");
            return;
        }
        Room room = HotelRoomMapper.toEntity(roomDTO);
        Hotel updated = hotelDAO.removeRoom(hotel, room);
        ctx.json(HotelRoomMapper.toDTO(updated));
    }

    public void getRoomsForHotel(Context ctx) {
        long hotelId = Long.parseLong(ctx.pathParam("id"));
        Hotel hotel = hotelDAO.getHotelById(hotelId);
        if (hotel == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Hotel not found");
            return;
        }
        List<RoomDTO> roomDTOs = hotelDAO.getRoomsForHotel(hotel).stream()
                .map(HotelRoomMapper::toDTO)
                .collect(Collectors.toList());
        ctx.json(roomDTOs);
    }
}