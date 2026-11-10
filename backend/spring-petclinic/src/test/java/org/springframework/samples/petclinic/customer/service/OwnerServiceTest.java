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

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.customer.domain.OwnerModel;
import org.springframework.samples.petclinic.customer.port.out.OwnerRepositoryPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

	@Mock
	private OwnerRepositoryPort ownerRepositoryPort;

	@InjectMocks
	private OwnerService ownerService;

	@Test
	void shouldFindOwnerById() {
		OwnerModel owner = new OwnerModel(1, "George", "Franklin", "110 W. Liberty St.", "Madison", "6085551023");
		when(this.ownerRepositoryPort.findById(1)).thenReturn(Optional.of(owner));

		Optional<OwnerModel> result = this.ownerService.findById(1);

		assertThat(result).isPresent();
		assertThat(result.get().getFirstName()).isEqualTo("George");
		assertThat(result.get().getCity()).isEqualTo("Madison");
	}

}
