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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customer.domain.OwnerModel;
import org.springframework.samples.petclinic.customer.domain.PetModel;
import org.springframework.samples.petclinic.customer.domain.PetTypeModel;
import org.springframework.samples.petclinic.customer.port.in.OwnerUseCase;
import org.springframework.samples.petclinic.customer.port.in.PetUseCase;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetRestController.class)
class PetRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PetUseCase petUseCase;

	@MockitoBean
	private OwnerUseCase ownerUseCase;

	@Test
	void shouldReturnAllPetTypes() throws Exception {
		when(this.petUseCase.getAllPetTypes())
			.thenReturn(List.of(new PetTypeModel(1, "cat"), new PetTypeModel(2, "dog")));

		this.mockMvc.perform(get("/api/v1/pet-types").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].name").value("cat"))
			.andExpect(jsonPath("$[1].name").value("dog"));
	}

	@Test
	void shouldAddPetToOwner() throws Exception {
		OwnerModel owner = new OwnerModel(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
		when(this.ownerUseCase.findById(1)).thenReturn(Optional.of(owner));
		when(this.petUseCase.getAllPetTypes()).thenReturn(List.of(new PetTypeModel(1, "cat")));

		PetModel created = new PetModel(10, "Leo", LocalDate.now().minusYears(1), new PetTypeModel(1, "cat"), 1);
		when(this.petUseCase.addPetToOwner(eq(1), any(PetModel.class))).thenReturn(created);

		String requestJson = """
				{
				  "name": "Leo",
				  "birthDate": "2023-01-01",
				  "typeId": 1
				}
				""";

		this.mockMvc.perform(post("/api/v1/owners/1/pets").contentType(MediaType.APPLICATION_JSON).content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(10))
			.andExpect(jsonPath("$.name").value("Leo"));
	}

}
