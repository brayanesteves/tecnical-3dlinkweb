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
package org.springframework.samples.petclinic.vet.adapter.out.persistence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.vet.domain.SpecialtyModel;
import org.springframework.samples.petclinic.vet.domain.VetModel;
import org.springframework.samples.petclinic.vet.port.out.VetRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class VetPersistenceAdapter implements VetRepositoryPort {

	private final VetRepository vetRepository;

	public VetPersistenceAdapter(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	@Override
	public List<VetModel> findAll() {
		return this.vetRepository.findAll().stream().map(vet -> {
			Set<SpecialtyModel> specialties = vet.getSpecialties()
				.stream()
				.map(s -> new SpecialtyModel(s.getId(), s.getName()))
				.collect(Collectors.toSet());
			return new VetModel(vet.getId(), vet.getFirstName(), vet.getLastName(), specialties);
		}).toList();
	}

}
