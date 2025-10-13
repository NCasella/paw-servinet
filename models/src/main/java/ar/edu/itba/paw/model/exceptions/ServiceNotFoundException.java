package ar.edu.itba.paw.model.exceptions;

public class ServiceNotFoundException extends NotFoundException {
    //TODO: cambiar por llamado al constructor con parametros
    private String servicename = "servicio 1";
    //TODO: ver cómo pasar el username si es que se ingresa desde una lista que ya fue cargada (más significativo que hablar de un id)
    //private static final String message = "The service with id: %s was not found";

    private static final String message = "The service was not found";
    private static final String SERVICE_NON_EXISTENT = "servicionoexiste";

    public ServiceNotFoundException() {
        super(SERVICE_NON_EXISTENT);
    }


    @Override
    public String getMessage() {
        return String.format(message, servicename);
    }
}
