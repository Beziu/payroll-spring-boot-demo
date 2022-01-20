package payroll;

// Gdy EmployeeNotFoundException zostanie wyrzucony, dodatkowo id
// oraz z konfiguracji Spring MVC jest używana do renderowania HTTP 404: EmployeeNotFoundAdvice
public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException (Long id) {
        super("Could not found employee " +id);
    }

}
