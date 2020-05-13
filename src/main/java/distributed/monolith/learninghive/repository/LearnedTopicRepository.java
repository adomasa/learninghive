package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.LearnedTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearnedTopicRepository extends JpaRepository<LearnedTopic, Long> {
	Optional<LearnedTopic> findByUserIdAndTopicId(long userId, long topicId);

	List<LearnedTopic> findByUserId(long userId);

	void deleteByTopicId(long topicId);
}
