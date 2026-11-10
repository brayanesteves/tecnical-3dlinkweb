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
package org.springframework.samples.petclinic.vet.adapter.in.web.dto;

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Veterinarian details")
public class VetResponseDto {

	@Schema(description = "Veterinarian unique identifier", example = "1")
	private Integer id;

	@Schema(description = "First name of the veterinarian", example = "James")
	private String firstName;

	@Schema(description = "Last name of the veterinarian", example = "Carter")
	private String lastName;

	@Schema(description = "List of medical specialties")
	private List<SpecialtyResponseDto> specialties = new ArrayList<>();

	public VetResponseDto() {
	}

	public VetResponseDto(Integer id, String firstName, String lastName, List<SpecialtyResponseDto> specialties) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		if (specialties != null) {
			this.specialties = specialties;
		}
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public List<SpecialtyResponseDto> getSpecialties() {
		return this.specialties;
	}

	public void setSpecialties(List<SpecialtyResponseDto> specialties) {
		this.specialties = (specialties != null) ? specialties : new ArrayList<>();
	}

}
