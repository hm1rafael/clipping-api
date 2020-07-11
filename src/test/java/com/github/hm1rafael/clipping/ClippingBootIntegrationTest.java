package com.github.hm1rafael.clipping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hm1rafael.clipping.entities.Clipping;
import com.github.hm1rafael.clipping.repositories.ClippingRepository;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class ClippingBootIntegrationTest {

    public static final int TIMEOUT = 15;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("classpath:data/clippings.json")
    private Resource resourceFile;
    @Autowired
    private ClippingRepository clippingRepository;

    @BeforeEach
    void setUp() {
        awaitForRepositoryOperation(this::persistClippings, 4);
    }

    @AfterEach
    void tearDown() {
        awaitForRepositoryOperation(clippingRepository::deleteAll, 0);
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
        Page page = loadClippings();

        List<Clipping> clippings = page.content;
        String contentJson = objectMapper.writeValueAsString(clippings);
        mockMvc.perform(post("/api/clipping/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentJson)
        ).andExpect(status().isCreated());

        assertThat(clippings)
                .hasSize(4)
                .usingElementComparatorIgnoringFields("id", "classifiedDate", "classifiedTime")
                .containsExactlyInAnyOrderElementsOf(clippingRepository.findAll());
    }

    @Test
    void savedClippingShouldBeFound() throws Exception {
        Page page = loadClippings();
        assertThat(page.totalElements)
                .isEqualTo(4);

        Assertions.assertThat(page.content)
                .hasSize(4)
                .usingElementComparatorIgnoringFields("id", "classifiedDate", "classifiedTime")
                .containsExactlyInAnyOrderElementsOf(clippingRepository.findAll());
    }

    @Test
    void confirmClipping() throws Exception {
        Page page = loadClippings();
        Clipping clipping = page.content.get(0);
        Assertions.assertThat(clipping.isConfirmation()).isFalse();

        Long id = clipping.getId();
        mockMvc.perform(patch("/api/clipping/{id}", id))
                .andExpect(status().isNoContent());

        Awaitility.await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> clippingRepository.findById(id).map(Clipping::isConfirmation).orElse(false));

        Boolean confirmed = clippingRepository.findById(id)
                .map(Clipping::isConfirmation)
                .orElse(false);

        assertThat(confirmed).isTrue();

        clippingRepository.findById(page.content.get(1).getId())
                .map(Clipping::isConfirmation)
                .ifPresentOrElse(confirmation -> Assertions.assertThat(confirmation).isFalse(), IllegalStateException::new);
    }

    @Test
    void clippingNotFoundIfTryToConfirmed() throws Exception {
        mockMvc.perform(patch("/api/clipping/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSingleClipping() throws Exception {
        Clipping clipping = loadSingleClipping();
        Long id = clipping.getId();
        mockMvc.perform(get("/api/clipping/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/clipping/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/clipping/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAllClippings() throws Exception {
        mockMvc.perform(get("/api/clipping/"))
                .andExpect(status().isOk());

        Assertions.assertThat(loadClippings().totalElements)
                .isEqualTo(4);

        mockMvc.perform(delete("/api/clipping/"))
                .andExpect(status().isNoContent());

        Assertions.assertThat(loadClippings().totalElements)
                .isEqualTo(0);
    }

    @Test
    void deleteNonExistingClipping() throws Exception {
        mockMvc.perform(delete("/api/clipping/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void findClippingById() throws Exception {
        Clipping expectedClipping = loadSingleClipping();
        String content = mockMvc.perform(get("/api/clipping/{id}", expectedClipping.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Clipping actualClipping = objectMapper.readValue(content, Clipping.class);

        Assertions.assertThat(actualClipping)
                .isEqualToIgnoringGivenFields(expectedClipping, "id", "classifiedDate");
    }

    @Test
    void findClippingByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/clipping/{id}", 1))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Clipping loadSingleClipping() throws Exception {
        return loadClippings().content.stream()
                .findFirst()
                .orElse(null);
    }

    private Page loadClippings() throws Exception {
        String contentAsString = mockMvc.perform(get("/api/clipping/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
    }

    private void persistClippings() {
        try {
            String clippingsJsonString = Files.readString(resourceFile.getFile().toPath());
            List<Clipping> clippings = parseClipping(clippingsJsonString);
            clippingRepository.saveAll(clippings);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<Clipping> parseClipping(String expectedClippings) throws com.fasterxml.jackson.core.JsonProcessingException {
        return objectMapper.readValue(expectedClippings, new TypeReference<>() {
        });
    }

    @Data
    private static class Page {

        private List<Clipping> content;
        private Integer totalElements;

    }

}
