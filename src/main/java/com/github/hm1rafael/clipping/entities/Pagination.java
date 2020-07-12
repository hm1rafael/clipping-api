package com.github.hm1rafael.clipping.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@JsonDeserialize(builder = Pagination.PaginationBuilder.class)
@AllArgsConstructor
public class Pagination {

    private static final int DEFAULT_PAGE_SIZE = 20;
    @Builder.Default
    @NotNull
    private final Integer size = DEFAULT_PAGE_SIZE;
    @NotNull
    private final Integer page;

    @JsonPOJOBuilder(withPrefix = StringUtils.EMPTY)
    public static class PaginationBuilder {
    }

}
