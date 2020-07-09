package com.github.hm1rafael.clipping.entities;

import lombok.Builder;
import lombok.Getter;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

@Entity
@Getter
@Builder
public class Alert {
    @Id
    private Long id;
    @Reference
    private Clipping clipping;
}
