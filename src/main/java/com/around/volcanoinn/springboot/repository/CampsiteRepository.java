package com.around.volcanoinn.springboot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import com.around.volcanoinn.springboot.entity.Campsite;

@Component
public interface CampsiteRepository extends CrudRepository<Campsite, Long> {

}
