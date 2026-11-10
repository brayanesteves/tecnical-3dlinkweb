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

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating or updating an owner")
public class OwnerRequestDto {

	@NotBlank(message = "First name is mandatory")
	@Size(max = 255, message = "First name must not exceed 255 characters")
	@Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "First name contains invalid characters")
	@Schema(description = "First name of the owner", example = "George")
	private String firstName;

	@NotBlank(message = "Last name is mandatory")
	@Size(max = 255, message = "Last name must not exceed 255 characters")
	@Pattern(regexp = "^[a-zA-Z\\s'-]+$", message = "Last name contains invalid characters")
	@Schema(description = "Last name of the owner", example = "Franklin")
	private String lastName;

	@NotBlank(message = "Address is mandatory")
	@Size(max = 255, message = "Address must not exceed 255 characters")
	@Schema(description = "Street address", example = "110 W. Liberty St.")
	private String address;

	@NotBlank(message = "City is mandatory")
	@Size(max = 255, message = "City must not exceed 255 characters")
	@Schema(description = "City name", example = "Madison")
	private String city;

	@NotBlank(message = "Telephone is mandatory")
	@Digits(fraction = 0, integer = 10, message = "Telephone must be a 10-digit number")
	@Schema(description = "10-digit contact telephone number", example = "6085551023")
	private String telephone;

	public OwnerRequestDto() {
	}

	public OwnerRequestDto(String firstName, String lastName, String address, String city, String telephone) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.telephone = telephone;
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

}
