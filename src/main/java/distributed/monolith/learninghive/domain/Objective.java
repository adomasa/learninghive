package distributed.monolith.learninghive.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "objectives", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"}))
public class Objective {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "topic_id")
	private Topic topic;

	/**
	 * User cannot edit objective which was assigned by supervisor.
	 */
	@ManyToOne
	private User owner;

	@CreationTimestamp
	private Date date;

	@Version
	private int version;
}
