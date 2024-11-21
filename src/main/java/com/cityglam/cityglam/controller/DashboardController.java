package com.cityglam.cityglam.controller;

import com.cityglam.cityglam.entity.Country;
import com.cityglam.cityglam.entity.Region;
import com.cityglam.cityglam.entity.County;
import com.cityglam.cityglam.entity.District;
import com.cityglam.cityglam.repository.CountryRepository;
import com.cityglam.cityglam.repository.RegionRepository;
import com.cityglam.cityglam.repository.CountyRepository;
import com.cityglam.cityglam.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {

        return "dashboard";
    }




}

