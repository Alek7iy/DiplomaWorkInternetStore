package Exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND");
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}