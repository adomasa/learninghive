package distributed.monolith.learninghive.repository;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import org.springframework.data.jpa.repository.Query;
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

	@Query(value = "with recursive cte as ( " +
			"        select * from users where id = ?1" +
			"        union all " +
			"        select u.* from users u join cte on cte.supervisor_id = u.id and u.id <> ?1)" +
			"     SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END from cte where supervisor_id = ?1",
			nativeQuery = true)
	Boolean circularReferencesExist(Long id);
}
