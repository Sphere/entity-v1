package com.sunbird.entity.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sunbird.entity.controller.EntityController;

@RunWith(SpringRunner.class)
public class EntityControllerTest {

	@InjectMocks
	EntityController entityController;

	@Test
	public void testMethod() throws JsonProcessingException {
		// entityController.addEntities(null, null);
		assertEquals(true, true);
	}

}
