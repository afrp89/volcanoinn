package com.around.volcanoinn.springboot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import com.around.volcanoinn.springboot.entity.Guest;

@Component
public interface GuestRepository extends CrudRepository<Guest, Long> {

}
