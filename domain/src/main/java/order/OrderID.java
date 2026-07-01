package order;

import com.domain.UniqueIdentifier;

import java.util.Objects;
import java.util.UUID;

public class OrderID extends UniqueIdentifier {
    private final String value;

    public OrderID(String value) {
        this.value = value;
    }

    public static OrderID uniqueID() {
        return OrderID.from(UUID.randomUUID());
    }

    private static OrderID from(UUID uuid) {
        return new OrderID(uuid.toString().toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
       if(this == obj) return true;
       if(obj == null || getClass() != obj.getClass()) return false;
       final OrderID orderID = (OrderID) obj;
       return value.equals(orderID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueId());
    }

    @Override
    public String valueId() {
        return value;
    }
}
