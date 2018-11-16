package repository;

import org.springframework.data.repository.CrudRepository;
import entity.Booking;

public interface BookingRepository extends CrudRepository<Booking, java.util.UUID> {

}
