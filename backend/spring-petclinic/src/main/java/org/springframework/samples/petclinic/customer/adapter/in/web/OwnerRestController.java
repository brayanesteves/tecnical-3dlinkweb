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
package org.springframework.samples.petclinic.customer.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customer.adapter.in.web.dto.OwnerRequestDto;
import org.springframework.samples.petclinic.customer.adapter.in.web.dto.OwnerResponseDto;
import org.springframework.samples.petclinic.customer.adapter.in.web.dto.PetResponseDto;
import org.springframework.samples.petclinic.customer.adapter.in.web.dto.PetTypeDto;
import org.springframework.samples.petclinic.customer.domain.OwnerModel;
import org.springframework.samples.petclinic.customer.port.in.OwnerUseCase;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owners")
@Tag(name = "Owners", description = "Endpoints for managing pet owners and their contact profiles")
public class OwnerRestController {

	private final OwnerUseCase ownerUseCase;

	public OwnerRestController(OwnerUseCase ownerUseCase) {
		this.ownerUseCase = ownerUseCase;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Search owners with pagination",
			description = "Returns a paginated list of owners optionally filtered by starting characters of their last name",
			responses = { @ApiResponse(responseCode = "200", description = "Owners page retrieved successfully") })
	public ResponseEntity<Page<OwnerResponseDto>> findOwners(
			@Parameter(description = "Last name prefix to filter by") @RequestParam(defaultValue = "") String lastName,
			@Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<OwnerModel> owners = this.ownerUseCase.findByLastName(lastName, pageable);
		Page<OwnerResponseDto> dtoPage = owners.map(this::mapToDto);
		return ResponseEntity.ok(dtoPage);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get owner by ID", description = "Returns full owner profile including registered pets",
			responses = {
					@ApiResponse(responseCode = "200", description = "Owner found",
							content = @Content(schema = @Schema(implementation = OwnerResponseDto.class))),
					@ApiResponse(responseCode = "404", description = "Owner not found") })
	public ResponseEntity<OwnerResponseDto> getOwnerById(
			@Parameter(description = "Owner unique ID") @PathVariable Integer id) {
		return this.ownerUseCase.findById(id)
			.map(this::mapToDto)
			.map(ResponseEntity::ok)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found with id: " + id));
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Create a new owner",
			description = "Creates a new owner record with validated contact details",
			responses = { @ApiResponse(responseCode = "201", description = "Owner successfully created"),
					@ApiResponse(responseCode = "400", description = "Validation error on payload") })
	public ResponseEntity<OwnerResponseDto> createOwner(@Valid @RequestBody OwnerRequestDto request) {
		OwnerModel model = new OwnerModel(null, request.getFirstName(), request.getLastName(), request.getAddress(),
				request.getCity(), request.getTelephone());
		OwnerModel created = this.ownerUseCase.createOwner(model);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(created));
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update an existing owner", description = "Updates an existing owner profile",
			responses = { @ApiResponse(responseCode = "200", description = "Owner updated successfully"),
					@ApiResponse(responseCode = "400", description = "Validation error on payload"),
					@ApiResponse(responseCode = "404", description = "Owner not found") })
	public ResponseEntity<OwnerResponseDto> updateOwner(
			@Parameter(description = "Owner unique ID") @PathVariable Integer id,
			@Valid @RequestBody OwnerRequestDto request) {
		this.ownerUseCase.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found with id: " + id));

		OwnerModel model = new OwnerModel(id, request.getFirstName(), request.getLastName(), request.getAddress(),
				request.getCity(), request.getTelephone());
		OwnerModel updated = this.ownerUseCase.updateOwner(id, model);
		return ResponseEntity.ok(mapToDto(updated));
	}

	private OwnerResponseDto mapToDto(OwnerModel owner) {
		List<PetResponseDto> pets = (owner.getPets() != null) ? owner.getPets().stream().map(pet -> {
			PetTypeDto typeDto = (pet.getType() != null)
					? new PetTypeDto(pet.getType().getId(), pet.getType().getName()) : null;
			return new PetResponseDto(pet.getId(), pet.getName(), pet.getBirthDate(), typeDto, owner.getId());
		}).toList() : List.of();

		return new OwnerResponseDto(owner.getId(), owner.getFirstName(), owner.getLastName(), owner.getAddress(),
				owner.getCity(), owner.getTelephone(), pets);
	}

}
