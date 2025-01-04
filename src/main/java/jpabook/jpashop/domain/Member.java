package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    
    //검증 로직이 현재 Entity에 있어서 좋지 않음
    @NotEmpty
    private String name;
    
    @Embedded
    private Address address;
    
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
