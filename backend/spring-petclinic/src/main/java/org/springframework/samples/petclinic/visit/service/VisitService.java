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
package org.springframework.samples.petclinic.visit.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.samples.petclinic.visit.domain.VisitModel;
import org.springframework.samples.petclinic.visit.port.in.VisitUseCase;
import org.springframework.samples.petclinic.visit.port.out.VisitRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class VisitService implements VisitUseCase {

	private final VisitRepositoryPort visitRepositoryPort;

	public VisitService(VisitRepositoryPort visitRepositoryPort) {
		this.visitRepositoryPort = visitRepositoryPort;
	}

	@Override
	public VisitModel bookVisit(int ownerId, int petId, VisitModel visitModel) {
		if (visitModel.getDate() == null) {
			throw new IllegalArgumentException("Visit date cannot be null");
		}
		if (!visitModel.getDate().isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Visit date must be in the future");
		}
		return this.visitRepositoryPort.save(ownerId, petId, visitModel);
	}

	@Override
	public List<VisitModel> getVisitsByPet(int ownerId, int petId) {
		return this.visitRepositoryPort.findByPetId(ownerId, petId);
	}

}
