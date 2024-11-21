package com.cityglam.cityglam.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "orbit_settings_regions_states")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "region_code", length = 10)
    private String regionCode;

    @Column(name = "region_name", nullable = false, length = 255)
    private String regionName;

    @Column(name = "status", columnDefinition = "ENUM('active', 'inactive')")
    private String status;


    @Column(name = "country_id")
    private Integer countryId;

    // Getters and Setters


    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
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
}


