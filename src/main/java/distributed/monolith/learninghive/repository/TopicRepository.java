package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Topic;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends CrudRepository<Topic, Long> {

	void deleteById(Long id);

	List<Topic> findByTitleIgnoreCaseContaining(String title);

	Optional<Topic> findByTitle(String title);

}
