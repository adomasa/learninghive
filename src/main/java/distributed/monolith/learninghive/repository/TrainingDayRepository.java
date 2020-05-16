package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.TrainingDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingDayRepository extends JpaRepository<TrainingDay, Long> {
	List<TrainingDay> findByUserId(long id);

	List<TrainingDay> deleteByUserId(long id);

	Optional<TrainingDay> findByScheduledDayAndUserId(Date date, Long userId);

	// Used for querying all users training days except for one currently being updated
	List<TrainingDay> findByIdNotAndUserId(long id, long userId);
}
