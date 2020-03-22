package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends CrudRepository<Invitation, Long> {

}
