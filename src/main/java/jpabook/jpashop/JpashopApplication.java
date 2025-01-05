package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }
    
    //그냥 이런 방법이 있다는거지 굳이 할 필요는 없다
    //어마어마하게 쿼리가 나간다. 이유는 Order안에 있는 Member와 Delivery를 가져오기 위해서
    //그래서 이런 방법을 사용하면 안된다.
/*    @Bean
    Hibernate5Module hibernate5Module() {
        Hibernate5Module hibernate5Module = new Hibernate5Module();
        //hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
        return hibernate5Module;
    }*/
}