package com.murali.batch.spring_batch_data_processor.config;

import com.murali.batch.spring_batch_data_processor.entity.Trip;
import org.springframework.batch.item.ItemProcessor;

public class TripProcessor implements ItemProcessor<Trip, Trip> {
    @Override
    public Trip process(Trip item) throws Exception {
        return item;
    }
}
