package nl.vodafoneziggo.repository;

import nl.vodafoneziggo.model.OrderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository interface for managing OrderEntity persistence operations.
 * Extends Spring Data's {@link CrudRepository} to provide CRUD operations
 * and custom query methods.
 */
public interface OrderRepository extends CrudRepository<OrderEntity, Integer> {
    Optional<OrderEntity> findByEmailAndProductID(String email, Integer productID);

    Iterable<OrderEntity> findByEmail(String email);
}
