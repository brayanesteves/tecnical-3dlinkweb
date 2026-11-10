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
package org.springframework.samples.petclinic.vet.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class VetModel implements Serializable {

	private Integer id;

	private String firstName;

	private String lastName;

	private Set<SpecialtyModel> specialties = new HashSet<>();

	public VetModel() {
	}

	public VetModel(Integer id, String firstName, String lastName, Set<SpecialtyModel> specialties) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		if (specialties != null) {
			this.specialties = new HashSet<>(specialties);
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

	public Set<SpecialtyModel> getSpecialties() {
		return this.specialties;
	}

	public void setSpecialties(Set<SpecialtyModel> specialties) {
		this.specialties = specialties != null ? new HashSet<>(specialties) : new HashSet<>();
	}

	public int getNrOfSpecialties() {
		return this.specialties.size();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VetModel vetModel = (VetModel) o;
		return Objects.equals(this.id, vetModel.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

}
