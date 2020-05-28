package distributed.monolith.learninghive.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InvitationData {
	private Long invitationId;
	private String recipient;
	private String link;
	private String source;
}
