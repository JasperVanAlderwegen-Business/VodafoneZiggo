package nl.vodafoneziggo.repository;

import nl.vodafoneziggo.model.OrderEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testPersist() {
        OrderEntity order = new OrderEntity();
        order.setProductID(1);
        order.setEmail("a@aa.nl");
        order.setFirstName("Aadje");
        order.setLastName("Aa");
        orderRepository.save(order);
        Assertions.assertEquals(1, orderRepository.count());
    }

    @Test
    void test_findByEmailAndProductID() {
        OrderEntity order = new OrderEntity();
        order.setProductID(1);
        order.setEmail("b@bb.nl");
        order.setFirstName("Beetje");
        order.setLastName("Bb");
        orderRepository.save(order);
        Assertions.assertEquals(1, orderRepository.count());
        OrderEntity byEmailAndProductID = orderRepository.findByEmailAndProductID("b@bb.nl", 1).get();
        Assertions.assertNotNull(byEmailAndProductID);
    }
}
