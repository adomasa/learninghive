package distributed.monolith.learninghive.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "topics")
@OptimisticLocking(type = OptimisticLockType.VERSION)
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Topic {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false, unique = true)
	@NonNull
	private String title;

	private String content;

	@CreationTimestamp
	private Date date;

	@ManyToOne
	private Topic parent;

	@OneToMany(mappedBy = "parent")
	private List<Topic> children;

}