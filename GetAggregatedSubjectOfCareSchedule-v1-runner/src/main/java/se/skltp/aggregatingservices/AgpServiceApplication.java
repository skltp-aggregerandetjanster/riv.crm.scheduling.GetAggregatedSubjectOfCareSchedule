package se.skltp.aggregatingservices;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"se.skltp.aggregatingservices","se.skltp.takcache"})
public class AgpServiceApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder(AgpServiceApplication.class).run(args);
  }

}
