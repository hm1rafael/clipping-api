package com.github.hm1rafael.clipping.controller;

import com.github.hm1rafael.clipping.entities.Clipping;
import com.github.hm1rafael.clipping.repositories.ClippingRepository;
import com.google.api.client.util.Lists;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clipping")
public class ClippingResource {

    private final ClippingRepository clippingRepository;
    private final Logger logger = LoggerFactory.getLogger(ClippingResource.class);

    public ClippingResource(ClippingRepository clippingRepository) {
        this.clippingRepository = clippingRepository;
    }

    @GetMapping("/")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Clippings found")
    })
    public Page<Clipping> findAll(Pageable pageable) {
        logger.info("Parameters {}", pageable);
        return clippingRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
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

    @DeleteMapping({"/{id}", "/"})
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Clipping not found"),
            @ApiResponse(responseCode = "204", description = "Clipping deleted")
    })
    public ResponseEntity<Void> deleteClipping(@PathVariable(required = false) Optional<Long> id) {
        id.map(this::find)
                .ifPresentOrElse(clippingRepository::delete, clippingRepository::deleteAll);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Clippings saved successfully")
    })
    @PostMapping("/")
    public ResponseEntity<Void> saveClipping(@RequestBody List<Clipping> clippings) {
        logger.info("Parameters {}", clippings);
        this.clippingRepository.saveAll(clippings);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
