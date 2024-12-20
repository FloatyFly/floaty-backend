package ch.floaty.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("ch.floaty")
@EnableJpaRepositories("ch.floaty")
@EntityScan("ch.floaty")
public class FloatyApplicationConfig {

}
