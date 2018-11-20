package com.around.volcanoinn.springboot.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.around.volcanoinn.springboot.entity.Booking;
import com.around.volcanoinn.springboot.entity.Campsite;

public interface CampsiteService {

    Optional<Campsite> getCampsiteAvailability(Long campsiteId, Optional<LocalDate> arrivalDate, Optional<LocalDate> departureDate);

    Optional<Campsite> existsCampsite(Long campsiteId);

    void booking(Booking booking, Long campsiteId);

    boolean existsBooking(UUID bookingId);

    void deleteBooking(UUID bookingId);

    void updateBooking(Booking booking, UUID bookingId);

    List<Campsite> getCampsitesAvailability(LocalDate arrivalDate, LocalDate departureDate);

    Booking getBooking(UUID bookingId);

    List<Booking> getAllBookings();

}