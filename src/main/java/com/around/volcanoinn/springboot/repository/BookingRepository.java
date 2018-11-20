package com.around.volcanoinn.springboot.repository;

import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import com.around.volcanoinn.springboot.entity.Booking;

@Component
public interface BookingRepository extends CrudRepository<Booking, java.util.UUID> {
    @Override
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Booking save(Booking booking);

}
