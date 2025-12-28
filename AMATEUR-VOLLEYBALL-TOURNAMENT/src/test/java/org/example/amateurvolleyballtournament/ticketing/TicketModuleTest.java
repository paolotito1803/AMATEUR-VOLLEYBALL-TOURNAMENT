package org.example.amateurvolleyballtournament.ticketing;

import org.example.amateurvolleyballtournament.tournament.MatchDTO;
import org.example.amateurvolleyballtournament.tournament.TournamentService;
import org.example.amateurvolleyballtournament.users.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationModuleTest
public class TicketModuleTest {
    @Autowired
    TicketService ticketService;

    @MockitoBean
    UsersService usersService;
    @MockitoBean
    TournamentService tournamentService;

    @Test
    void shouldAddTicketSuccessfully(){
        String userId = "user_" + UUID.randomUUID();
        String matchId = "match_" + UUID.randomUUID();

        // Mock the Tournament Service to say there are seats
        MatchDTO mockMatch = new MatchDTO(matchId, "Stadium", 10, LocalDateTime.parse("2025-01-01T00:00:00"), "Ref", 1, 0, "A", "", 0, 0, "", "");
        when(tournamentService.getMatchInformation(matchId)).thenReturn(Optional.of(mockMatch));
        when(tournamentService.reserveSeat(matchId)).thenReturn(true);


        // Real TicketService action
        ticketService.addTicket(13, userId, matchId);

        List<TicketDTO> tickets = ticketService.getAllTicketsByUserId(userId);
        assertThat(tickets).hasSize(1);
        assertThat(tickets.getFirst().numSeat()).isEqualTo(13);
    }

    @Test
    void shouldNotAddTicketSuccessfully(){
        String userId = "user_" + UUID.randomUUID();
        String match = "match_" + UUID.randomUUID();

        assertThatThrownBy(()->ticketService.addTicket(0,userId,match))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotAddTicketSuccessfullyIfNoSeatsAvailable(){
        String userId = "user_" + UUID.randomUUID();
        String matchId = "match_" + UUID.randomUUID();

        // Mock the Tournament Service to say 0 seats left
        MatchDTO fullMatch = new MatchDTO(matchId, "Stadium", 0, LocalDateTime.parse("2025-01-01T00:00:00"), "Ref", 1, 0, "A", "", 0, 0, "","");
        when(tournamentService.getMatchInformation(matchId)).thenReturn(Optional.of(fullMatch));
        when(tournamentService.reserveSeat(matchId)).thenReturn(false);

        // Real TicketService action
        assertThatThrownBy(() -> ticketService.addTicket(13, userId, matchId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldNotAddTicketSuccessfullyIfNoMatch(){
        String userId = "user_" + UUID.randomUUID();

        assertThatThrownBy(()->ticketService.addTicket(13,userId,""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
