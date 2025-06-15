package com.murali.batch.spring_batch_data_processor.config;

import com.murali.batch.spring_batch_data_processor.entity.Trip;
import com.murali.batch.spring_batch_data_processor.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Spring Batch 5‑style configuration (no deprecated factories).
 */
@Configuration
@RequiredArgsConstructor
public class SpringBatchConfig {

    private final DataSource dataSource;
    private final PlatformTransactionManager txnManager;

    private static final int CHUNK   = 10_000;

    @Autowired
    private TripRepository tripRepo;

    @Autowired
    private JobRepository jobRepo;


    /* ─────────────── Reader ─────────────── */

    @Bean
    public FlatFileItemReader<Trip> tripReader() {
        FlatFileItemReader<Trip> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource(
                "C:/Users/murali/Documents/Spring_Batch/parquet_csv/fhvhv_tripdata_2025-01.csv"));
        itemReader.setName("tripCsvReader");
        itemReader.setLinesToSkip(1);           // skip header row
        itemReader.setLineMapper(tripLineMapper());
        return itemReader;
    }

    private LineMapper<Trip> tripLineMapper() {
        DefaultLineMapper<Trip> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("hvfhsLicenseNum","dispatchingBaseNum","originatingBaseNum",
                "requestDatetime","onSceneDatetime","pickupDatetime","dropoffDatetime",
                "puLocationId","doLocationId","tripMiles","tripTime",
                "basePassengerFare","tolls","bcf","salesTax","congestionSurcharge",
                "airportFee","tips","driverPay",
                "sharedRequestFlag","sharedMatchFlag","accessARideFlag",
                "wavRequestFlag","wavMatchFlag","cbdCongestionFee");

        BeanWrapperFieldSetMapper<Trip> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Trip.class);
        fieldSetMapper.setConversionService(tripConversionService());

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public FormattingConversionService tripConversionService() {
        var cs = new DefaultFormattingConversionService(false);

        // yyyy-MM-dd HH:mm:ss  → LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        cs.addConverter(String.class, LocalDateTime.class,
                s -> (s == null || s.isBlank()) ? null : LocalDateTime.parse(s, formatter));

        return cs;
    }


    /* -------------- Processor --------------- */

    @Bean
    public TripProcessor tripProcessor()
    {
        return new TripProcessor();
    }

    /* ─────────────── Writer ─────────────── */

    @Bean
    public RepositoryItemWriter<Trip> tripWriter()
    {
        RepositoryItemWriter<Trip> repositoryItemWriter=new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(tripRepo);
        repositoryItemWriter.setMethodName("save");
        return repositoryItemWriter;
    }

    @Bean
    public Step loadTripStep(JobRepository jobRepo) {

        // configure executor with concurrency limit instead of throttleLimit()
//        SimpleAsyncTaskExecutor exec = new SimpleAsyncTaskExecutor("csv-loader-");
//        exec.setConcurrencyLimit(THREADS);

        return new StepBuilder("loadTripStep", jobRepo)
                .<Trip, Trip>chunk(CHUNK, txnManager)
                .reader(tripReader())                                     // your @Bean methods
                .processor(tripProcessor())
                .writer(tripWriter())
                .faultTolerant()
                .skipPolicy(new AlwaysSkipBadRecordsPolicy())
                .build();
    }

    @Bean
    public Job csvToPostgresJob(JobRepository jobRepo, Step loadTripStep) {

        return new JobBuilder("csvToPostgresJob", jobRepo)
                .incrementer(new RunIdIncrementer())
                .start(loadTripStep)
                .build();
    }

//
//
//    /* ─────────────── Writer ─────────────── */
//
//    @Bean
//    public JdbcBatchItemWriter<Trip> tripWriter() {
//        return new JdbcBatchItemWriterBuilder<Trip>()
//                .dataSource(dataSource)
//                .sql("""
//                      INSERT INTO fhvhv_trips(
//                        hvfhs_license_num, dispatching_base_num, originating_base_num,
//                        request_datetime, on_scene_datetime, pickup_datetime, dropoff_datetime,
//                        pulocationid, dolocationid, trip_miles, trip_time,
//                        base_passenger_fare, tolls, bcf, sales_tax, congestion_surcharge,
//                        airport_fee, tips, driver_pay, shared_request_flag, shared_match_flag,
//                        access_a_ride_flag, wav_request_flag, wav_match_flag, cbd_congestion_fee)
//                      VALUES (
//                        :hvfhsLicenseNum, :dispatchingBaseNum, :originatingBaseNum,
//                        :requestDatetime, :onSceneDatetime, :pickupDatetime, :dropoffDatetime,
//                        :puLocationId, :doLocationId, :tripMiles, :tripTime,
//                        :basePassengerFare, :tolls, :bcf, :salesTax, :congestionSurcharge,
//                        :airportFee, :tips, :driverPay, :sharedRequestFlag, :sharedMatchFlag,
//                        :accessARideFlag, :wavRequestFlag, :wavMatchFlag, :cbdCongestionFee)
//                      """)
//                .beanMapped()
//                .build();
//    }
//
//    /* ─────────────── Step ─────────────── */
//
//    @Bean
//    public Step loadTripStep(FlatFileItemReader<Trip> reader,
//                             JdbcBatchItemWriter<Trip> writer) {
//
//        // configure executor with concurrency limit instead of throttleLimit()
//        SimpleAsyncTaskExecutor exec = new SimpleAsyncTaskExecutor("csv-loader-");
//        exec.setConcurrencyLimit(THREADS);
//
//        return new StepBuilder("loadTripStep", jobRepository)
//                .<Trip, Trip>chunk(CHUNK, txnManager)
//                .reader(reader)
//                .writer(writer)
//                .faultTolerant()
//                .skipPolicy(new AlwaysSkipBadRecordsPolicy())
//                .taskExecutor(exec)   // -> parallel chunks
//                .build();
//    }
//
//    /* ─────────────── Job ─────────────── */
//
//    @Bean
//    public Job csvToPostgresJob(Step loadTripStep,
//                                JobExecutionListener listener) {
//
//        return new JobBuilder("csvToPostgresJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .listener(listener)
//                .start(loadTripStep)
//                .build();
//    }
}
