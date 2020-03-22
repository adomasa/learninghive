package distributed.monolith.learninghive.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "links")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    @NonNull
    @Length(max = 254, message = "Email is too long")
    private String email;

    @Column(nullable = false)
    @NonNull
    private Date date;

    @Column(nullable = false, unique = true)
    @NonNull
    private String link;

    @Column(nullable = false)
    @NonNull
    @ManyToOne
    private User userWhoInvited;
}
