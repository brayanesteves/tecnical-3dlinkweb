/*
 * Copyright 2012-2025 the original author or authors.
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
package org.springframework.samples.petclinic.visit.adapter.in.web.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for booking a visit")
public class VisitRequestDto {

	@NotNull(message = "Visit date is required")
	@Future(message = "Visit date must be in the future")
	@Schema(description = "Date of visit (must be in the future)", example = "2026-12-01")
	private LocalDate date;

	@NotBlank(message = "Description must not be blank")
	@Size(max = 255, message = "Description must be at most 255 characters")
	@Schema(description = "Description or reason for the visit", example = "Regular vaccination checkup")
	private String description;

	public VisitRequestDto() {
	}

	public VisitRequestDto(LocalDate date, String description) {
		this.date = date;
		this.description = description;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
