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

import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Owner profile details including registered pets")
public class OwnerResponseDto {

	@Schema(description = "Owner unique identifier", example = "1")
	private Integer id;

	@Schema(description = "First name of the owner", example = "George")
	private String firstName;

	@Schema(description = "Last name of the owner", example = "Franklin")
	private String lastName;

	@Schema(description = "Street address", example = "110 W. Liberty St.")
	private String address;

	@Schema(description = "City name", example = "Madison")
	private String city;

	@Schema(description = "Contact telephone number", example = "6085551023")
	private String telephone;

	@Schema(description = "List of pets belonging to this owner")
	private List<PetResponseDto> pets = new ArrayList<>();

	public OwnerResponseDto() {
	}

	public OwnerResponseDto(Integer id, String firstName, String lastName, String address, String city,
			String telephone, List<PetResponseDto> pets) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.telephone = telephone;
		if (pets != null) {
			this.pets = pets;
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

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<PetResponseDto> getPets() {
		return this.pets;
	}

	public void setPets(List<PetResponseDto> pets) {
		this.pets = (pets != null) ? pets : new ArrayList<>();
	}

}
