package nl.vodafoneziggo.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity class representing an order in the database.
 * This class is annotated with JPA and Lombok annotations to simplify
 * persistence and data handling operations.
 * <p>
 * An order is associated with a product and contains user-related information
 * such as email, first name, and last name.
 * <p>
 * The database table is named "orders" and includes an index for efficient lookups
 * based on the combination of email and product ID.
 * <p>
 * Annotations:
 * - @Entity: Marks the class as a JPA entity.
 * - @Table: Specifies the database table and defines an index on the combination of email and product ID.
 * - @Data: Lombok annotation to generate boilerplate code such as getters, setters, and toString.
 */
@Entity
@Table(name = "orders",
        indexes = {
                @Index(name = "idx_order_email_product_id", columnList = "email,productID")
        })
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer productID;

    private String email;

    private String firstName;

    private String lastName;
}
