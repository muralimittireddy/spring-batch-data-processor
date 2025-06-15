package com.murali.batch.spring_batch_data_processor.repository;

import com.murali.batch.spring_batch_data_processor.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface TripRepository extends JpaRepository<Trip, Serializable>{
}
