package com.github.hm1rafael.clipping.entities;

import lombok.Builder;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity
@Builder
public class Client {
    @Id
    private String client;
}
