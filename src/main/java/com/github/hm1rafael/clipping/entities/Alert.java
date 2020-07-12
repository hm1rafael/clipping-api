package com.github.hm1rafael.clipping.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.datastore.Key;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Alert {
    @Id
    @JsonIgnore
    private Key alertId;

    @JsonProperty
    private Long getAlertId() {
        return Optional.ofNullable(alertId).map(Key::getId).orElse(0L);
    }

}
