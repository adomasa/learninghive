package distributed.monolith.learninghive.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "learned_topic", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"}))
public class LearnedTopic {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@CreationTimestamp
	private Date date;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	User user;

	@ManyToOne
	@JoinColumn(name = "topic_id", nullable = false)
	Topic topic;
}
