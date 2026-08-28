package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {
    private List<FieldErrorDetail> errors;

    public ValidationErrorResponse(String validationError, List<FieldErrorDetail> errors) {
    }

    @Data
    @AllArgsConstructor
    public static class FieldErrorDetail {
        private String field;
        private String message;
    }
}
