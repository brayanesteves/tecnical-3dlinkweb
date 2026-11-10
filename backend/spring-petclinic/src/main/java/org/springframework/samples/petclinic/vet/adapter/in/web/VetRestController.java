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
package org.springframework.samples.petclinic.vet.adapter.in.web;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.vet.adapter.in.web.dto.SpecialtyResponseDto;
import org.springframework.samples.petclinic.vet.adapter.in.web.dto.VetResponseDto;
import org.springframework.samples.petclinic.vet.port.in.GetVetsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vets")
@Tag(name = "Veterinarians", description = "Endpoints for retrieving veterinary personnel and their specialties")
public class VetRestController {

	private final GetVetsUseCase getVetsUseCase;

	public VetRestController(GetVetsUseCase getVetsUseCase) {
		this.getVetsUseCase = getVetsUseCase;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get all veterinarians",
			description = "Returns a complete list of veterinarians along with their certified medical specialties",
			responses = { @ApiResponse(responseCode = "200", description = "Successfully retrieved veterinarians list",
					content = @Content(mediaType = "application/json",
							array = @ArraySchema(schema = @Schema(implementation = VetResponseDto.class)))) })
	public List<VetResponseDto> getAllVets() {
		return this.getVetsUseCase.getAllVets().stream().map(vet -> {
			List<SpecialtyResponseDto> specialties = vet.getSpecialties()
				.stream()
				.map(s -> new SpecialtyResponseDto(s.getId(), s.getName()))
				.toList();
			return new VetResponseDto(vet.getId(), vet.getFirstName(), vet.getLastName(), specialties);
		}).toList();
	}

}
