package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {

	List<Objective> findByUserId(long userId);

	List<Objective> findByTopicId(long userId);

	List<Objective> deleteByUserId(long userId);

	Objective findByUserIdAndTopicId(long userId, long topicId);

}
