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
package org.springframework.samples.petclinic.customer.adapter.in.web.dto;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating or updating a pet")
public class PetRequestDto {

	@NotBlank(message = "Pet name is mandatory")
	@Size(max = 255, message = "Pet name must not exceed 255 characters")
	@Schema(description = "Name of the pet", example = "Leo")
	private String name;

	@NotNull(message = "Birth date is mandatory")
	@PastOrPresent(message = "Birth date cannot be in the future")
	@Schema(description = "Birth date of the pet", example = "2020-09-07")
	private LocalDate birthDate;

	@NotNull(message = "Pet type ID is mandatory")
	@Schema(description = "Identifier of the pet type", example = "1")
	private Integer typeId;

	public PetRequestDto() {
	}

	public PetRequestDto(String name, LocalDate birthDate, Integer typeId) {
		this.name = name;
		this.birthDate = birthDate;
		this.typeId = typeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getTypeId() {
		return this.typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

}
