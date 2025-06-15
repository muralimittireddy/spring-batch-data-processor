package com.murali.batch.spring_batch_data_processor.config;

import org.springframework.batch.core.step.skip.SkipPolicy;

public class AlwaysSkipBadRecordsPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable t, long skipCount) {
        // Log only 10 bad records to keep output clean
        if (skipCount < 10) {
            System.err.println("Skipping bad row #" + skipCount + ": " + t.getMessage());
        }
        return true;
    }
}
