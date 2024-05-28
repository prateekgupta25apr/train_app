package prateek_gupta.train_app;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prateek_gupta.train_app.dto.PurchaseRequest;
import prateek_gupta.train_app.dto.SeatModificationRequest;
import prateek_gupta.train_app.service.TicketService;

@RestController
@RequestMapping("/")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public ResponseEntity<JSONObject> purchaseTicket(@RequestBody PurchaseRequest request) {
        JSONObject response=ticketService.purchaseTicket(request, "London", "France", 20.0);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/get_ticket_details")
    public ResponseEntity<JSONObject> getTicket(@RequestParam Long ticketId) {
        return new ResponseEntity<>(ticketService.getTicket(ticketId),HttpStatus.OK);
    }

    @GetMapping("/get_user_section_mapping")
    public ResponseEntity<JSONObject>  getUsersBySection(@RequestParam String section) {
        return new ResponseEntity<>(ticketService.getUsersBySection(section),HttpStatus.OK);
    }

    @DeleteMapping("/remove_user")
    public ResponseEntity<JSONObject>  removeUser(@RequestBody String userEmail) {
        return new ResponseEntity<>(ticketService.removeUser(userEmail),HttpStatus.OK);
    }

    @PutMapping("/modify_seat")
    public ResponseEntity<JSONObject>  modifyUserSeat(@RequestBody SeatModificationRequest request) {
        return new ResponseEntity<>(ticketService.modifyUserSeat(request),HttpStatus.OK);
    }
}





