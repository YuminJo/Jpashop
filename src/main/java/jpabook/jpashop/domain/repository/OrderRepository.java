package jpabook.jpashop.domain.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    
    private final EntityManager em;
    
    public void save(Order order) {
        em.persist(order);
    }
    
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }
    
    //QueryDsl로 처리하는 것이 더 깔끔하다.
    //검색 기능을 위한 메서드
    
    //JPA Criteria
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName()
                            + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000 건
        return query.getResultList();
    }
    
    //JPQL로 처리
    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
        // Order를 조회할 때 연관된 Member와 Delivery를 함께 조회
        // fetch 는 Jpa만 지원하는 기능이다.
        // 실무에서 Jpa쓰기 위해서는 100% 알아야 한다.
    }

    public List<SimpleOrderQueryDto> findOrderDtos() {
        // Controller 의존관계 생기면 안된다.
        return em.createQuery(
                "select new jpabook.jpashop.domain.repository.SimpleOrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "from Order o" +
                        " join o.member m" +
                        " join o.delivery d", SimpleOrderQueryDto.class
        ).getResultList();
    }
    
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class
        )
                .setFirstResult(1)
                .setMaxResults(100)
                .getResultList();
        // Order 데이터 자체가 OrderItem과 Item을 가지고 있으므로 중복이 발생할 수 있다.
        // Join은 1대다 관계에서 데이터가 뻥튀기 되는 현상이 발생할 수 있다.
        // distinct를 사용하면 중복을 제거할 수 있다.
        // distinct는 완벽히 동일한 엔티티가 조회되어야만 중복을 제거한다.
        // distinct를 사용하면 SQL에 distinct를 추가하고, SQL 결과에 엔티티 중복이 있으면 애플리케이션에서 추가적으로 중복을 제거한다.
        
        // 컬렉션 페치 조인을 사용하면 페이징이 불가능하다.
        // 컬렉션 페치 조인을 사용하면 일대다 조인이 일어나는데, 일대다 조인을 페이징하면 데이터가 뻥튀기 되기 때문에 원하는 결과가 나오지 않는다.
        // 메모리에서 페이징 해버린다.(매우 위험)
        // 컬렉션 페치 조인은 1개만 사용할 수 있다.
    }
}
