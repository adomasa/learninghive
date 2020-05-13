package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Restriction;
import distributed.monolith.learninghive.restrictions.RestrictionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestrictionRepository extends JpaRepository<Restriction, Long> {
	Optional<Restriction> findByUserIdAndRestrictionType(Long userId, RestrictionType type);

	List<Restriction> findByUserIdOrUserIdIsNull(Long userId);

	List<Restriction> findByUserId(Long userId);
}
