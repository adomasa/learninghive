package distributed.monolith.learninghive.model.request;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class ObjectiveRequest {
    @NotNull
    Long userId;

    @NotNull
    Long topicId;

    @NotNull
    Boolean completed;
}
