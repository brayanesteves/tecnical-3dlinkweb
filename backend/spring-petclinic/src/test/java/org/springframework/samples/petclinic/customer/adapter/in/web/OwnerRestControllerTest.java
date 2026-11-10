/*
 * Copyright 2012-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.customer.adapter.in.web;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.common.exception.GlobalExceptionHandler;
import org.springframework.samples.petclinic.customer.domain.OwnerModel;
import org.springframework.samples.petclinic.customer.port.in.OwnerUseCase;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OwnerRestController.class)
@Import(GlobalExceptionHandler.class)
class OwnerRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerUseCase ownerUseCase;

	@Test
	void shouldGetOwnerById() throws Exception {
		OwnerModel owner = new OwnerModel(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
		when(this.ownerUseCase.findById(1)).thenReturn(Optional.of(owner));

		this.mockMvc.perform(get("/api/v1/owners/1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.firstName").value("George"))
			.andExpect(jsonPath("$.telephone").value("6085551023"));
	}

	@Test
	void shouldReturn404WhenOwnerNotFound() throws Exception {
		when(this.ownerUseCase.findById(999)).thenReturn(Optional.empty());

		this.mockMvc.perform(get("/api/v1/owners/999").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void shouldCreateOwnerSuccessfully() throws Exception {
		OwnerModel created = new OwnerModel(1, "Betty", "Davis", "638 Cardinal Ave.", "Sun Prairie", "6085551749");
		when(this.ownerUseCase.createOwner(any(OwnerModel.class))).thenReturn(created);

		String requestJson = """
				{
				  "firstName": "Betty",
				  "lastName": "Davis",
				  "address": "638 Cardinal Ave.",
				  "city": "Sun Prairie",
				  "telephone": "6085551749"
				}
				""";

		this.mockMvc.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON).content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.firstName").value("Betty"));
	}

	@Test
	void shouldRejectOwnerWithInvalidTelephone() throws Exception {
		String invalidJson = """
				{
				  "firstName": "Betty",
				  "lastName": "Davis",
				  "address": "638 Cardinal Ave.",
				  "city": "Sun Prairie",
				  "telephone": "not-digits"
				}
				""";

		this.mockMvc.perform(post("/api/v1/owners").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

}
