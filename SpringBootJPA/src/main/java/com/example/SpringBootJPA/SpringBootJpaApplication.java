package com.example.SpringBootJPA;

import com.example.SpringBootJPA.entity.Student;
import com.example.SpringBootJPA.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootJpaApplication.class, args);
	}
	@Bean
	CommandLineRunner commandLineRunner(StudentRepository studentRepository) { // Для того чтобы мы могли запустить какой-то код, после запуска приложения
		return args -> {
			Student maria = new Student(
					"Maria",
					"Jones",
					"maria.jones@gmail.com",
					21);
			studentRepository.save(maria);
		};
	}

}
