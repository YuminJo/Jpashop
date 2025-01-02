package jpabook.jpashop.domain.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.repository.OrderRepository;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@Transactional
class OrderServiceTest {
    //좋은 테스트는 db나 dependency를 사용하지 않는다.
    //순수한 메서드의 **단위 테스트**를 해야한다. 단위 테스트가 중요.
    //하지만 현재 테스트는 전체적으로 잘 되는지 보기 위해 사용한다.
    
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    
    @Test
    void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Item book = createBook("시골 JPA", 10000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        
        //then
        Order getOrder = orderRepository.findOne(orderId);
        
        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다.", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 8, book.getStockQuantity());
    }

    @Test
    void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        
        int orderCount = 11;

        //when
        
        //then
        final NotEnoughStockException notEnoughStockException = assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), orderCount),
                "재고수량이 부족 하면 예외가 발생 한다."
        );
    }
    
    @Test
    void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);
        
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        
        //when
        orderService.cancelOrder(orderId);
        
        //then
        Order getOrder = orderRepository.findOne(orderId);
        
        assertEquals("주문 취소시 상태는 CANCEL 이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);//CTRL + ALT + P 파라미터 추출
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}