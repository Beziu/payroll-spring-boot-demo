package payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    public EmployeeController (EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> all() {

        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @GetMapping("/employees/{id}")
    EntityModel<Employee> one(@PathVariable Long id) {

        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }

    // Post ktory obsluguje stare i nowe zadania klientow
    // Zmiana w kierunku upewnienia sie czy dodanie nowego emplayees mialo miejsce
    // tj. sprawdzenie czy dzala wlasciwie
    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

        EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }


//    Obiekt Employee zbudowany na podstawie operacji save() jest następnie zawijany w EmployeeModelAssembler
//    za pomocą EntityModel<Employee>. Korzystając z metody getRequiredLink(),
//    możesz pobrać Link utworzone przez EmployeeModelAssemblera za pomocą SELF. Ta metoda zwraca Link,
//    który musi zostać zamienione na a URI za pomocą metody toUri.
    // Metoda przy aktualizacji danych uwzglednia tez pola firstName oraz lastName
    @PutMapping("/employees/{id}")
    // potrzebujemy object newEmplyoee oraz id
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

        // tworzy nowy objekt updatedEmployee i wstawia do niego dane z repo jak znajdzie odpowiedni id
        // jak nie to tworzy nowy odjekt z nowym id i zapisuje go.
        // tutaj employee to nazwa tymczasowa ktora odnosi sie do znalezionego objektu
        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save((newEmployee));
                });

        // do zwroconego ob. updatedEmployee dodajemy za pomoca assemplerem linki
        // tworzac model danych entityModel
        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    // potrzebna jest do wywolania bledu "HTTP 204 No Content"
    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> deleteEmployee (@PathVariable Long id) {

        repository.deleteById(id);

        return ResponseEntity.noContent()
                .build();
    }

}
