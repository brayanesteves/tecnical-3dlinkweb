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
package org.springframework.samples.petclinic.vet.service;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.vet.domain.SpecialtyModel;
import org.springframework.samples.petclinic.vet.domain.VetModel;
import org.springframework.samples.petclinic.vet.port.out.VetRepositoryPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

	@Mock
	private VetRepositoryPort vetRepositoryPort;

	@InjectMocks
	private VetService vetService;

	@Test
	void shouldReturnAllVets() {
		VetModel vet = new VetModel(1, "James", "Carter", Set.of(new SpecialtyModel(1, "radiology")));
		when(this.vetRepositoryPort.findAll()).thenReturn(List.of(vet));

		List<VetModel> result = this.vetService.getAllVets();

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getFirstName()).isEqualTo("James");
	}

}
