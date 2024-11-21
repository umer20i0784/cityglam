package com.cityglam.cityglam.controller;

import com.cityglam.cityglam.repository.*;
import org.springframework.web.bind.annotation.*;
import com.cityglam.cityglam.entity.*;
import java.util.*;

@RestController
@RequestMapping("/location")
public class LocationController {

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final CountyRepository countyRepository;
    private final DistrictRepository districtRepository;

    public LocationController(CountryRepository countryRepository,
                              RegionRepository regionRepository,
                              CountyRepository countyRepository,
                              DistrictRepository districtRepository) {
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.countyRepository = countyRepository;
        this.districtRepository = districtRepository;
    }

    // Fetch all active countries
    @GetMapping("/countries")
    public List<Country> getCountries() {
        return countryRepository.findByStatus("active");
    }

    // Fetch regions by countryId
    @PostMapping("/regions")
    public List<Region> getRegions(@RequestParam("countryId") Long countryId) {
        return regionRepository.findByCountryIdAndStatus(countryId, "active");
    }

    // Fetch counties by regionId
    @PostMapping("/counties")
    public List<County> getCounties(@RequestParam("regionId") Long regionId) {
        return countyRepository.findByRegionIdAndStatus(regionId, "active");
    }

    // Fetch districts by countyId
    @PostMapping("/districts")
    public List<District> getDistricts(@RequestParam("countyId") Long countyId) {
        return districtRepository.findByCountyIdAndStatus(countyId, "active");
    }




}

