package com.github.hm1rafael.clipping.repositories;

import com.github.hm1rafael.clipping.entities.Clipping;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface ClippingRepository extends DatastoreRepository<Clipping, Long> {
}
