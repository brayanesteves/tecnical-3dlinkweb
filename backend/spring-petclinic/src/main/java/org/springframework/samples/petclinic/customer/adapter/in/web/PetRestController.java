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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.customer.adapter.in.web.dto.PetRequestDto;
import org.springframework.samples.petclinic.customer.adapter.in.web.dto.PetResponseDto;
import org.springframework.samples.petclinic.customer.adapter.in.web.dto.PetTypeDto;
import org.springframework.samples.petclinic.customer.domain.PetModel;
import org.springframework.samples.petclinic.customer.domain.PetTypeModel;
import org.springframework.samples.petclinic.customer.port.in.OwnerUseCase;
import org.springframework.samples.petclinic.customer.port.in.PetUseCase;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Pets", description = "Endpoints for managing pets and pet types")
public class PetRestController {

	private final PetUseCase petUseCase;

	private final OwnerUseCase ownerUseCase;

	public PetRestController(PetUseCase petUseCase, OwnerUseCase ownerUseCase) {
		this.petUseCase = petUseCase;
		this.ownerUseCase = ownerUseCase;
	}

	@GetMapping(value = "/pet-types", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all available pet types",
			description = "Returns catalog list of pet types (cat, dog, bird, etc.)",
			responses = { @ApiResponse(responseCode = "200", description = "List of pet types retrieved successfully",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = PetTypeDto.class)))) })
	public List<PetTypeDto> getPetTypes() {
		return this.petUseCase.getAllPetTypes().stream().map(t -> new PetTypeDto(t.getId(), t.getName())).toList();
	}

	@GetMapping(value = "/owners/{ownerId}/pets", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get pets by owner ID",
			description = "Returns list of all pets registered to a specific owner",
			responses = { @ApiResponse(responseCode = "200", description = "Pets list retrieved"),
					@ApiResponse(responseCode = "404", description = "Owner not found") })
	public List<PetResponseDto> getPetsByOwner(@PathVariable Integer ownerId) {
		this.ownerUseCase.findById(ownerId)
			.orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found with id: " + ownerId));

		return this.petUseCase.findByOwnerId(ownerId).stream().map(this::mapToDto).toList();
	}

	@PostMapping(value = "/owners/{ownerId}/pets", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Add a new pet to an owner", description = "Registers a new pet under an existing owner",
			responses = { @ApiResponse(responseCode = "201", description = "Pet created successfully"),
					@ApiResponse(responseCode = "400", description = "Validation error"),
					@ApiResponse(responseCode = "404", description = "Owner not found") })
	public ResponseEntity<PetResponseDto> addPetToOwner(@PathVariable Integer ownerId,
			@Valid @RequestBody PetRequestDto request) {
		this.ownerUseCase.findById(ownerId)
			.orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found with id: " + ownerId));

		PetTypeModel type = this.petUseCase.getAllPetTypes()
			.stream()
			.filter(t -> t.getId().equals(request.getTypeId()))
			.findFirst()
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid pet type ID: " + request.getTypeId()));

		PetModel petModel = new PetModel(null, request.getName(), request.getBirthDate(), type, ownerId);
		PetModel created = this.petUseCase.addPetToOwner(ownerId, petModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(created));
	}

	@PutMapping(value = "/owners/{ownerId}/pets/{petId}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Update an existing pet", description = "Updates details of a registered pet",
			responses = { @ApiResponse(responseCode = "200", description = "Pet updated successfully"),
					@ApiResponse(responseCode = "400", description = "Validation error"),
					@ApiResponse(responseCode = "404", description = "Owner or pet not found") })
	public ResponseEntity<PetResponseDto> updatePet(@PathVariable Integer ownerId, @PathVariable Integer petId,
			@Valid @RequestBody PetRequestDto request) {
		this.ownerUseCase.findById(ownerId)
			.orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found with id: " + ownerId));

		this.petUseCase.findById(petId)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found with id: " + petId));

		PetTypeModel type = this.petUseCase.getAllPetTypes()
			.stream()
			.filter(t -> t.getId().equals(request.getTypeId()))
			.findFirst()
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid pet type ID: " + request.getTypeId()));

		PetModel petModel = new PetModel(petId, request.getName(), request.getBirthDate(), type, ownerId);
		PetModel updated = this.petUseCase.updatePet(ownerId, petId, petModel);
		return ResponseEntity.ok(mapToDto(updated));
	}

	private PetResponseDto mapToDto(PetModel pet) {
		PetTypeDto typeDto = (pet.getType() != null) ? new PetTypeDto(pet.getType().getId(), pet.getType().getName())
				: null;
		return new PetResponseDto(pet.getId(), pet.getName(), pet.getBirthDate(), typeDto, pet.getOwnerId());
	}

}
