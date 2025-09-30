package app.daos;

import app.entities.Hotel;
import app.entities.Room;
import app.resources.HotelResource;
import app.config.HibernateConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class HotelDAO implements HotelResource {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    @Override
    public List<Hotel> getAllHotels() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT h FROM Hotel h LEFT JOIN FETCH h.rooms", Hotel.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel getHotelById(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Hotel> result = em.createQuery(
                            "SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms WHERE h.id = :id", Hotel.class)
                    .setParameter("id", id)
                    .getResultList();
            return result.isEmpty() ? null : result.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(hotel);
            em.getTransaction().commit();
            return hotel;
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel updateHotel(Hotel hotel) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Hotel merged = em.merge(hotel);
            em.getTransaction().commit();
            return merged;
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel deleteHotel(long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Hotel hotel = em.find(Hotel.class, id);
            if (hotel != null) {
                em.remove(hotel);
            }
            em.getTransaction().commit();
            return hotel;
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel addRoom(Hotel hotel, Room room) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Hotel managedHotel = em.merge(hotel);
            managedHotel.addRoom(room); // assumes addRoom sets up both sides of the relationship
            em.persist(room);
            em.getTransaction().commit();
            return managedHotel;
        } finally {
            em.close();
        }
    }

    @Override
    public Hotel removeRoom(Hotel hotel, Room room) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Hotel managedHotel = em.merge(hotel);
            managedHotel.removeRoom(room);
            em.remove(em.contains(room) ? room : em.merge(room));
            em.getTransaction().commit();
            return managedHotel;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Room> getRoomsForHotel(Hotel hotel) {
        EntityManager em = emf.createEntityManager();
        try {
            Hotel h = em.createQuery(
                            "SELECT h FROM Hotel h LEFT JOIN FETCH h.rooms WHERE h.id = :id", Hotel.class
                    ).setParameter("id", hotel.getId())
                    .getSingleResult();

            return h.getRooms();
        } finally {
            em.close();
        }
    }
}