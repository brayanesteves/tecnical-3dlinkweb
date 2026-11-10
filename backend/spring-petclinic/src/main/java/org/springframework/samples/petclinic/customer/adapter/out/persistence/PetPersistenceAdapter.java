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
package org.springframework.samples.petclinic.customer.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.samples.petclinic.customer.domain.PetModel;
import org.springframework.samples.petclinic.customer.domain.PetTypeModel;
import org.springframework.samples.petclinic.customer.port.out.PetRepositoryPort;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class PetPersistenceAdapter implements PetRepositoryPort {

	private final OwnerRepository ownerRepository;

	private final PetTypeRepository petTypeRepository;

	public PetPersistenceAdapter(OwnerRepository ownerRepository, PetTypeRepository petTypeRepository) {
		this.ownerRepository = ownerRepository;
		this.petTypeRepository = petTypeRepository;
	}

	@Override
	public Optional<PetModel> findById(Integer id) {
		return this.ownerRepository.findAll()
			.stream()
			.flatMap(o -> o.getPets().stream().map(p -> toModel(p, o.getId())))
			.filter(p -> p.getId() != null && p.getId().equals(id))
			.findFirst();
	}

	@Override
	public List<PetModel> findByOwnerId(Integer ownerId) {
		return this.ownerRepository.findById(ownerId)
			.map(owner -> owner.getPets().stream().map(p -> toModel(p, ownerId)).toList())
			.orElse(List.of());
	}

	@Override
	public PetModel save(PetModel model) {
		if (model.getOwnerId() == null) {
			throw new IllegalArgumentException("Owner ID must not be null when saving a pet");
		}

		Owner owner = this.ownerRepository.findById(model.getOwnerId())
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + model.getOwnerId()));

		Pet pet;
		if (model.getId() != null) {
			pet = owner.getPet(model.getId());
			if (pet == null) {
				pet = new Pet();
				owner.addPet(pet);
			}
		}
		else {
			pet = new Pet();
			owner.addPet(pet);
		}

		pet.setName(model.getName());
		pet.setBirthDate(model.getBirthDate());

		if (model.getType() != null && model.getType().getId() != null) {
			PetType type = this.petTypeRepository.findById(model.getType().getId()).orElse(null);
			pet.setType(type);
		}

		Owner savedOwner = this.ownerRepository.save(owner);
		Pet savedPet = savedOwner.getPet(model.getName(), false);
		if (savedPet == null && model.getId() != null) {
			savedPet = savedOwner.getPet(model.getId());
		}

		return (savedPet != null) ? toModel(savedPet, model.getOwnerId()) : model;
	}

	@Override
	public List<PetTypeModel> findPetTypes() {
		return this.petTypeRepository.findPetTypes()
			.stream()
			.map(t -> new PetTypeModel(t.getId(), t.getName()))
			.toList();
	}

	@Override
	public Optional<PetTypeModel> findPetTypeById(Integer typeId) {
		return this.petTypeRepository.findById(typeId).map(t -> new PetTypeModel(t.getId(), t.getName()));
	}

	private PetModel toModel(Pet entity, Integer ownerId) {
		PetTypeModel type = (entity.getType() != null)
				? new PetTypeModel(entity.getType().getId(), entity.getType().getName()) : null;
		return new PetModel(entity.getId(), entity.getName(), entity.getBirthDate(), type, ownerId);
	}

}
