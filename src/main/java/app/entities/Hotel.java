package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "address", length = 100, nullable = false)
    private String address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Room> rooms;


    public void addRoom(Room room) {
        rooms.add(room);
        room.setHotel(this); // Make sure the Room's hotel field is set
    }

    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setHotel(null); // Unset the Room's hotel field
    }
}
