package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Link;
import distributed.monolith.learninghive.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepository extends CrudRepository<Link, Long> {

}
