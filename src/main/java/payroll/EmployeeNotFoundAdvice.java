package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class EmployeeNotFoundAdvice {

    // @ResponseBody sygnalizuje, że wyjątek jest przekazywany bezpośrednio do treści odpowiedzi
    @ResponseBody
    // @ExceptionHandler konfiguruje poradę tak, aby odpowiadała tylko wtedy, gdy EmployeeNotFoundException zostanie zgłoszony.
    @ExceptionHandler(EmployeeNotFoundException.class)
    // @ResponseStatus mówi, aby wydać HttpStatus.NOT_FOUND, tj. HTTP 404
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(EmployeeNotFoundException exception) {
        return exception.getMessage();
    }

}
