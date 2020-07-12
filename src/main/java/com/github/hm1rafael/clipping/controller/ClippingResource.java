package com.github.hm1rafael.clipping.controller;

import com.github.hm1rafael.clipping.entities.Clipping;
import com.github.hm1rafael.clipping.entities.ClippingRequest;
import com.github.hm1rafael.clipping.entities.Pagination;
import com.github.hm1rafael.clipping.repositories.ClippingRepository;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clipping")
@Validated
public class ClippingResource {

    private final ClippingRepository clippingRepository;
    private final Logger logger = LoggerFactory.getLogger(ClippingResource.class);

    public ClippingResource(ClippingRepository clippingRepository) {
        this.clippingRepository = clippingRepository;
    }

    @GetMapping(value = "/", produces = "application/json")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clippings found")
    })
    public Page<Clipping> findAll(@Valid Pagination pagination) {
        Pageable pageRequest = PageRequest.of(pagination.getPage(), pagination.getSize());
        logger.info("Parameters {}", pageRequest);
        return clippingRepository.findAll(pageRequest);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Clipping not found"),
            @ApiResponse(responseCode = "200", description = "Clipping found")
    })
    public Clipping find(@PathVariable Long id) {
        return clippingRepository.findById(id)
                .orElseGet(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
    }

    @PatchMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Clipping not found"),
            @ApiResponse(responseCode = "204", description = "Clipping updated")
    })
    public ResponseEntity<Void> confirmClipping(@PathVariable Long id) {
        clippingRepository.findById(id)
                .ifPresentOrElse(clipping -> {
                    clipping.confirm();
                    clippingRepository.save(clipping);
                }, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Clippings deleted")
    })
    public ResponseEntity<Void> deleteAllClippings() {
        clippingRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Clipping not found"),
            @ApiResponse(responseCode = "204", description = "Clipping deleted")
    })
    public ResponseEntity<Void> deleteClipping(@PathVariable Long id) {
        clippingRepository.findById(id)
                .ifPresentOrElse(clippingRepository::delete, () -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                });
        return ResponseEntity.noContent().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clippings saved successfully"),
            @ApiResponse(responseCode = "204", description = "No clipping to save"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/")
    public ResponseEntity<Void> saveClipping(@Valid @RequestBody List<ClippingRequest> clippingRequests) {
        if (CollectionUtils.isEmpty(clippingRequests)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        logger.info("Parameters {}", clippingRequests);
        clippingRepository.performTransaction(repo -> {
            List<Clipping> clippingEntities = clippingRequests.stream()
                    .map(Clipping::new)
                    .collect(Collectors.toList());
            repo.saveAll(clippingEntities);
            return "Transaction completed";
        });
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
