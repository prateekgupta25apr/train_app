package prateek_gupta.train_app.service;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import prateek_gupta.train_app.dto.PurchaseRequest;
import prateek_gupta.train_app.dto.SeatModificationRequest;
import prateek_gupta.train_app.entities.Seat;
import prateek_gupta.train_app.entities.Ticket;
import prateek_gupta.train_app.entities.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TicketServiceImpl implements TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);

    private final List<User> users = new ArrayList<>();
    private final Map<Long, Ticket> ticketMap = new HashMap<>();
    private final List<String> availableSeats=new ArrayList<>();
    private final AtomicLong userIdCounter = new AtomicLong();
    private final AtomicLong ticketIdCounter = new AtomicLong();

    public TicketServiceImpl() {
        int seatsPerSection = 2;
        for (String section: Arrays.asList("A","B"))
            for (int i=1;i<=seatsPerSection;i++)
                availableSeats.add(section+"-"+i);
    }

    public JSONObject purchaseTicket(PurchaseRequest request, String from, String to,
                                     double price) throws Exception {
        JSONObject result=new JSONObject();
        try{
            Seat seat = allocateSeat();
            if (seat == null) {
                result.put("message", "Sorry!! The train is full.");
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
            result.put("message", "Ticket booked successfully");
            result.put("ticket_details", ticket);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception();
        }

        return result;
    }

    public JSONObject getTicket(Long id) throws Exception {
        JSONObject response=new JSONObject();
        try{
            Ticket ticket = ticketMap.getOrDefault(id, null);
            if (ticket != null) {
                response.put("message", "Ticket details fetched successfully");
                response.put("ticket_details", ticket);
            } else {
                response.put("message", "Ticket not found");
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception();
        }
        return response;
    }

    public JSONObject getUsersBySection(String section) throws Exception {
        JSONObject response=new JSONObject();
        try{
            response.put("message", "Successfully fetched user for the section " + section);
            response.put("user_section_mapping", ticketMap.values().stream()
                    .filter(ticket -> section.equals(ticket.getSeat().getSection()))
                    .map(Ticket::getUser).toList());
        }
        catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception();
        }
        return response;
    }

    public JSONObject removeUser(String userEmail) throws Exception {
        JSONObject response=new JSONObject();
        User user=new User(userEmail);
        try{
            if (users.contains(user)) {
                // Cancelling Tickets
                for (Ticket ticket : new ArrayList<>(ticketMap.values()))
                    if (ticket.getUser().getEmail().equals(userEmail)) {
                        availableSeats.add(ticket.getSeat().getSection() + "-" +
                                ticket.getSeat().getSeatNumber());
                        ticketMap.remove(ticket.getTicketId());
                    }

                // Removing user from DB
                users.remove(user);

                response.put("message","Successfully delete the user");
            }
            else
                response.put("message","User not found");
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception();
        }
        return response;
    }

    public JSONObject modifyUserSeat(SeatModificationRequest request) throws Exception {
        JSONObject response=new JSONObject();
        try{
            Ticket ticket = ticketMap.get(request.getTicketId());
            if (ticket != null) {
                List<Seat> occupiedSeats = ticketMap.values().stream()
                        .filter(tkt ->
                                tkt.getSeat().getSeatNumber().equals(request.getSeatNumber()) &&
                                        tkt.getSeat().getSection().equals(request.getSection()))
                        .map(Ticket::getSeat)
                        .toList();
                if (occupiedSeats.isEmpty()) {
                    Seat seat = ticket.getSeat();
                    seat.setSection(request.getSection());
                    seat.setSeatNumber(request.getSeatNumber());
                    response.put("message", "Successfully modified the ticket");
                } else
                    response.put("message", "Requested seat is already booked");
            }
            else {
                response.put("message", "Invalid ticket id passed");
                throw new Exception();
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception();
        }
        return response;
    }

    private Seat allocateSeat() throws Exception {
        try{
            if (!availableSeats.isEmpty()){
                String seatNumber=availableSeats.get(0);
                String[] seatDetails=seatNumber.split("-");
                Seat seat = new Seat();
                seat.setSection(seatDetails[0]);
                seat.setSeatNumber(seatDetails[1]);
                availableSeats.remove(0);
                return seat;
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception();
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

