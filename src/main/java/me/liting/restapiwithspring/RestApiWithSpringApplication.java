package me.liting.restapiwithspring;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestApiWithSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiWithSpringApplication.class, args);
	}
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
		//여기서 등록한 빈 컨트롤러에서 사용가능
	}
}
