package com.around.volcanoinn.springboot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import com.around.volcanoinn.springboot.entity.Booking;

@Component
public interface BookingRepository extends CrudRepository<Booking, java.util.UUID> {

}
