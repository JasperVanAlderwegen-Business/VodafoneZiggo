package nl.vodafoneziggo.repository;

import nl.vodafoneziggo.model.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity, Integer> {
    Optional<OrderEntity> findByEmailAndProductID(String email, Integer productID);

}
