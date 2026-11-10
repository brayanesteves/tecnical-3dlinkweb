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

@Schema(description = "Pet details")
public class PetResponseDto {

	@Schema(description = "Pet identifier", example = "1")
	private Integer id;

	@Schema(description = "Name of the pet", example = "Leo")
	private String name;

	@Schema(description = "Birth date of the pet", example = "2020-09-07")
	private LocalDate birthDate;

	@Schema(description = "Type/species of the pet")
	private PetTypeDto type;

	@Schema(description = "Owner identifier", example = "1")
	private Integer ownerId;

	public PetResponseDto() {
	}

	public PetResponseDto(Integer id, String name, LocalDate birthDate, PetTypeDto type, Integer ownerId) {
		this.id = id;
		this.name = name;
		this.birthDate = birthDate;
		this.type = type;
		this.ownerId = ownerId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public PetTypeDto getType() {
		return this.type;
	}

	public void setType(PetTypeDto type) {
		this.type = type;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

}
