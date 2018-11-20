package com.around.volcanoinn.springboot.repository;

import org.springframework.data.repository.CrudRepository;
import com.around.volcanoinn.springboot.entity.Booking;

public interface BookingRepository extends CrudRepository<Booking, java.util.UUID> {

}
