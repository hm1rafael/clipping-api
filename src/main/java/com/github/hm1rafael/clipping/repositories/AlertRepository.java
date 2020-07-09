package com.github.hm1rafael.clipping.repositories;

import com.github.hm1rafael.clipping.entities.Alert;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface AlertRepository extends DatastoreRepository<Alert, Long> {
}
