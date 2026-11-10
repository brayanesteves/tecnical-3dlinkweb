/*
 * Copyright 2012-2025 the original author or authors.
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
package org.springframework.samples.petclinic.visit.adapter.out.persistence;

import java.util.Collections;
import java.util.List;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.visit.domain.VisitModel;
import org.springframework.samples.petclinic.visit.port.out.VisitRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class VisitPersistenceAdapter implements VisitRepositoryPort {

	private final OwnerRepository ownerRepository;

	public VisitPersistenceAdapter(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Override
	public VisitModel save(int ownerId, int petId, VisitModel visitModel) {
		Owner owner = this.ownerRepository.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException("Pet not found with id: " + petId);
		}

		Visit visit = new Visit();
		visit.setDate(visitModel.getDate());
		visit.setDescription(visitModel.getDescription());

		owner.addVisit(petId, visit);
		this.ownerRepository.save(owner);

		return new VisitModel(visit.getId(), visit.getDate(), visit.getDescription(), petId);
	}

	@Override
	public List<VisitModel> findByPetId(int ownerId, int petId) {
		Owner owner = this.ownerRepository.findById(ownerId)
			.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException("Pet not found with id: " + petId);
		}

		if (pet.getVisits() == null) {
			return Collections.emptyList();
		}

		return pet.getVisits()
			.stream()
			.map(v -> new VisitModel(v.getId(), v.getDate(), v.getDescription(), petId))
			.toList();
	}

}
