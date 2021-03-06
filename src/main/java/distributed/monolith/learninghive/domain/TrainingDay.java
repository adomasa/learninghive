package distributed.monolith.learninghive.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "training_day",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "scheduled_day"}))
public class TrainingDay implements VersionedEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToMany
	@JoinTable(
			name = "training_day_topic",
			joinColumns = @JoinColumn(name = "training_day_id"),
			inverseJoinColumns = @JoinColumn(name = "topic_id"))
	private List<Topic> topics;

	@CreationTimestamp
	private java.util.Date creationDate;

	@Column(nullable = false, name = "scheduled_day")
	private java.sql.Date scheduledDay;

	@Column(nullable = false)
	private String title;

	private String description;

	@Version
	private Integer version;
}
