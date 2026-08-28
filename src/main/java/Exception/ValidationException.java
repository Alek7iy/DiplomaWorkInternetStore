package Exception;

import java.util.List;

public class ValidationException extends ApiException {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("VALIDATION_ERROR", "Ошибка валидации данных");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}

