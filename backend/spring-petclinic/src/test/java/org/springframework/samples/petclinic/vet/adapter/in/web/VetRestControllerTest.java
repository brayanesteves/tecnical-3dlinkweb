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
package org.springframework.samples.petclinic.vet.adapter.in.web;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.common.exception.GlobalExceptionHandler;
import org.springframework.samples.petclinic.vet.domain.SpecialtyModel;
import org.springframework.samples.petclinic.vet.domain.VetModel;
import org.springframework.samples.petclinic.vet.port.in.GetVetsUseCase;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetRestController.class)
@Import(GlobalExceptionHandler.class)
class VetRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private GetVetsUseCase getVetsUseCase;

	@Test
	void shouldReturnVetsListAsJson() throws Exception {
		VetModel vet = new VetModel(1, "Helen", "Leary", Set.of(new SpecialtyModel(1, "radiology")));
		when(this.getVetsUseCase.getAllVets()).thenReturn(List.of(vet));

		this.mockMvc.perform(get("/api/v1/vets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$[0].id").value(1))
			.andExpect(jsonPath("$[0].firstName").value("Helen"))
			.andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
	}

}
