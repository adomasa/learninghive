package distributed.monolith.learninghive.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.util.List;

@Entity
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Getter
@Setter
@Table(name = "training_day",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "scheduled_day"}))
public class TrainingDay {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToMany
	@JoinTable(
			name = "training_day_objective",
			joinColumns = @JoinColumn(name = "training_day_id"),
			inverseJoinColumns = @JoinColumn(name = "objective_id"))
	private List<Objective> objectives;

	@CreationTimestamp
	private java.util.Date creationDate;

	@Column(nullable = false, name = "scheduled_day")
	private java.sql.Date scheduledDay;

	private String description;
}
