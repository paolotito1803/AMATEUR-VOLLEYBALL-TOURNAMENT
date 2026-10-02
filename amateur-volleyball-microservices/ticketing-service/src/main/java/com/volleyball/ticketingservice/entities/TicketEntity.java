package com.volleyball.ticketingservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {
    @Id
    private String id;
    private int numSeat;
    private String matchId;
    private String userId;

    public TicketEntity(int numSeat, String matchId, String userId) {
        this.id = UUID.randomUUID().toString();
        this.numSeat = numSeat;
        this.matchId = matchId;
        this.userId = userId;
    }
}
