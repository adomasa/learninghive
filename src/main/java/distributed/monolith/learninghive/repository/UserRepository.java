package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> deleteByEmail(@Param("email") String email);

	Optional<User> findByEmail(@Param("email") String email);

	List<User> findByRoles(Role role);
}
