package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Course;
import com.example.model.CourseStudent;
import com.example.model.Student;

@Repository
public interface CourseStudentRepository extends JpaRepository<CourseStudent,Long>{
	public List<CourseStudent> findByCourse(Course course);
	
	public List<CourseStudent> findByStudent(Student student);
	
	public Optional<CourseStudent> findTopByCourseAndStudent(Course course, Student student);
}
