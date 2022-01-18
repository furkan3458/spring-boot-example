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
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;


import com.example.model.Teacher;
import com.example.repository.TeacherRepository;
import com.example.comparator.TeacherComparator;
import com.example.table.model.Column;
import com.example.table.model.Order;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TeacherService {

	private static final Comparator<Teacher> EMPTY_COMPARATOR = (e1, e2) -> 0;
	
	private TeacherRepository teacherRepo;

	@Autowired
	public TeacherService(TeacherRepository teacherRepo) {
		this.teacherRepo = teacherRepo;
	}
	
	private HashMap<String,Object> getResultArray(List<Teacher> teacher){
		HashMap<String,Object> result = new HashMap<>();
		
		if(teacher.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Teacher(s) not found.");
		}
		else
			result.put("error", false);
		
		result.put("values", teacher);
		
		return result;
	}
	
	private HashMap<String,Object> getResultArray(Optional<Teacher> teacher){
		HashMap<String,Object> result = new HashMap<>();
		
		result.put("error", false);
		result.put("values", new int[0]);
		
		if(teacher.isEmpty()) {
			result.replace("error", true);
			result.put("errorMsg", "Teacher not found.");
		}
		else
			result.replace("values", teacher.get());
		
		return result;
	}
	
	public Page<Teacher> getAll(PagingRequest pagingRequest){
		List<Teacher> teacher = teacherRepo.findAll();
		
		List<Teacher> filtered = teacher.stream().sorted(sortTeachers(pagingRequest))
												.filter(filterTeachers(pagingRequest))
												.skip(pagingRequest.getStart())
		                                        .limit(pagingRequest.getLength())
		                                        .collect(Collectors.toList());
		
		long count = teacher.stream()
                .filter(filterTeachers(pagingRequest))
                .count();
		
		Page<Teacher> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
	}
	
	public PageArray getAllArray(PagingRequest pagingRequest) {
		pagingRequest.setColumns(Stream.of("id", "name").map(Column::new).collect(Collectors.toList()));
		Page<Teacher> teacherPage = getAll(pagingRequest);
		
		PageArray pageArray = new PageArray();
		pageArray.setRecordsFiltered(teacherPage.getRecordsFiltered());
		pageArray.setDraw(teacherPage.getDraw());
		pageArray.setRecordsTotal(teacherPage.getRecordsTotal());
		pageArray.setData(teacherPage.getData().stream().map(this::toStringList).collect(Collectors.toList()));
		
		return pageArray;
	}
	
	public HashMap<String, Object> getAll(){
		List<Teacher> teacher = teacherRepo.findAll();
		
		return getResultArray(teacher);
	}
	
	public HashMap<String, Object> getById(Long id){
		Optional<Teacher> teacher = teacherRepo.findById(id);		
		
		return getResultArray(teacher);
	}
	
	public HashMap<String, Object> insert(Teacher teacher){
		HashMap<String,Object> result = new HashMap<>();
		
		teacher.setId(0);
		
		try {
			teacherRepo.save(teacher);
			result.put("error", false);
			result.put("errorMsg", "Teacher successfully inserted.");
		}
		catch(ConstraintViolationException e) {
			result.put("error", true);
			result.put("errorMsg", "Duplicated teacher.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> update(Teacher teacher){
		HashMap<String,Object> result = new HashMap<>();
		
		Optional<Teacher> tea = teacherRepo.findById(teacher.getId());
		
		if(tea.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Teacher not found.");
		}
		else {
			try {
				teacherRepo.saveAndFlush(teacher);
				result.put("error", false);
				result.put("errorMsg", "Teacher successfully updated.");
			}
			catch(ConstraintViolationException e) {
				result.put("error", true);
				result.put("errorMsg", "Teacher not found.");
			}
		}

		return result;
	}
	
	public HashMap<String, Object> delete(Teacher teacher){
		HashMap<String,Object> result = new HashMap<>();
		
		try{
			teacherRepo.deleteById(teacher.getId());
			result.put("error", false);
			result.put("errorMsg", "Teacher successfully deleted.");
		}
		catch(IllegalArgumentException e) {
			result.put("error", true);
			result.put("errorMsg", "Teacher not found or null.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> deleteAll(){
		HashMap<String,Object> result = new HashMap<>();

		teacherRepo.deleteAll();
		result.put("error", false);
		result.put("errorMsg", "Teacher successfully deleted.");
		
		return result;
	}
	
	private Predicate<Teacher> filterTeachers(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch()
                                                                                  .getValue())) {
            return teacher -> true;
        }

        String value = pagingRequest.getSearch()
                                    .getValue();

        return teacher ->  Long.toString(teacher.getId()).contains(value)
        		|| teacher.getName().toLowerCase().contains(value);
    }

    private Comparator<Teacher> sortTeachers(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder()
                                       .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                                         .get(columnIndex);

            Comparator<Teacher> comparator = TeacherComparator.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }
	
	private List<String> toStringList(Teacher teacher) {
        return Arrays.asList(Long.toString(teacher.getId()), teacher.getName());
    }
}
