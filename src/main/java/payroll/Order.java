package payroll;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table (name = "CUSTOMER_ORDER")
public class Order {

    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private String status;

    public Order (String description, String status) {
        this.description = description;
        this.status = status;
    }

    public Order () {
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId())
                && Objects.equals(getDescription(), order.getDescription())
                && Objects.equals(getStatus(), order.getStatus());
    }

    @Override
    public int hashCode () {
        return Objects.hash(getId(), getDescription(), getStatus());
    }

    @Override
    public String toString () {
        return "Order{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
