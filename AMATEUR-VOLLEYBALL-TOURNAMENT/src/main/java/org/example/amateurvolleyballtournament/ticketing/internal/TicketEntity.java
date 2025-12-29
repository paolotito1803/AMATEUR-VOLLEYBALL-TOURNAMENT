package org.example.amateurvolleyballtournament.ticketing.internal;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="tickets", schema = "ticketing_module")
@Data
@NoArgsConstructor
public class TicketEntity {
    @Id
    private String id;
    private int numSeat;
    private String match;
    private String userId;

    /*@Enumerated(EnumType.STRING) // JPA: Save the ENUM as text ("")
    private TipologySeat seatType;*/
    /*@ManyToOne
    @JoinColumn(name = "id");
    private UserEntity user;

    @ManyToOne
    @JoinColumn();
    private MatchEntity match;*/

    public TicketEntity(int numSeat, String match, String userId) {
        this.id = UUID.randomUUID().toString();
        this.numSeat = numSeat;
        this.match = match;
        this.userId = userId;
    }
}
