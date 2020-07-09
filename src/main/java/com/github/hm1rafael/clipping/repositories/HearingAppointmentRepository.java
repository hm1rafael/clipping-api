package com.github.hm1rafael.clipping.repositories;

import com.github.hm1rafael.clipping.entities.HearingAppointment;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

public interface HearingAppointmentRepository extends DatastoreRepository<HearingAppointment, Long> {
}
