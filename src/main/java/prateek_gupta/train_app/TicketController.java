package prateek_gupta.train_app;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
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

    @PostMapping("/purchase_ticket")
    public ResponseEntity<JSONObject> purchaseTicket(@RequestBody PurchaseRequest request) {
        JSONObject response=new JSONObject();
        try{
            if (request == null || StringUtils.isBlank(request.getFirstName()) ||
                    StringUtils.isBlank(request.getLastName()) ||
                    StringUtils.isBlank(request.getEmail())) {
                response.put("message","Please provide valid arguments");
                return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
            }
            response=ticketService.purchaseTicket(request, "London", "France", 20.0);
        }catch (Exception e){
            response.put("message","An error occurred");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/get_ticket_details")
    public ResponseEntity<JSONObject> getTicket(@RequestParam Long ticketId) {
        JSONObject response=new JSONObject();
        try{
            if (ticketId==null||ticketId<=0) {
                response.put("message", "Please provide valid arguments");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response=ticketService.getTicket(ticketId);
        }catch (Exception e){
            response.put("message","An error occurred");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/get_user_section_mapping")
    public ResponseEntity<JSONObject>  getUsersBySection(@RequestParam String section) {
        JSONObject response=new JSONObject();
        try{
            if (StringUtils.isBlank(section)) {
                response.put("message", "Please provide valid arguments");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response=ticketService.getUsersBySection(section);
        }catch (Exception e){
            response.put("message","An error occurred");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/remove_user")
    public ResponseEntity<JSONObject> removeUser(@RequestParam String userEmail) {
        JSONObject response=new JSONObject();
        try{
            if (StringUtils.isBlank(userEmail)) {
                response.put("message", "Please provide valid arguments");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response=ticketService.removeUser(userEmail);
        }catch (Exception e){
            response.put("message","An error occurred");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/modify_seat")
    public ResponseEntity<JSONObject> modifyUserSeat(
            @RequestBody SeatModificationRequest request) {
        JSONObject response=new JSONObject();
        try{
            if (request.getTicketId() == null || request.getTicketId() <= 0 ||
                    StringUtils.isBlank(request.getSeatNumber())
                    || StringUtils.isBlank(request.getSection())) {
                response.put("message", "Please provide valid arguments");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            response=ticketService.modifyUserSeat(request);
        }catch (Exception e){
            response.put("message","An error occurred");
            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}





