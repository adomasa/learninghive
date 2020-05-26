package distributed.monolith.learninghive.domain;

import distributed.monolith.learninghive.restrictions.RestrictionType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "restrictions", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "restriction_type"}))
public class Restriction implements VersionedEntity {
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

	@Version
	private Integer version;
}
