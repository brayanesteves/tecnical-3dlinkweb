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
package org.springframework.samples.petclinic.visit.adapter.in.web;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.visit.domain.VisitModel;
import org.springframework.samples.petclinic.visit.port.in.VisitUseCase;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitRestController.class)
class VisitRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VisitUseCase visitUseCase;

	@Test
	void shouldBookVisit() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(5);
		VisitModel createdVisit = new VisitModel(1, futureDate, "Routine checkup", 7);

		when(this.visitUseCase.bookVisit(eq(1), eq(7), any(VisitModel.class))).thenReturn(createdVisit);

		String requestJson = String.format("{\"date\":\"%s\",\"description\":\"Routine checkup\"}", futureDate);

		this.mockMvc
			.perform(
					post("/api/v1/owners/1/pets/7/visits").contentType(MediaType.APPLICATION_JSON).content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.description").value("Routine checkup"))
			.andExpect(jsonPath("$.petId").value(7));
	}

	@Test
	void shouldRejectVisitWithBlankDescription() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(5);
		String requestJson = String.format("{\"date\":\"%s\",\"description\":\"\"}", futureDate);

		this.mockMvc
			.perform(
					post("/api/v1/owners/1/pets/7/visits").contentType(MediaType.APPLICATION_JSON).content(requestJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void shouldGetVisitsForPet() throws Exception {
		VisitModel visit1 = new VisitModel(1, LocalDate.of(2026, 12, 1), "Vaccination", 7);
		VisitModel visit2 = new VisitModel(2, LocalDate.of(2026, 12, 15), "Checkup", 7);

		when(this.visitUseCase.getVisitsByPet(1, 7)).thenReturn(List.of(visit1, visit2));

		this.mockMvc.perform(get("/api/v1/owners/1/pets/7/visits").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].description").value("Vaccination"))
			.andExpect(jsonPath("$[1].description").value("Checkup"));
	}

}
