package payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

// @RestController wskazuje, że dane zwrócone przez każdą metodę zostaną zapisane bezpośrednio w treści odpowiedzi zamiast renderowania szablonu.
@RestController
public class EmployeeControllerV2 {

    private final EmployeeRepository repo;

    public EmployeeControllerV2 (EmployeeRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/employees/v2")
    // CollectionModel to kolejny kontener Spring HATEOAS; ma na celu hermetyzację zbiorów zasobów
    CollectionModel<EntityModel<Employee>> all() {

        List<EntityModel<Employee>> employees = repo.findAll().stream()
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
                        linkTo(methodOn(EmployeeController.class).all()).withRel("employees/v2")))
                .collect(Collectors.toList());

        return CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @GetMapping("/employees/v2/{id}")
    // EntityModel pozyskuje pojedynczą jednostkę zasobów
    EntityModel<Employee> one (@PathVariable Long id) {

        Employee employee = repo.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).all()).withRel("employees/v2"));
    }

    @PutMapping ("/employees/v2/{id}")
    // Stara medoda aktualizacji pracownika uwzglednia tylko pole "name"
    Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        return repo.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repo.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repo.save(newEmployee);
                });
    }


}
