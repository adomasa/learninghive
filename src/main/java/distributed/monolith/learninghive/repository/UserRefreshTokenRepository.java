package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.domain.UserRefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRefreshTokenRepository extends CrudRepository<UserRefreshToken, Long> {

	Optional<UserRefreshToken> findByToken(String token);

	Optional<UserRefreshToken> findByUser(User user);

	Optional<UserRefreshToken> removeByUser(User user);

}
