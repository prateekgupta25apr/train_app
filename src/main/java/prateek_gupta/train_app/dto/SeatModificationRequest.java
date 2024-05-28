package prateek_gupta.train_app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatModificationRequest {
    private Long ticketId;
    private String section;
    private String seatNumber;

}
