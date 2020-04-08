package distributed.monolith.learninghive.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateObjectiveException extends RuntimeException {

    public DuplicateObjectiveException() {
        super("Objective with such topic and user combination already exists");
    }
}
