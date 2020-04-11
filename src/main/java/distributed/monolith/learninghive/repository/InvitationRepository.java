package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, Long> {

	Optional<Invitation> findByValidationToken(String validationToken);
}
