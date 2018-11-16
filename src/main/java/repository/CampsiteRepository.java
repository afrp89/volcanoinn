package repository;

import org.springframework.data.repository.CrudRepository;
import entity.Campsite;

public interface CampsiteRepository extends CrudRepository<Campsite, Long> {

}
