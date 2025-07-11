package umc.plantory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlantoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantoryApplication.class, args);
	}

}
