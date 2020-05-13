package distributed.monolith.learninghive.domain;

import distributed.monolith.learninghive.restrictions.RestrictionType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.util.Date;

@Entity
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Getter
@Setter
@Table(name = "restrictions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "restriction_type"}))
public class Restriction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false, name = "restriction_type")
	private RestrictionType restrictionType;

	@Column(nullable = false)
	private long daysLimit;

	@CreationTimestamp
	private Date date;
}
