package com.github.hm1rafael.clipping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hm1rafael.clipping.entities.Alert;
import com.github.hm1rafael.clipping.entities.Clipping;
import com.github.hm1rafael.clipping.entities.ClippingRequest;
import com.github.hm1rafael.clipping.entities.HearingAppointment;
import com.github.hm1rafael.clipping.repositories.AlertRepository;
import com.github.hm1rafael.clipping.repositories.ClippingRepository;
import com.github.hm1rafael.clipping.repositories.HearingRepository;
import lombok.Data;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClippingRequestBootIntegrationTest {

    private static final int TIMEOUT = 15;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("classpath:data/clippings.json")
    private Resource resourceFile;
    @Value("classpath:data/invalid_clipping.json")
    private Resource resourceFileWithInvalidCreateJson;
    @Autowired
    private ClippingRepository clippingRepository;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private HearingRepository hearingRepository;

    @AfterEach
    void tearDown() {
        awaitForRepositoryOperation(clippingRepository::deleteAll, 0);
        alertRepository.deleteAll();
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS)
                .until(() -> StreamSupport.stream(alertRepository.findAll().spliterator(), false).count() == 0);
        hearingRepository.deleteAll();
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS)
                .until(() -> StreamSupport.stream(hearingRepository.findAll().spliterator(), false).count() == 0);
    }

    private void awaitForRepositoryOperation(Runnable runnable, int i) {
        runnable.run();
        Awaitility.await()
                .atMost(TIMEOUT, TimeUnit.SECONDS)
                .until(() -> matchesSize(i));
    }

    boolean matchesSize(int size) {
        return StreamSupport.stream(clippingRepository.findAll().spliterator(), Boolean.FALSE).count() == size;
    }

    @Test
    void createClippings() throws Exception {
        List<ClippingRequest> clippingRequests = readStubClippingsRequests();
        mockMvc.perform(post("/api/clipping/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clippingRequests))
        ).andExpect(status().isCreated());

        awaitForRepositoryOperation(() -> {
        }, 4);

        List<Clipping> clippings = readStubClippings();
        assertEqualsToDataStoreClippings(clippings);
    }

    @Test
    void savedClippingShouldBeFound() throws Exception {
        awaitForRepositoryOperation(this::persistClippings, 4);

        String content = mockMvc.perform(get("/api/clipping/").param("page", "0").param("size", "4"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Page<Clipping> page = objectMapper.readValue(content, new TypeReference<>() {
        });

        assertThat(page.totalElements)
                .isEqualTo(4);

        assertEqualsToDataStoreClippings(page.content);
    }

    @Test
    void confirmClipping() throws Exception {
        awaitForRepositoryOperation(this::persistClippings, 4);
        Clipping clipping = loadSingleClipping();

        assertThat(clipping.isConfirmed()).isFalse();

        mockMvc.perform(patch("/api/clipping/{id}", clipping.getId()))
                .andExpect(status().isNoContent());

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> clippingRepository.findById(clipping.getId()).map(Clipping::isConfirmed).orElse(false));

        Boolean confirmed = clippingRepository.findById(clipping.getId())
                .map(Clipping::isConfirmed)
                .orElse(false);

        assertThat(confirmed).isTrue();
    }

    @Test
    void clippingNotFoundIfTryToConfirmed() throws Exception {
        mockMvc.perform(patch("/api/clipping/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSingleClipping() throws Exception {
        awaitForRepositoryOperation(this::persistClippings, 4);
        Clipping clipping = loadSingleClipping();
        Long id = clipping.getId();
        mockMvc.perform(get("/api/clipping/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/clipping/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/clipping/{id}", id))
                .andExpect(status().isNotFound());
        Awaitility.waitAtMost(TIMEOUT, TimeUnit.SECONDS)
                .until(() -> StreamSupport.stream(clippingRepository.findAll().spliterator(), Boolean.FALSE).count() == 3);
    }

    @Test
    void deleteAllClippings() throws Exception {
        awaitForRepositoryOperation(this::persistClippings, 4);
        mockMvc.perform(delete("/api/clipping/"))
                .andExpect(status().isNoContent());
        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> loadClippings().size() == 0);
    }

    @Test
    void deleteNonExistingClipping() throws Exception {
        mockMvc.perform(delete("/api/clipping/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void findClippingById() throws Exception {
        awaitForRepositoryOperation(this::persistClippings, 4);
        Clipping clipping = loadSingleClipping();
        String content = mockMvc.perform(get("/api/clipping/{id}", clipping.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Clipping actualClipping = objectMapper.readValue(content, Clipping.class);

        assertThat(actualClipping)
                .isEqualToIgnoringGivenFields(clipping, "id", "alert", "hearingAppointment");
    }

    @Test
    void findClippingByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/clipping/{id}", 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void noContentToBeSaved() throws Exception {
        mockMvc.perform(post("/api/clipping/")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void emptyArrayToBeSaved() throws Exception {
        mockMvc.perform(post("/api/clipping/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]")
        ).andExpect(status().isNoContent());
    }

    @Test
    void invalidDataOnCreateRequest() throws Exception {
        String json = Files.readString(resourceFileWithInvalidCreateJson.getFile().toPath());
        mockMvc.perform(post("/api/clipping/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void invalidClippingPaginationRequest() throws Exception {
        mockMvc.perform(get("/api/clipping/"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/clipping/").param("size", "2"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/clipping/").param("page", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loadAlerts() throws Exception {
        awaitForRepositoryOperation(this::persistClippings, 4);
        String content = mockMvc.perform(get("/api/user/alerts"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Page<Alert> alert = objectMapper.readValue(content, new TypeReference<Page<Alert>>() {
        });

        assertThat(alert.content)
                .hasSize(3);
    }

    @Test
    void loadHearings() throws Exception {
        awaitForRepositoryOperation(this::persistClippings, 4);
        String content = mockMvc.perform(get("/api/user/hearings"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Page<HearingAppointment> alert = objectMapper.readValue(content, new TypeReference<>() {
        });

        assertThat(alert.content)
                .hasSize(2);
    }

    private void assertEqualsToDataStoreClippings(List<Clipping> clippings) {
        assertThat(clippings)
                .hasSize(4)
                .usingElementComparatorIgnoringFields("id", "alert", "hearingAppointment")
                .containsExactlyInAnyOrderElementsOf(clippingRepository.findAll());
    }

    private Clipping loadSingleClipping() {
        return loadClippings().stream()
                .findFirst()
                .orElse(null);
    }

    private List<Clipping> loadClippings() {
        return StreamSupport.stream(clippingRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    private void persistClippings() {
        try {
            List<Clipping> clippings = readStubClippings();
            clippingRepository.saveAll(clippings);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Clipping> readStubClippings() throws IOException {
        return readStubClippingsRequests().stream()
                .map(Clipping::new)
                .collect(Collectors.toList());
    }

    private List<ClippingRequest> readStubClippingsRequests() throws IOException {
        String clippingsJsonString = Files.readString(resourceFile.getFile().toPath());
        return objectMapper.readValue(clippingsJsonString, new TypeReference<>() {
        });
    }

    @Data
    private static class Page<T> {

        private List<T> content;
        private Integer totalElements;

    }

}
