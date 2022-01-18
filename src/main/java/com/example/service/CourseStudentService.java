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

import com.example.comparator.CourseStudentComparator;
import com.example.model.CourseStudent;
import com.example.repository.CourseStudentRepository;
import com.example.table.model.Column;
import com.example.table.model.Order;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CourseStudentService {

	private static final Comparator<CourseStudent> EMPTY_COMPARATOR = (e1, e2) -> 0;
	
	private CourseStudentRepository courseStudentRepo;
	
	@Autowired
	public CourseStudentService(CourseStudentRepository courseStudentRepo) {
		this.courseStudentRepo = courseStudentRepo;
	}
	
	private HashMap<String,Object> getResultArray(List<CourseStudent> courseStudent){
		HashMap<String,Object> result = new HashMap<>();
		
		if(courseStudent.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Assigned Course(s) not found.");
		}
		else
			result.put("error", false);
		
		result.put("values", courseStudent);
		
		return result;
	}
	
	private HashMap<String,Object> getResultArray(Optional<CourseStudent> courseStudent){
		HashMap<String,Object> result = new HashMap<>();
		
		result.put("error", false);
		result.put("values", new int[0]);
		
		if(courseStudent.isEmpty()) {
			result.replace("error", true);
			result.put("errorMsg", "Assigned Course not found.");
		}
		else
			result.replace("values", courseStudent.get());
		
		return result;
	}
	
	public HashMap<String, Object> getAll(){		
		List<CourseStudent> courseStudent = courseStudentRepo.findAll();
		
		return getResultArray(courseStudent);
	}
	
	
	public HashMap<String, Object> getById(Long id){
		Optional<CourseStudent> courseStudent = courseStudentRepo.findById(id);
		
		return getResultArray(courseStudent);
	}
	
	public Page<CourseStudent> getAll(PagingRequest pagingRequest){
		List<CourseStudent> courseStudent = courseStudentRepo.findAll();
		
		List<CourseStudent> filtered = courseStudent.stream().sorted(sortAssignments(pagingRequest))
												.filter(filterAssignments(pagingRequest))
												.skip(pagingRequest.getStart())
		                                        .limit(pagingRequest.getLength())
		                                        .collect(Collectors.toList());
		
		long count = courseStudent.stream()
                .filter(filterAssignments(pagingRequest))
                .count();
		
		Page<CourseStudent> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
	}
	
	public PageArray getAllArray(PagingRequest pagingRequest) {
		pagingRequest.setColumns(Stream.of("id", "student.name","student.number","subject.subject","teacher.name").map(Column::new).collect(Collectors.toList()));
		Page<CourseStudent> courseStudentPage = getAll(pagingRequest);
		
		PageArray pageArray = new PageArray();
		pageArray.setRecordsFiltered(courseStudentPage.getRecordsFiltered());
		pageArray.setDraw(courseStudentPage.getDraw());
		pageArray.setRecordsTotal(courseStudentPage.getRecordsTotal());
		pageArray.setData(courseStudentPage.getData().stream().map(this::toStringList).collect(Collectors.toList()));
		
		return pageArray;
	}
	
	public HashMap<String, Object> insert(CourseStudent courseStudent){
		HashMap<String,Object> result = new HashMap<>();
		
		courseStudent.setId(0);
		
		try {
			courseStudentRepo.save(courseStudent);
			result.put("error", false);
			result.put("errorMsg", "Course successfully inserted for student.");
		}
		catch(ConstraintViolationException | InvalidDataAccessApiUsageException | JpaObjectRetrievalFailureException e) {
			result.put("error", true);
			result.put("errorMsg", "Course or Student id not found.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> update(CourseStudent courseStudent){
		HashMap<String,Object> result = new HashMap<>();
		
		Optional<CourseStudent> courseStu = courseStudentRepo.findById(courseStudent.getId());
		
		if(courseStu.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Course assignment to the student not found.");
		}
		else {
			try {
				courseStudentRepo.saveAndFlush(courseStudent);
				result.put("error", false);
				result.put("errorMsg", "Course successfully updated for student.");
			}
			catch(ConstraintViolationException | InvalidDataAccessApiUsageException | JpaObjectRetrievalFailureException e) {
				result.put("error", true);
				result.put("errorMsg", "Course or Student id not found.");
			}	
		}
		
		return result;
	}
	
	public HashMap<String,Object> delete(CourseStudent courseStudent){
		HashMap<String,Object> result = new HashMap<>();
		courseStudent.getCourse().setId(0);
		courseStudent.getStudent().setId(0);
		
		try{
			courseStudentRepo.deleteById(courseStudent.getId());
			result.put("error", false);
			result.put("errorMsg", "Course assignment successfully deleted.");
		}
		catch(IllegalArgumentException e) {
			result.put("error", true);
			result.put("errorMsg", "Course assignment not found or null.");
		}
		
		return result;
	}
	
	public HashMap<String,Object> deleteAll(){
		HashMap<String,Object> result = new HashMap<>();
		courseStudentRepo.deleteAll();
		result.put("error", false);
		result.put("errorMsg", "Course assignment(s) successfully deleted.");
		
		return result;
	}
	
	private Predicate<CourseStudent> filterAssignments(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch()
                                                                                  .getValue())) {
            return assignment -> true;
        }

        String value = pagingRequest.getSearch()
                                    .getValue();

        return assignment -> Long.toString(assignment.getId()).toLowerCase().contains(value)
                || assignment.getStudentName().toLowerCase().contains(value)
                || Integer.toString(assignment.getStudentNumber()).contains(value)
                || assignment.getSubjectName().toLowerCase().contains(value)
                || assignment.getTeacherName().toLowerCase().contains(value);
    }

    private Comparator<CourseStudent> sortAssignments(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder()
                                       .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                                         .get(columnIndex);

            Comparator<CourseStudent> comparator = CourseStudentComparator.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }
	
	private List<String> toStringList(CourseStudent courseStudent) {
        return Arrays.asList(Long.toString(courseStudent.getId()), courseStudent.getStudentName(), Integer.toString(courseStudent.getStudentNumber()), courseStudent.getSubjectName(), courseStudent.getTeacherName());
    }
}
