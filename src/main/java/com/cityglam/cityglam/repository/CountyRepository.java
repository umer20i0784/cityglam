package com.cityglam.cityglam.repository;

import com.cityglam.cityglam.entity.County;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CountyRepository extends JpaRepository<County, Integer> {
    @Query(value = "SELECT * FROM orbit_settings_county_province", nativeQuery = true)
    List<County> findAllNative();

    List<County> findByRegionIdAndStatus(Long regionId, String status);


}

