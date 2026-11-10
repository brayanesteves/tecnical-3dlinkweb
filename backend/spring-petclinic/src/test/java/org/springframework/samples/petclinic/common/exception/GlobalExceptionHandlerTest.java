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
package org.springframework.samples.petclinic.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
			.setControllerAdvice(new GlobalExceptionHandler())
			.build();
	}

	@Test
	void shouldHandleNotFoundException() throws Exception {
		this.mockMvc.perform(get("/test-errors/not-found").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.title").value("Resource Not Found"))
			.andExpect(jsonPath("$.detail").value("Owner not found with id: 99"));
	}

	@Test
	void shouldHandleBadRequestException() throws Exception {
		this.mockMvc.perform(get("/test-errors/bad-argument").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.title").value("Invalid Argument"))
			.andExpect(jsonPath("$.detail").value("Invalid input value"));
	}

	@Test
	void shouldHandleValidationException() throws Exception {
		this.mockMvc
			.perform(post("/test-errors/validation").contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\"}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.title").value("Validation Error"))
			.andExpect(jsonPath("$.errors.name").value("must not be blank"));
	}

	@Test
	void shouldHandleGenericException() throws Exception {
		this.mockMvc.perform(get("/test-errors/server-error").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.title").value("Internal Server Error"));
	}

	@RestController
	@RequestMapping("/test-errors")
	static class DummyController {

		@GetMapping("/not-found")
		public void notFound() {
			throw new IllegalArgumentException("Owner not found with id: 99");
		}

		@GetMapping("/bad-argument")
		public void badArgument() {
			throw new IllegalArgumentException("Invalid input value");
		}

		@GetMapping("/server-error")
		public void serverError() {
			throw new RuntimeException("Unexpected error");
		}

		@PostMapping("/validation")
		public void validate(@Valid @RequestBody DummyDto dto) {
		}

	}

	static class DummyDto {

		@NotBlank(message = "must not be blank")
		private String name;

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
