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

import com.example.comparator.SubjectComparator;
import com.example.model.Subject;
import com.example.repository.SubjectRepository;
import com.example.table.model.Column;
import com.example.table.model.Order;
import com.example.table.model.Page;
import com.example.table.model.PageArray;
import com.example.table.model.PagingRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubjectService {

	private static final Comparator<Subject> EMPTY_COMPARATOR = (e1, e2) -> 0;
	
	private SubjectRepository subjectRepo;

	@Autowired
	public SubjectService(SubjectRepository subjectRepo) {
		this.subjectRepo = subjectRepo;
	}
	
	private HashMap<String,Object> getResultArray(List<Subject> subject){
		HashMap<String,Object> result = new HashMap<>();
		
		if(subject.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Subject(s) not found.");
		}
		else
			result.put("error", false);
		
		result.put("values", subject);
		
		return result;
	}
	
	private HashMap<String,Object> getResultArray(Optional<Subject> subject){
		HashMap<String,Object> result = new HashMap<>();
		
		result.put("error", false);
		result.put("values", new int[0]);
		
		if(subject.isEmpty()) {
			result.replace("error", true);
			result.put("errorMsg", "Subject not found.");
		}
		else
			result.replace("values", subject.get());
		
		return result;
	}
	
	public Page<Subject> getAll(PagingRequest pagingRequest){
		List<Subject> subject = subjectRepo.findAll();
		
		List<Subject> filtered = subject.stream().sorted(sortSubjects(pagingRequest))
												.filter(filterSubjects(pagingRequest))
												.skip(pagingRequest.getStart())
		                                        .limit(pagingRequest.getLength())
		                                        .collect(Collectors.toList());
		
		long count = subject.stream()
                .filter(filterSubjects(pagingRequest))
                .count();
		
		Page<Subject> page = new Page<>(filtered);
        page.setRecordsFiltered((int) count);
        page.setRecordsTotal((int) count);
        page.setDraw(pagingRequest.getDraw());

        return page;
	}
	
	public PageArray getAllArray(PagingRequest pagingRequest) {
		pagingRequest.setColumns(Stream.of("id", "subject").map(Column::new).collect(Collectors.toList()));
		Page<Subject> subjectPage = getAll(pagingRequest);
		
		PageArray pageArray = new PageArray();
		pageArray.setRecordsFiltered(subjectPage.getRecordsFiltered());
		pageArray.setDraw(subjectPage.getDraw());
		pageArray.setRecordsTotal(subjectPage.getRecordsTotal());
		pageArray.setData(subjectPage.getData().stream().map(this::toStringList).collect(Collectors.toList()));
		
		return pageArray;
	}
	
	public HashMap<String, Object> getAll(){
		List<Subject> subject = subjectRepo.findAll();
		
		return getResultArray(subject);
	}
	
	public HashMap<String, Object> getById(Long id){
		
		Optional<Subject> subject = subjectRepo.findById(id);
		
		return getResultArray(subject);
	}
	
	public HashMap<String, Object> insert(Subject subject){
		HashMap<String,Object> result = new HashMap<>();
		
		subject.setId(0);
		
		try {
			subjectRepo.save(subject);
			result.put("error", false);
			result.put("errorMsg", "Subject successfully inserted.");
		}
		catch(ConstraintViolationException e) {
			result.put("error", true);
			result.put("errorMsg", "Duplicated subject.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> update(Subject subject){
		HashMap<String,Object> result = new HashMap<>();
		
		Optional<Subject> sub = subjectRepo.findById(subject.getId());
		
		if(sub.isEmpty()) {
			result.put("error", true);
			result.put("errorMsg", "Subject not found.");
		}
		else {
			try {
				subjectRepo.saveAndFlush(subject);
				result.put("error", false);
				result.put("errorMsg", "Subject successfully updated.");
			}
			catch(ConstraintViolationException e) {
				result.put("error", true);
				result.put("errorMsg", "Subject not found.");
			}
		}

		return result;
	}
	
	public HashMap<String, Object> delete(Subject subject){
		HashMap<String,Object> result = new HashMap<>();
		
		try{
			subjectRepo.deleteById(subject.getId());
			result.put("error", false);
			result.put("errorMsg", "Subject successfully deleted.");
		}
		catch(IllegalArgumentException e) {
			result.put("error", true);
			result.put("errorMsg", "Subject not found or null.");
		}
		
		return result;
	}
	
	public HashMap<String, Object> deleteAll(){
		HashMap<String,Object> result = new HashMap<>();
		
		subjectRepo.deleteAll();
		result.put("error", false);
		result.put("errorMsg", "Subject(s) successfully deleted.");
		
		return result;
	}
	
	private Predicate<Subject> filterSubjects(PagingRequest pagingRequest) {
        if (pagingRequest.getSearch() == null || StringUtils.isEmpty(pagingRequest.getSearch()
                                                                                  .getValue())) {
            return subject -> true;
        }

        String value = pagingRequest.getSearch()
                                    .getValue();

        return subject -> Long.toString(subject.getId()).contains(value)
        		|| subject.getSubject().toLowerCase().contains(value);
    }

    private Comparator<Subject> sortSubjects(PagingRequest pagingRequest) {
        if (pagingRequest.getOrder() == null) {
            return EMPTY_COMPARATOR;
        }

        try {
            Order order = pagingRequest.getOrder()
                                       .get(0);

            int columnIndex = order.getColumn();
            Column column = pagingRequest.getColumns()
                                         .get(columnIndex);

            Comparator<Subject> comparator = SubjectComparator.getComparator(column.getData(), order.getDir());
            if (comparator == null) {
                return EMPTY_COMPARATOR;
            }

            return comparator;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return EMPTY_COMPARATOR;
    }
	
	private List<String> toStringList(Subject subject) {
        return Arrays.asList(Long.toString(subject.getId()), subject.getSubject());
    }
}
