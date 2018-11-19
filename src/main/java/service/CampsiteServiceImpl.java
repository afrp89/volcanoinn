package service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import entity.Booking;
import entity.Campsite;
import repository.BookingRepository;
import repository.CampsiteRepository;
import util.Utilities;

@Service("campsiteService")
public class CampsiteServiceImpl implements CampsiteService {

    private static final Logger logger = LoggerFactory.getLogger(CampsiteServiceImpl.class);
    @Autowired
    CampsiteRepository campsiteRepository;
    @Autowired
    BookingRepository bookingRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Campsite getCampsiteAvailability(Long campsiteId, LocalDate fromDate, LocalDate toDate) {

        LocalDate today = LocalDate.now();

        if (fromDate != null && toDate != null) {
            if (fromDate.isEqual(toDate))
                throw new RuntimeException("fromDate and toDate could not be the same");
            if (fromDate.isBefore(today) || fromDate.isEqual(today))
                throw new RuntimeException("fromDate could not be equal or before current date");
            if (toDate.isBefore(today) || toDate.isEqual(today))
                throw new RuntimeException("toDate could not be equal or before current date");
            if (toDate.isBefore(fromDate))
                throw new RuntimeException("toDate should be after fromDate");
        } else {
            if (fromDate != null ^ toDate != null) {
                throw new RuntimeException("Cannot send only one date");
            } else {
                fromDate = today.plusDays(Utilities.MINIMUM_DAYS_AHEAD);
                toDate = fromDate.plusMonths(Utilities.MAXIMUM_MONTHS_AHEAD);
            }
        }

        logger.debug("From date => " + fromDate);
        logger.debug("To date => " + toDate);

        Campsite campsite = campsiteRepository.findById(campsiteId).get();
        if (campsite != null) {
            campsite.setAvailableDays(new HashSet<Long>());
            for (LocalDate i = fromDate; i.isBefore(toDate); i = i.plusDays(1)) {
                if (campsite.getBookings().isEmpty()) {
                    Long availDay = Instant.from(i.atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
                    campsite.getAvailableDays().add(availDay);
                    logger.debug("Available date => " + i);
                } else {
                    for (Booking r : campsite.getBookings()) {
                        if (!dateOverlapsWithBooking(i, r)) {
                            Long availDay = Instant.from(i.atStartOfDay(ZoneId.systemDefault()).toInstant()).toEpochMilli();
                            campsite.getAvailableDays().add(availDay);
                            logger.debug("Available date => " + i);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return campsite;
    }

    @Override
    public boolean existsCampsite(Long campsiteId) {
        return campsiteRepository.existsById(campsiteId);
    }

    @Override
    public synchronized void booking(Booking booking, Long campsiteId) {
        validateCreateBooking(booking, campsiteId);
        booking.setCampsite(campsiteRepository.findById(campsiteId).get());
        bookingRepository.save(booking);
    }

    @Override
    public boolean existsBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).isPresent();
    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public void updateBooking(Booking booking, Long bookingId) {
        validateUpdateBooking(booking, bookingId);
        Booking oldBooking = bookingRepository.findById(bookingId).get();
        oldBooking.setEmail(booking.getEmail());
        oldBooking.setFullName(booking.getFullName());
        oldBooking.setArrivalDate(booking.getArrivalDate());
        oldBooking.setDepartureDate(booking.getDepartureDate());
        bookingRepository.save(oldBooking);
    }

    @Override
    public List<Campsite> getCampsitesAvailability(LocalDate arrivalDate, LocalDate departureDate) {
        List<Campsite> campsites = (List<Campsite>) campsiteRepository.findAll();
        for (Campsite c : campsites) {
            getCampsiteAvailability(c.getId(), arrivalDate, departureDate);
        }
        return campsites;
    }

    @Override
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).get();
    }

    @Override
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<Booking>();
        for (Booking r : bookingRepository.findAll()) {
            bookings.add(r);
        }
        return bookings;
    }

    private boolean dateOverlapsWithBooking(LocalDate i, Booking r) {
        LocalDate arrivalDate = Utilities.getDateFromUnixTime(r.getArrivalDate());
        LocalDate departureDate = Utilities.getDateFromUnixTime(r.getDepartureDate());
        if ((i.isEqual(arrivalDate) || i.isAfter(arrivalDate)) && i.isBefore(departureDate))
            return true;
        else
            return false;
    }

    private void validateDateParameters(LocalDate arrivalDate, LocalDate departureDate) {
        LocalDate today = LocalDate.now();

        /* Arrival date cannot be before tomorrow or after one month from today */
        if (arrivalDate.isBefore(today.plusDays(Utilities.MINIMUM_DAYS_AHEAD)) ||
            arrivalDate.isAfter(today.plusMonths(Utilities.MAXIMUM_MONTHS_AHEAD))) {
            throw new RuntimeException("Invalid arrival date => " + arrivalDate);
        }

        /* Departure date cannot be before arrival date */
        if (departureDate.isBefore(arrivalDate)) {
            throw new RuntimeException("Departure date cannot be before arrival date");
        }

        /* Bookings can last three days maximum */
        if (Period.between(arrivalDate, departureDate).getDays() > Utilities.MAXIMUM_BOOKING_DAYS) {
            throw new RuntimeException("Cannot booking for more than " + Utilities.MAXIMUM_BOOKING_DAYS + " days");
        }
    }

    private void validateCreateBooking(Booking booking, Long campsiteId) {
        LocalDate arrivalDate = Utilities.getDateFromUnixTime(booking.getArrivalDate());
        LocalDate departureDate = Utilities.getDateFromUnixTime(booking.getDepartureDate());

        validateDateParameters(arrivalDate, departureDate);

        /* Check if new booking overlaps to existing one */
        List<Booking> bookings = campsiteRepository.findById(campsiteId).get().getBookings();
        validateOverlappingBookings(arrivalDate, departureDate, bookings);
    }

    private void validateOverlappingBookings(LocalDate arrivalDate, LocalDate departureDate, List<Booking> bookings) {

        /* Set containing actual bookingd days */
        Set<LocalDate> daysBookingd = new HashSet<LocalDate>();
        for (Booking r : bookings) {
            LocalDate rArrivalDate = Utilities.getDateFromUnixTime(r.getArrivalDate());
            LocalDate rDepartureDate = Utilities.getDateFromUnixTime(r.getDepartureDate());
            for (LocalDate i = rArrivalDate; i.isBefore(rDepartureDate); i = i.plusDays(1)) {
                daysBookingd.add(i);
            }
        }
        /* Check if days of the new booking are contained in the bookingd days */
        for (LocalDate i = arrivalDate; i.isBefore(departureDate); i = i.plusDays(1)) {
            if (daysBookingd.contains(i)) {
                throw new RuntimeException("New booking overlaps with existing one");
            }
        }
    }

    private void validateUpdateBooking(Booking booking, Long bookingId) {
        LocalDate arrivalDate = Utilities.getDateFromUnixTime(booking.getArrivalDate());
        LocalDate departureDate = Utilities.getDateFromUnixTime(booking.getDepartureDate());

        validateDateParameters(arrivalDate, departureDate);

        /* Retrieve all bookings except the one to be updated */
        List<Booking> bookings = entityManager.createQuery("SELECT rs FROM Booking rs WHERE campsite_id = :campsite_id AND id != :booking_id", Booking.class)
            .setParameter("campsite_id", bookingRepository.findById(bookingId).get().getCampsite().getId())
            .setParameter("booking_id", bookingId)
            .getResultList();

        /* Checks if the updated booking overlaps with existing ones */
        validateOverlappingBookings(arrivalDate, departureDate, bookings);
    }
}