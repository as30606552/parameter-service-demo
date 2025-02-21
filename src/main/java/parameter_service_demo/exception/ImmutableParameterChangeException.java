package parameter_service_demo.exception;

public class ImmutableParameterChangeException extends RuntimeException {

    public ImmutableParameterChangeException(Long id) {
        super("Parameter with id %d is activated and immutable".formatted(id));
    }
}
