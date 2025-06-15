package com.murali.batch.spring_batch_data_processor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fhvhv_trips")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // BIGSERIAL in Postgres
    private Long id;

    @Column(name = "hvfhs_license_num")
    private String hvfhsLicenseNum;

    @Column(name = "dispatching_base_num")
    private String dispatchingBaseNum;

    @Column(name = "originating_base_num")
    private String originatingBaseNum;

    @Column(name = "request_datetime")
    private LocalDateTime requestDatetime;

    @Column(name = "on_scene_datetime")
    private LocalDateTime onSceneDatetime;

    @Column(name = "pickup_datetime")
    private LocalDateTime pickupDatetime;

    @Column(name = "dropoff_datetime")
    private LocalDateTime dropoffDatetime;

    @Column(name = "pu_location_id")
    private Integer puLocationId;

    @Column(name = "do_location_id")
    private Integer doLocationId;

    @Column(name = "trip_miles")
    private BigDecimal tripMiles;

    @Column(name = "trip_time")
    private Integer tripTime;

    @Column(name = "base_passenger_fare")
    private BigDecimal basePassengerFare;

    @Column(name = "tolls")
    private BigDecimal tolls;

    @Column(name = "bcf")
    private BigDecimal bcf;

    @Column(name = "sales_tax")
    private BigDecimal salesTax;

    @Column(name = "congestion_surcharge")
    private BigDecimal congestionSurcharge;

    @Column(name = "airport_fee")
    private BigDecimal airportFee;

    @Column(name = "tips")
    private BigDecimal tips;

    @Column(name = "driver_pay")
    private BigDecimal driverPay;

    @Column(name = "shared_request_flag")
    private String sharedRequestFlag;

    @Column(name = "shared_match_flag")
    private String sharedMatchFlag;

    @Column(name = "access_a_ride_flag")
    private String accessARideFlag;

    @Column(name = "wav_request_flag")
    private String wavRequestFlag;

    @Column(name = "wav_match_flag")
    private String wavMatchFlag;

    @Column(name = "cbd_congestion_fee")
    private BigDecimal cbdCongestionFee;
}
