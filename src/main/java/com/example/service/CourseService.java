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
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import com.example.comparator.CourseComparator;
import com.example.model.Course;
import com.example.model.Subject;
import com.example.model.Teacher;
import com.example.repository.CourseRepository;
import com.example.table.model.Column;
import com.example.table.model.Order;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseService {

	private static final Comparator<Course> EMPTY_COMPARATOR = (e1, e2) -> 0;
	
	private CourseRepository courseRepo;
	
	@Autowired
	public CourseService(CourseRepository courseRepo) {
		this.courseRepo = courseRepo;
	}
	
	private HashMap<String,Object> getResultArray(List<Course> course){
		HashMap<String,Object> result = new HashMap<>();
		
		if(course.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Course(s) not found.");
		}
		else
			result.put("error", false);
		
		result.put("values", course);
		
		return result;
	}
	
	private HashMap<String,Object> getResultArray(Optional<Course> course){
		HashMap<String,Object> result = new HashMap<>();
		
		result.put("error", false);
		result.put("values", new int[0]);
		
		if(course.isEmpty()) {
			result.replace("error", true);
			result.put("errorMsg", "Course not found.");
		}
		else
			result.replace("values", course.get());
		
		return result;
	}
	
	public HashMap<String, Object> getAll(){
		List<Course> course = courseRepo.findAll();
		
		return getResultArray(course);
	}
	
	public Page<Course> getAll(PagingRequest pagingRequest){
		List<Course> course = courseRepo.findAll();
		
		List<Course> filtered = course.stream().sorted(sortCourses(pagingRequest))
												.filter(filterCourses(pagingRequest))
												.skip(pagingRequest.getStart())
		                                        .limit(pagingRequest.getLength())
		                                        .collect(Collectors.toList());
		
		long count = course.stream()
                .filter(filterCourses(pagingRequest))
                .count();
		
		Page<Course> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
	}
	
	public PageArray getAllArray(PagingRequest pagingRequest) {
		pagingRequest.setColumns(Stream.of("id", "teacher.name","subject.subject").map(Column::new).collect(Collectors.toList()));
		Page<Course> coursePage = getAll(pagingRequest);
		
		PageArray pageArray = new PageArray();
		pageArray.setRecordsFiltered(coursePage.getRecordsFiltered());
		pageArray.setDraw(coursePage.getDraw());
		pageArray.setRecordsTotal(coursePage.getRecordsTotal());
		pageArray.setData(coursePage.getData().stream().map(this::toStringList).collect(Collectors.toList()));
		
		return pageArray;
	}
	
	public HashMap<String, Object> getById(Long id){		
		Optional<Course> course = courseRepo.findById(id);
		
		return getResultArray(course);
	}
	
	public HashMap<String, Object> getByTeacher(Teacher teacher){
		Optional<Course> course = courseRepo.findTopByTeacher(teacher);
		
		return getResultArray(course);
	}
	
	public HashMap<String, Object> getByTeacherAll(Teacher teacher){		
		List<Course> course = courseRepo.findByTeacher(teacher);
		
		return getResultArray(course);
	}
	
	public HashMap<String, Object> getBySubject(Subject subject){
		
		Optional<Course> course = courseRepo.findTopBySubject(subject);
		
		return getResultArray(course);
	}
	
	public HashMap<String, Object> getBySubjectAll(Subject subject){
		List<Course> course = courseRepo.findBySubject(subject);
		
		return getResultArray(course);
	}
	
	public HashMap<String, Object> getByTeacherAndSubject(Teacher teacher,Subject subject){
		Optional<Course> course = courseRepo.findTopByTeacherAndSubject(teacher,subject);
		
		return getResultArray(course);
	}
	
	public HashMap<String, Object> getByTeacherAndSubjectAll(Teacher teacher,Subject subject){		
		List<Course> course = courseRepo.findByTeacherAndSubject(teacher,subject);
		
		return getResultArray(course);
	}
	
	public HashMap<String, Object> insert(Course course){
		HashMap<String,Object> result = new HashMap<>();
		
		course.setId(0);
		
		try {
			courseRepo.save(course);
			result.put("error", false);
			result.put("errorMsg", "Course successfully inserted.");
		}
		catch(ConstraintViolationException | InvalidDataAccessApiUsageException | JpaObjectRetrievalFailureException e) {
			result.put("error", true);
			result.put("errorMsg", "Teacher or subject id not found or duplicated.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> update(Course course){
		HashMap<String,Object> result = new HashMap<>();
		
		Optional<Course> c = courseRepo.findById(course.getId());
		
		if(c.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Course not found.");
		}
		else {
			try {
				courseRepo.saveAndFlush(course);
				result.put("error", false);
				result.put("errorMsg", "Course successfully uptaded.");
			}
			catch(ConstraintViolationException | InvalidDataAccessApiUsageException | JpaObjectRetrievalFailureException e){
				result.put("error", true);
				result.put("errorMsg", "Teacher or subject not found.");
			}
		}
	
		return result;
	}
	
	public HashMap<String,Object> delete(Course course){
		HashMap<String,Object> result = new HashMap<>();
		
		course.getTeacher().setId(0);
		course.getSubject().setId(0);
		
		try{
			courseRepo.deleteById(course.getId());
			result.put("error", false);
			result.put("errorMsg", "Course successfully deleted.");
		}
		catch(IllegalArgumentException e) {
			result.put("error", true);
			result.put("errorMsg", "Course not found or null.");
		}
		
		return result;
	}
	
	public HashMap<String,Object> deleteAll(){
		HashMap<String,Object> result = new HashMap<>();

		courseRepo.deleteAll();
		result.put("error", false);
		result.put("errorMsg", "Course(s) successfully deleted.");
		
		return result;
	}
	
	private Predicate<Course> filterCourses(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch()
                                                                                  .getValue())) {
            return course -> true;
        }

        String value = pagingRequest.getSearch()
                                    .getValue();

        return course -> course.getTeacher().getName()
                                   .toLowerCase()
                                   .contains(value)
                || course.getSubject().getSubject()
                           .toLowerCase()
                           .contains(value)
                || Long.toString(course.getId()).contains(value);
    }

    private Comparator<Course> sortCourses(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder()
                                       .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                                         .get(columnIndex);

            Comparator<Course> comparator = CourseComparator.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }
	
	private List<String> toStringList(Course course) {
        return Arrays.asList(Long.toString(course.getId()), course.getTeacher().getName(), course.getSubject().getSubject());
    }
}
