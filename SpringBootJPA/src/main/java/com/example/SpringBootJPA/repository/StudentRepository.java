package com.example.SpringBootJPA.repository;

import com.example.SpringBootJPA.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
