package payroll;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping (path = "/orders")
class OrderController {

    private final OrderRepository orderRepository;
    private final OrderModelAssembler assembler;

    OrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {
        this.orderRepository = orderRepository;
        this.assembler = assembler;
    }

    @GetMapping
    CollectionModel<EntityModel<Order>> all() {

        List<EntityModel<Order>> orders = orderRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping (path = "{id}")
    EntityModel<Order> one (@PathVariable Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return assembler.toModel(order);
    }

    @PostMapping
    ResponseEntity<EntityModel<Order>> newOrder (@RequestBody Order order) {

        order.setStatus(String.valueOf(Status.IN_PROGRESS));
        Order newOrder = orderRepository.save(order);

        return ResponseEntity
                .created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri()) //
                .body(assembler.toModel(newOrder));
    }
}
