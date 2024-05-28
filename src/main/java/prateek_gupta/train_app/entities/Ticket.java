package prateek_gupta.train_app.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ticket {
    private Long ticketId;
    private String fromLocation;
    private String toLocation;
    private double price;
    private User user;
    private Seat seat;
}

