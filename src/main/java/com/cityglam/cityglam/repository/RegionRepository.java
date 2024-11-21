package com.cityglam.cityglam.repository;

import com.cityglam.cityglam.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    List<Region> findByCountryIdAndStatus(Long countryId, String status);
}

