package com.dct.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(
    exclude = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
    }
)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DctBaseStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(DctBaseStarterApplication.class, args);
    }
}
