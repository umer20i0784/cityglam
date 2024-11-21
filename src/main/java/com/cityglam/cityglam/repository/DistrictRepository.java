package com.cityglam.cityglam.repository;

import com.cityglam.cityglam.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {
    @Query("SELECT d FROM District d")
    List<District> findAllDistrictsCustom();

    List<District> findByCountyIdAndStatus(Long countyId, String status);
}

