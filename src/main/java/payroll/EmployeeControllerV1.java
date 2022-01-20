package payroll;

import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController wskazuje, że dane zwrócone przez każdą metodę zostaną zapisane bezpośrednio w treści odpowiedzi zamiast renderowania szablonu.
@RestController
// Jest to nie czyste RESTful tylko RPC (Remote Procedure Call).
public class EmployeeControllerV1 {

    // EmployeeRepository jest wstrzykiwany przez konstruktora do kontrolera.
    private final EmployeeRepository repo;

    public EmployeeControllerV1 (EmployeeRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/employees/v1")
    List<Employee> all() {
        return repo.findAll();
    }

    @PostMapping("/employees/v1")
    Employee newEmployee (@RequestBody Employee newEmployee) {
        return repo.save(newEmployee);
    }

    @GetMapping("/employees/v1/{id}")
    Employee one (@PathVariable Long id) {

        // EmployeeNotFoundException to wyjątek używany do wskazania, kiedy pracownik jest wyszukiwany, ale nie może zostać znaleziony.
        return repo.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @PutMapping("/employees/v1/{id}")
    Employee replaceEmployee (@RequestBody Employee newEmployee, @PathVariable Long id) {
        return repo.findById(id)
                .map(e -> {
                    e.setName(newEmployee.getName());
                    e.setRole(newEmployee.getRole());
                    return repo.save(e);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repo.save(newEmployee);
                });
    }

    @DeleteMapping("/employees/v1/{id}")
    void deletedEmployee (@PathVariable Long id) {
        repo.deleteById(id);
    }

}
