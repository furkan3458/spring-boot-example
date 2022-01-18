package com.example.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Course;
import com.example.model.Subject;
import com.example.model.Teacher;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long>{
	public List<Course> findByTeacher(Teacher teacher);
	
	public Optional<Course> findTopByTeacher(Teacher teacher);
	
	public List<Course> findBySubject(Subject subject);
	
	public Optional<Course> findTopBySubject(Subject subject);
	
	public List<Course> findByTeacherAndSubject(Teacher teacher, Subject subject);

	public Optional<Course> findTopByTeacherAndSubject(Teacher teacher, Subject subject);
}
