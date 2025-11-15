package swanhack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * 
 * @author Jackson Weatherspoon
 * @coms-3090-011.class.las.iastate.edu
 */

@SpringBootApplication
@EnableJpaRepositories(basePackages = "swanhack")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
