package prateek_gupta.train_app.service;

import net.sf.json.JSONObject;
import prateek_gupta.train_app.dto.PurchaseRequest;
import prateek_gupta.train_app.dto.SeatModificationRequest;

public interface TicketService {
    JSONObject purchaseTicket(PurchaseRequest request, String from, String to,
                                     double price) throws Exception;

    JSONObject getTicket(Long id) throws Exception;

    JSONObject getUsersBySection(String section) throws Exception;

    JSONObject removeUser(String userEmail) throws Exception;

    JSONObject modifyUserSeat(SeatModificationRequest request) throws Exception;
}
