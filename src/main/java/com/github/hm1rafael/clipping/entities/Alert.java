package com.github.hm1rafael.clipping.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Alert {
    @Id
    private Long alertId;
}
