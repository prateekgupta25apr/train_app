package prateek_gupta.train_app.service;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import prateek_gupta.train_app.dto.PurchaseRequest;
import prateek_gupta.train_app.dto.SeatModificationRequest;
import prateek_gupta.train_app.entities.Seat;
import prateek_gupta.train_app.entities.Ticket;
import prateek_gupta.train_app.entities.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private int sectionAAllocatedSeatsCount=0;
    private int sectionBAllocatedSeatsCount=0;
    private final List<User> users = new ArrayList<>();
    private final Map<Long, Ticket> ticketMap = new HashMap<>();
    private final AtomicLong userIdCounter = new AtomicLong();
    private final AtomicLong ticketIdCounter = new AtomicLong();

    public JSONObject purchaseTicket(PurchaseRequest request, String from, String to,
                                     double price) {
        JSONObject result=new JSONObject();

        Seat seat = allocateSeat();
        if (seat==null){
            result.put("message","Sorry!! The train is full.");
            return result;
        }

        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketIdCounter.incrementAndGet());
        ticket.setUser(getUser(request));
        ticket.setFromLocation(from);
        ticket.setToLocation(to);
        ticket.setPrice(price);
        ticket.setSeat(seat);
        ticketMap.put(ticket.getTicketId(), ticket);
        result.put("message","Ticket booked successfully");
        result.put("ticket_details",ticket);

        return result;
    }

    public JSONObject getTicket(Long id) {
        JSONObject response=new JSONObject();
        Ticket ticket=ticketMap.getOrDefault(id,null);
        if (ticket!=null) {
            response.put("message","Ticket details fetched successfully");
            response.put("ticket_details",ticket);
        }
        else {
            response.put("message","Ticket not found");
        }
        return response;
    }

    public JSONObject getUsersBySection(String section) {
        JSONObject response=new JSONObject();
        response.put("message","Successfully fetched user for the section "+section);
        response.put("user_section_mapping",ticketMap.values().stream()
                .filter(ticket -> section.equals(ticket.getSeat().getSection()))
                .map(Ticket::getUser)
                .collect(Collectors.toList()));
        return response;
    }

    public JSONObject removeUser(String userEmail) {
        if (userEmail != null && !userEmail.isEmpty()) {
            // Cancelling Tickets
            for (Ticket ticket : ticketMap.values())
                if (ticket.getUser().getEmail().equals(userEmail))
                    ticketMap.remove(ticket.getTicketId());

            // Removing user from DB
            users.remove(new User(userEmail));
        }
        JSONObject response=new JSONObject();
        response.put("message","Successfully delete the user");
        return response;
    }

    public JSONObject modifyUserSeat(SeatModificationRequest request) {
        Ticket ticket = ticketMap.get(request.getTicketId());
        if (ticket != null) {
            Seat seat = ticket.getSeat();
            seat.setSection(request.getSection());
            seat.setSeatNumber(request.getSeatNumber());
        }
        JSONObject response=new JSONObject();
        response.put("message","Successfully modified the ticket");
        return response;
    }

    private Seat allocateSeat() {
        int seatsPerSection = 2;
        if (sectionAAllocatedSeatsCount< seatsPerSection){
            Seat seat = new Seat();
            seat.setSection("A");
            sectionAAllocatedSeatsCount++;
            seat.setSeatNumber(String.valueOf(sectionAAllocatedSeatsCount));
            return seat;
        } else if (sectionAAllocatedSeatsCount == seatsPerSection &&
                sectionBAllocatedSeatsCount < seatsPerSection){
            Seat seat = new Seat();
            seat.setSection("B");
            sectionBAllocatedSeatsCount++;
            seat.setSeatNumber(String.valueOf(sectionBAllocatedSeatsCount));
            return seat;
        }

        return null;
    }

    User getUser(PurchaseRequest request){
        User user = new User(request.getEmail());

        if (!users.contains(user)) {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setUserId(userIdCounter.incrementAndGet());
            users.add(user);
        }else
            user=users.get(users.indexOf(user));


        return user;
    }
}

