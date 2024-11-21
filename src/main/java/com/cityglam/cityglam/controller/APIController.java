package com.cityglam.cityglam.controller;

import com.cityglam.cityglam.entity.*;
import com.cityglam.cityglam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cityglam.cityglam.entity.Country;
import com.cityglam.cityglam.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.*;
@RestController
public class APIController {

    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/api/countries")
    public List<Country> getAllCountries() {
        return (List<Country>) countryRepository.findAll();
    }

    @PostMapping("/api/countries")
    public ResponseEntity<Country> addCountry(@RequestBody Country newCountry) {
        newCountry.setCreatedDate(LocalDateTime.now());
        Country savedCountry = countryRepository.save(newCountry);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCountry);
    }


    // Edit country (Update)
    @PutMapping("/api/countries/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable Integer id, @RequestBody Country updatedCountry) {
        Optional<Country> existingCountry = countryRepository.findById(id);
        if (existingCountry.isPresent()) {
            Country country = existingCountry.get();
            // Update fields
            country.setCountryName(updatedCountry.getCountryName());
            country.setCountryCode(updatedCountry.getCountryCode());
            country.setCurrencyIsoCode(updatedCountry.getCurrencyIsoCode());
            country.setIsoCode(updatedCountry.getIsoCode());
            country.setStatus(updatedCountry.getStatus());
            country.setContinentId(updatedCountry.getContinentId());
            country.setCountryImage(updatedCountry.getCountryImage());
            country.setCurrencySymbol(updatedCountry.getCurrencySymbol());
            country.setDefaultCountry(updatedCountry.getDefaultCountry());
            // Save updated entity
            countryRepository.save(country);
            return ResponseEntity.ok("Country updated successfully.");
        } else {
            return ResponseEntity.status(404).body("Country not found.");
        }
    }

    // Delete country
    @DeleteMapping("/api/countries/{id}")
    public ResponseEntity<?> deleteCountry(@PathVariable Integer id) {
        Optional<Country> existingCountry = countryRepository.findById(id);
        if (existingCountry.isPresent()) {
            countryRepository.deleteById(id);
            return ResponseEntity.ok("Country deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Country not found.");
        }
    }

    @Autowired
    private RegionRepository regionRepository;

    @GetMapping("/api/regions")
    public ResponseEntity<List<Region>> getAllRegions() {
        List<Region> regions = regionRepository.findAll();
        return ResponseEntity.ok(regions);
    }

    @PostMapping("/api/regions")
    public ResponseEntity<Region> addRegion(@RequestBody Region newRegion) {
        Region savedRegion = regionRepository.save(newRegion);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRegion);
    }

    @PutMapping("/api/regions/{id}")
    public ResponseEntity<Region> updateRegion(@PathVariable Integer id, @RequestBody Region updatedRegion) {
        return regionRepository.findById(id)
                .map(region -> {
                    // Update basic region fields
                    region.setRegionCode(updatedRegion.getRegionCode());
                    region.setRegionName(updatedRegion.getRegionName());
                    region.setStatus(updatedRegion.getStatus());

                    // Update the country ID directly
                    region.setCountryId(updatedRegion.getCountryId());

                    // Save and return the updated region
                    regionRepository.save(region);
                    return ResponseEntity.ok(region);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/regions/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Integer id) {
        return regionRepository.findById(id)
                .map(region -> {
                    regionRepository.delete(region);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Autowired
    private CountyRepository countyRepository;

    @GetMapping("/api/counties")
    public ResponseEntity<List<County>> getAllCounties() {
        List<County> counties = countyRepository.findAllNative();
        return ResponseEntity.ok(counties);
    }

    @PostMapping("/api/counties")
    public ResponseEntity<County> addCounty(@RequestBody County newCounty) {
        County savedCounty = countyRepository.save(newCounty);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCounty);
    }


    @PutMapping("/api/counties/{id}")
    public ResponseEntity<County> updateCounty(@PathVariable Integer id, @RequestBody County updatedCounty) {
        return countyRepository.findById(id)
                .map(county -> {
                    // Update fields
                    county.setCountyName(updatedCounty.getCountyName());
                    county.setStatus(updatedCounty.getStatus());
                    county.setRegionId(updatedCounty.getRegionId());
                    county.setCountryId(updatedCounty.getCountryId());

                    // Save the updated county
                    County savedCounty = countyRepository.save(county);

                    // Return the updated county
                    return ResponseEntity.ok(savedCounty);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/api/counties/{id}")
    public ResponseEntity<Void> deleteCounty(@PathVariable Integer id) {
        if (countyRepository.existsById(id)) {
            countyRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Autowired
    private DistrictRepository districtRepository;

    @GetMapping("/api/districts")
    public ResponseEntity<List<District>> getAllDistricts() {
        List<District> districts = districtRepository.findAllDistrictsCustom();
        return ResponseEntity.ok(districts);
    }

    @PostMapping("/api/districts")
    public ResponseEntity<District> addDistrict(@RequestBody District newDistrict) {
        District savedDistrict = districtRepository.save(newDistrict);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDistrict);
    }


    @PutMapping("/api/districts/{id}")
    public ResponseEntity<District> updateDistrict(@PathVariable Integer id, @RequestBody District updatedDistrict) {
        Optional<District> optionalDistrict = districtRepository.findById(id);
        if (optionalDistrict.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        District district = optionalDistrict.get();
        // Update district fields
        district.setDistrictName(updatedDistrict.getDistrictName());
        district.setStatus(updatedDistrict.getStatus());
        district.setCountyId(updatedDistrict.getCountyId()); // Update countyId

        // Save and return the updated district
        return ResponseEntity.ok(districtRepository.save(district));
    }


    @DeleteMapping("/api/districts/{id}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Integer id) {
        if (!districtRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        districtRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam("role") Role role) {
        List<User> users = userRepository.findByRole(role);
        return ResponseEntity.ok(users);
    }

}

