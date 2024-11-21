package com.cityglam.cityglam.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orbit_settings_county_province")
public class County {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "county_id")
    private Integer countyId;

    @Column(name = "county_name", nullable = false, length = 255)
    private String countyName;

    @Column(name = "status", columnDefinition = "ENUM('active', 'inactive')")
    private String status;

    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "region_id")
    private Integer regionId;

    // Getters and Setters


    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer country) {
        this.countryId = country;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer region) {
        this.regionId = region;
    }
}


