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
package org.springframework.samples.petclinic.visit.adapter.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.visit.adapter.in.web.dto.VisitRequestDto;
import org.springframework.samples.petclinic.visit.adapter.in.web.dto.VisitResponseDto;
import org.springframework.samples.petclinic.visit.domain.VisitModel;
import org.springframework.samples.petclinic.visit.port.in.VisitUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/owners/{ownerId}/pets/{petId}/visits")
@Tag(name = "Visits", description = "Visits scheduling and consultation REST API")
public class VisitRestController {

	private final VisitUseCase visitUseCase;

	public VisitRestController(VisitUseCase visitUseCase) {
		this.visitUseCase = visitUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Book a new visit", description = "Schedules a new clinic visit for a specific pet")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Visit successfully booked"),
			@ApiResponse(responseCode = "400", description = "Invalid request payload or past visit date"),
			@ApiResponse(responseCode = "404", description = "Owner or pet not found") })
	public VisitResponseDto bookVisit(@PathVariable int ownerId, @PathVariable int petId,
			@Valid @RequestBody VisitRequestDto request) {
		VisitModel model = new VisitModel(null, request.getDate(), request.getDescription(), petId);
		VisitModel created = this.visitUseCase.bookVisit(ownerId, petId, model);
		return toDto(created);
	}

	@GetMapping
	@Operation(summary = "Get all visits for a pet", description = "Retrieves the visit history for a specific pet")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "List of visits successfully retrieved"),
			@ApiResponse(responseCode = "404", description = "Owner or pet not found") })
	public List<VisitResponseDto> getVisits(@PathVariable int ownerId, @PathVariable int petId) {
		return this.visitUseCase.getVisitsByPet(ownerId, petId).stream().map(this::toDto).toList();
	}

	private VisitResponseDto toDto(VisitModel model) {
		return new VisitResponseDto(model.getId(), model.getDate(), model.getDescription(), model.getPetId());
	}

}
