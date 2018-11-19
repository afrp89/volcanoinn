package service;

import java.time.LocalDate;
import java.util.List;
import entity.Booking;
import entity.Campsite;

public interface CampsiteService {

    Campsite getCampsiteAvailability(Long campsiteId, LocalDate arrivalDate, LocalDate departureDate);

    boolean existsCampsite(Long campsiteId);

    void booking(Booking booking, Long campsiteId);

    boolean existsBooking(Long bookingId);

    void deleteBooking(Long bookingId);

    void updateBooking(Booking booking, Long bookingId);

    List<Campsite> getCampsitesAvailability(LocalDate arrivalDate, LocalDate departureDate);

    Booking getBooking(Long bookingId);

    List<Booking> getAllBookings();

}