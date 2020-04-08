package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

	List<Topic> findByTitleIgnoreCaseContaining(String title);

	Optional<Topic> findByTitle(String title);

}