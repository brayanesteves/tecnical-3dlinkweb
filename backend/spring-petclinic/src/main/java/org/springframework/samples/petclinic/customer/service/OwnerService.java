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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.customer.domain.OwnerModel;
import org.springframework.samples.petclinic.customer.port.in.OwnerUseCase;
import org.springframework.samples.petclinic.customer.port.out.OwnerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OwnerService implements OwnerUseCase {

	private final OwnerRepositoryPort ownerRepositoryPort;

	public OwnerService(OwnerRepositoryPort ownerRepositoryPort) {
		this.ownerRepositoryPort = ownerRepositoryPort;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<OwnerModel> findById(Integer id) {
		return this.ownerRepositoryPort.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OwnerModel> findByLastName(String lastName) {
		return this.ownerRepositoryPort.findByLastName(lastName);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OwnerModel> findByLastName(String lastName, Pageable pageable) {
		return this.ownerRepositoryPort.findByLastName(lastName, pageable);
	}

	@Override
	public OwnerModel createOwner(OwnerModel owner) {
		owner.setId(null);
		return this.ownerRepositoryPort.save(owner);
	}

	@Override
	public OwnerModel updateOwner(Integer id, OwnerModel owner) {
		owner.setId(id);
		return this.ownerRepositoryPort.save(owner);
	}

}
