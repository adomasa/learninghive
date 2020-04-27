package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

	List<Topic> findByTitleIgnoreCaseContaining(String title);

	Optional<Topic> findByTitle(String title);

	@Query(value = "with recursive cte as ( " +
			"        select * from topics where id = ?1" +
			"        union all " +
			"        select t.* from topics t join cte on cte.parent_id = t.id and t.id <> ?1)" +
			"     SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END from cte where parent_id = ?1",
			nativeQuery = true)
	Boolean circularReferencesExist(Long id);

}
