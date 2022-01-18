package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long>{
	
	//Usage => [prefix][...{columnName}{And,Or}...][suffix] [
	
	//Prefix => (find[Distinct]{FirstBy,Top[count->(default "" => 1)]By,AllBy,By}[columnName]), (existBy[columnName])
	
	//Suffix => Like,Contains,Containing,IsContaining,Is,Equals,Between,
	//Suffix => LessThan,LessThanEqual,GreaterThan,GreaterThanEqual,After,Before
	//Suffix => IsNull,NotNull,NotLike,StartingWith,EndingWith,(OrderBy[columnName]{Asc,Desc})
	
	public Optional<Student> findFirstByName(String name);
	
	public List<Student> findAllByName(String name, Sort sort);
	
	public List<Student> findAllByNameContains(String name, Sort sort);
	
	public List<Student> findAllByNumber(Integer number, Sort sort);
	
	public Optional<Student> findFirstByNameAndNumber(String name, Integer number);
	
	public List<Student> findAllByNameAndNumber(String name, Integer number, Sort sort);
}