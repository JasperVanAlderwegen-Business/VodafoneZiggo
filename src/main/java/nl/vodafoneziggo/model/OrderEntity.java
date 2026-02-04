package nl.vodafoneziggo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order",
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
