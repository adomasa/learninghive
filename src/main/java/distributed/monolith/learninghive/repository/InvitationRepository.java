package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

	Optional<Invitation> findByValidationToken(String validationToken);

	Optional<Invitation> findByEmail(String validationToken);
}
