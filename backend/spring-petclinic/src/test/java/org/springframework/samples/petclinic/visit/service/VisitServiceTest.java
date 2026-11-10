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
package org.springframework.samples.petclinic.visit.service;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.visit.domain.VisitModel;
import org.springframework.samples.petclinic.visit.port.out.VisitRepositoryPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

	@Mock
	private VisitRepositoryPort visitRepositoryPort;

	private VisitService visitService;

	@BeforeEach
	void setUp() {
		this.visitService = new VisitService(this.visitRepositoryPort);
	}

	@Test
	void shouldBookVisitWhenDateIsInFuture() {
		LocalDate futureDate = LocalDate.now().plusDays(3);
		VisitModel input = new VisitModel(null, futureDate, "Checkup", 1);
		VisitModel saved = new VisitModel(10, futureDate, "Checkup", 1);

		when(this.visitRepositoryPort.save(eq(1), eq(1), any(VisitModel.class))).thenReturn(saved);

		VisitModel result = this.visitService.bookVisit(1, 1, input);

		assertThat(result.getId()).isEqualTo(10);
		verify(this.visitRepositoryPort).save(1, 1, input);
	}

	@Test
	void shouldThrowExceptionWhenVisitDateIsInPast() {
		LocalDate pastDate = LocalDate.now().minusDays(1);
		VisitModel input = new VisitModel(null, pastDate, "Checkup", 1);

		assertThatThrownBy(() -> this.visitService.bookVisit(1, 1, input)).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("Visit date must be in the future");
	}

	@Test
	void shouldGetVisitsByPet() {
		VisitModel visit = new VisitModel(1, LocalDate.now().plusDays(2), "Checkup", 5);
		when(this.visitRepositoryPort.findByPetId(2, 5)).thenReturn(List.of(visit));

		List<VisitModel> list = this.visitService.getVisitsByPet(2, 5);

		assertThat(list).hasSize(1);
		assertThat(list.get(0).getDescription()).isEqualTo("Checkup");
	}

}
