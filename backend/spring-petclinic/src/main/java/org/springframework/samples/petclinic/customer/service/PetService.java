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
package org.springframework.samples.petclinic.customer.service;

import java.util.List;
import java.util.Optional;
import org.springframework.samples.petclinic.customer.domain.PetModel;
import org.springframework.samples.petclinic.customer.domain.PetTypeModel;
import org.springframework.samples.petclinic.customer.port.in.PetUseCase;
import org.springframework.samples.petclinic.customer.port.out.PetRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PetService implements PetUseCase {

	private final PetRepositoryPort petRepositoryPort;

	public PetService(PetRepositoryPort petRepositoryPort) {
		this.petRepositoryPort = petRepositoryPort;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PetModel> findById(Integer id) {
		return this.petRepositoryPort.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PetModel> findByOwnerId(Integer ownerId) {
		return this.petRepositoryPort.findByOwnerId(ownerId);
	}

	@Override
	public PetModel addPetToOwner(Integer ownerId, PetModel pet) {
		pet.setId(null);
		pet.setOwnerId(ownerId);
		return this.petRepositoryPort.save(pet);
	}

	@Override
	public PetModel updatePet(Integer ownerId, Integer petId, PetModel pet) {
		pet.setId(petId);
		pet.setOwnerId(ownerId);
		return this.petRepositoryPort.save(pet);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PetTypeModel> getAllPetTypes() {
		return this.petRepositoryPort.findPetTypes();
	}

}
