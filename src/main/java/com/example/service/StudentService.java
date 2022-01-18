package com.example.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.example.comparator.StudentComparator;
import com.example.model.Student;
import com.example.repository.StudentRepository;
import com.example.table.model.Column;
import com.example.table.model.Order;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StudentService {
	
	private StudentRepository studentRepo;
	
	private static final Comparator<Student> EMPTY_COMPARATOR = (e1, e2) -> 0;

	@Autowired
	public StudentService(StudentRepository studentRepo) {
		this.studentRepo = studentRepo;
	}
	
	private HashMap<String,Object> getResultArray(List<Student> student){
		HashMap<String,Object> result = new HashMap<>();
		
		if(student.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Student(s) not found.");
		}
		else
			result.put("error", false);
		
		result.put("values", student);
		
		return result;
	}
	
	private HashMap<String,Object> getResultArray(Optional<Student> student){
		HashMap<String,Object> result = new HashMap<>();
		
		result.put("error", false);
		result.put("values", new int[0]);
		
		if(student.isEmpty()) {
			result.replace("error", true);
			result.put("errorMsg", "Student not found.");
		}
		else
			result.replace("values", student.get());
		
		return result;
	}
	
	public Page<Student> getAll(PagingRequest pagingRequest){
		List<Student> student = studentRepo.findAll();
		
		List<Student> filtered = student.stream().sorted(sortStudents(pagingRequest))
												.filter(filterStudents(pagingRequest))
												.skip(pagingRequest.getStart())
		                                        .limit(pagingRequest.getLength())
		                                        .collect(Collectors.toList());
		
		long count = student.stream()
                .filter(filterStudents(pagingRequest))
                .count();
		
		Page<Student> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
	}
	
	public PageArray getAllArray(PagingRequest pagingRequest) {
		pagingRequest.setColumns(Stream.of("id", "name","number").map(Column::new).collect(Collectors.toList()));
		Page<Student> studentPage = getAll(pagingRequest);
		
		PageArray pageArray = new PageArray();
		pageArray.setRecordsFiltered(studentPage.getRecordsFiltered());
		pageArray.setDraw(studentPage.getDraw());
		pageArray.setRecordsTotal(studentPage.getRecordsTotal());
		pageArray.setData(studentPage.getData().stream().map(this::toStringList).collect(Collectors.toList()));
		
		return pageArray;
	}
	
	public HashMap<String, Object> getAll(){
		List<Student> student = studentRepo.findAll();
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> getById(Long id){
		Optional<Student> student = studentRepo.findById(id);
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> getByName(String name){
		Optional<Student> student = studentRepo.findFirstByName(name);
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> getByNameAll(String name, Sort sort){		
		List<Student> student = studentRepo.findAllByName(name, sort);
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> getByNameLikeAll(String name, Sort sort){		
		List<Student> student = studentRepo.findAllByNameContains(name, sort);
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> getByNumberAll(Integer number, Sort sort){
		List<Student> student = studentRepo.findAllByNumber(number, sort);
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> getByNameAndNumber(String name, Integer number){
		Optional<Student> student = studentRepo.findFirstByNameAndNumber(name, number);
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> getByNameAndNumberAll(String name, Integer number, Sort sort){
		List<Student> student = studentRepo.findAllByNameAndNumber(name, number, sort);
		
		return getResultArray(student);
	}
	
	public HashMap<String, Object> insert(Student student){
		HashMap<String,Object> result = new HashMap<>();
		
		student.setId(0);
		
		try {
			studentRepo.save(student);
			result.put("error", false);
			result.put("errorMsg", "Student successfully inserted.");
		}
		catch(ConstraintViolationException | InvalidDataAccessApiUsageException | JpaObjectRetrievalFailureException e) {
			result.put("error", true);
			result.put("errorMsg", "Student already inserted.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> update(Student student){
		HashMap<String,Object> result = new HashMap<>();
		
		Optional<Student> stu = studentRepo.findById(student.getId());
		
		if(stu.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Student not found.");
		}
		else {
			try {
				studentRepo.saveAndFlush(student);
				result.put("error", false);
				result.put("errorMsg", "Student successfully updated.");
			}
			catch(ConstraintViolationException e) {
				result.put("error", true);
				result.put("errorMsg", "Student not found.");
			}
		}
		
		return result;
	}
	
	public HashMap<String, Object> delete(Student student){
		HashMap<String,Object> result = new HashMap<>();
		
		try{
			studentRepo.deleteById(student.getId());
			result.put("error", false);
			result.put("errorMsg", "Student successfully deleted.");
		}
		catch(IllegalArgumentException e) {
			result.put("error", true);
			result.put("errorMsg", "Student not found or null.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> deleteAll(){
		HashMap<String,Object> result = new HashMap<>();

		studentRepo.deleteAll();
		result.put("error", false);
		result.put("errorMsg", "Student(s) successfully deleted.");

		return result;
	}
	
	private Predicate<Student> filterStudents(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch()
                                                                                  .getValue())) {
            return student -> true;
        }

        String value = pagingRequest.getSearch()
                                    .getValue();

        return student -> student.getName()
                                   .toLowerCase()
                                   .contains(value)
                || Long.toString(student.getId())
                	.contains(value)
                || Integer.toString(student.getNumber()).contains(value);
    }

    private Comparator<Student> sortStudents(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder()
                                       .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                                         .get(columnIndex);

            Comparator<Student> comparator = StudentComparator.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }
	
	private List<String> toStringList(Student student) {
        return Arrays.asList(Long.toString(student.getId()), student.getName(), Integer.toString(student.getNumber()));
    }
}
