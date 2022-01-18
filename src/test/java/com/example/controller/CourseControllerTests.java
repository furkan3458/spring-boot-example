package com.example.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTests {

	@Autowired
	MockMvc mock;
	
	@Test
	public void course() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course");
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
	
	@Test
	public void courseById() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course/1");
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
	
	@Test
	public void courseByTeacher() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course/find")
				.param("teacher", "1");	
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
	
	@Test
	public void courseBySubject() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course/find")
				.param("subject", "1");
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
	
	@Test
	public void courseByTeacherAndSubject() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course/find")
				.param("teacher","1")
				.param("subject","1");
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
	
	@Test
	public void courseByTeacherAll() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course/findAll")
				.param("teacher", "1");
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
	
	@Test
	public void courseBySubjectAll() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course/findAll")
				.param("subject", "1");
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
	
	@Test
	public void courseByTeacherAndSubjectAll() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/obs/course/findAll")
				.param("teacher", "1")
				.param("subject", "1");
		
		mock.perform(request).andReturn().getResponse().getContentAsString();
	}
}
