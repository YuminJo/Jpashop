package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopApplication {
    
    public static void main(String[] args) {
        Test sample = new Test();
        sample.setData("Hello, world!");
        String name = sample.getData();
        System.out.println(name);
        
        SpringApplication.run(JpashopApplication.class, args);
    }

}
