package parameter_service_demo.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String entityName, Long id) {
        super("%s with id %d does not exist".formatted(entityName, id));
    }
}
