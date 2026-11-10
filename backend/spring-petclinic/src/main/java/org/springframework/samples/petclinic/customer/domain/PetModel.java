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
package org.springframework.samples.petclinic.customer.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class PetModel implements Serializable {

	private Integer id;

	private String name;

	private LocalDate birthDate;

	private PetTypeModel type;

	private Integer ownerId;

	public PetModel() {
	}

	public PetModel(Integer id, String name, LocalDate birthDate, PetTypeModel type, Integer ownerId) {
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

	public PetTypeModel getType() {
		return this.type;
	}

	public void setType(PetTypeModel type) {
		this.type = type;
	}

	public Integer getOwnerId() {
		return this.ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PetModel petModel = (PetModel) o;
		return Objects.equals(this.id, petModel.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

}
