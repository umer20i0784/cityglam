package com.cityglam.cityglam.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orbit_settings_district_community")
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "district_id")
    private Integer districtId;

    @Column(name = "district_name", nullable = false, length = 255)
    private String districtName;

    @Column(name = "status", columnDefinition = "ENUM('active', 'inactive')")
    private String status;

    @Column(name = "county_id")
    private Integer countyId;

    // Getters and Setters

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer county) {
        this.countyId = county;
    }
}


