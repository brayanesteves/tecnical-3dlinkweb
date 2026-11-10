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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.customer.domain.OwnerModel;
import org.springframework.samples.petclinic.customer.domain.PetModel;
import org.springframework.samples.petclinic.customer.domain.PetTypeModel;
import org.springframework.samples.petclinic.customer.port.out.OwnerRepositoryPort;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OwnerPersistenceAdapter implements OwnerRepositoryPort {

	private final OwnerRepository ownerRepository;

	public OwnerPersistenceAdapter(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Override
	public Optional<OwnerModel> findById(Integer id) {
		return this.ownerRepository.findById(id).map(this::toModel);
	}

	@Override
	public List<OwnerModel> findByLastName(String lastName) {
		return this.ownerRepository.findByLastNameStartingWith(lastName, Pageable.unpaged())
			.getContent()
			.stream()
			.map(this::toModel)
			.toList();
	}

	@Override
	public Page<OwnerModel> findByLastName(String lastName, Pageable pageable) {
		return this.ownerRepository.findByLastNameStartingWith(lastName, pageable).map(this::toModel);
	}

	@Override
	public OwnerModel save(OwnerModel model) {
		Owner entity = (model.getId() != null) ? this.ownerRepository.findById(model.getId()).orElse(new Owner())
				: new Owner();
		entity.setFirstName(model.getFirstName());
		entity.setLastName(model.getLastName());
		entity.setAddress(model.getAddress());
		entity.setCity(model.getCity());
		entity.setTelephone(model.getTelephone());

		Owner saved = this.ownerRepository.save(entity);
		return toModel(saved);
	}

	private OwnerModel toModel(Owner entity) {
		OwnerModel model = new OwnerModel(entity.getId(), entity.getFirstName(), entity.getLastName(),
				entity.getAddress(), entity.getCity(), entity.getTelephone());
		if (entity.getPets() != null) {
			List<PetModel> pets = entity.getPets().stream().map(pet -> {
				PetTypeModel type = (pet.getType() != null)
						? new PetTypeModel(pet.getType().getId(), pet.getType().getName()) : null;
				return new PetModel(pet.getId(), pet.getName(), pet.getBirthDate(), type, entity.getId());
			}).toList();
			model.setPets(pets);
		}
		return model;
	}

}
