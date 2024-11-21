package com.cityglam.cityglam.controller;

import com.cityglam.cityglam.entity.Role;
import com.cityglam.cityglam.entity.*;
import com.cityglam.cityglam.repository.CountryRepository;
import com.cityglam.cityglam.repository.UserRepository;
import com.cityglam.cityglam.service.OTPService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

import com.cityglam.cityglam.entity.Permission;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Controller
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private final OTPService otpService;

    private final CountryRepository countryRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private final Map<String, User> tempUserStorage = new ConcurrentHashMap<>();

    public AuthController(OTPService otpService, CountryRepository countryRepository) {
        this.otpService = otpService;
        this.countryRepository = countryRepository;
    }

    @GetMapping("/signin")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        return "auth-login"; // Login page template
    }

    @GetMapping("/auth-two-steps")
    public String showAuthTwoSteps(@RequestParam(value = "error", required = false) String error, Model model) {
        return "auth-two-steps";
    }


//    @GetMapping("/register-multi-step")
//    public String showAuthMultiSteps(
//            @RequestParam(value = "email") String email,
//            @RequestParam(value = "password") String password,
//            @RequestParam(value = "error", required = false) String error,
//            Model model) {
//        model.addAttribute("email", email);
//        model.addAttribute("password", password);
//        return "auth-register-multisteps";
//    }

    @GetMapping("/register-multi-step")
    public String showAuthMultiSteps(
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        if (!model.containsAttribute("email") || !model.containsAttribute("password")) {
            return "redirect:/login"; // Redirect to login if email or password is missing
        }
        // Fetch active countries for the first dropdown
        List<Country> countries = countryRepository.findByStatus("active");
        model.addAttribute("countries", countries);
        return "auth-register-multisteps";
    }

    @PostMapping("/signin")
    public String performLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model
            ) {

        LOGGER.debug("Attempting login for username: {}", username);

        // Fetch user from the database
        Optional<User> optionalUser = userRepository.findByEmail(username);
        if (optionalUser.isEmpty()) {
            LOGGER.warn("Login failed: User with email '{}' not found", username);
            return "redirect:/register";
        }

        User user = optionalUser.get();
        LOGGER.debug("User found: {}", user.getEmail());

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            LOGGER.warn("Login failed: Invalid password for user '{}'", username);
            return "redirect:/register";
        }

        LOGGER.debug("Password verification successful for user: {}", username);

        // Map roles and permissions to Spring Security authorities
        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            Set<SimpleGrantedAuthority> authorities = Arrays.stream(user.getPermissions().split(","))
                    .map(permission -> Permission.valueOf(permission))
                    .map(permission -> new SimpleGrantedAuthority(permission.name()))
                    .collect(Collectors.toSet());

            LOGGER.debug("User permissions mapped: {}", authorities);
            session.setAttribute("permissions", authorities);
        } else {
            LOGGER.warn("User '{}' has no permissions assigned", username);
        }

        // Store user details in session
        session.setAttribute("user", user);
        session.setAttribute("role", user.getRole());

        LOGGER.debug("Login successful for user: {}, redirecting to dashboard", username);

        // Redirect to the dashboard
        return "redirect:/dashboard";
    }


    @GetMapping("/register")
    public String showRegisterPage(@RequestParam(value = "error", required = false) String error, Model model) {

        return "auth-register";
    }

    @PostMapping("/register")
    public String quickSignUp(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String role,
            Model model) {

        String userRole = role != null ? role.toUpperCase() : "MANAGER";

        // Validation for CA, Manager, and Member roles
        if (validateEmailRoleConflict(email, userRole, model)) {
            return "auth-register"; // Return to the register page with errors
        }

        // Generate OTP and send it
        String otp = otpService.generateOTP(email);
        otpService.storeTempUser(email, password, userRole);
        otpService.sendOTPEmail(email, otp);

        model.addAttribute("email", email);
        return "auth-two-steps"; // Redirect to OTP verification page
    }
    private boolean validateEmailRoleConflict(String email, String userRole, Model model) {
        if ((userRole.equals("CA") || userRole.equals("MANAGER")) &&
                userRepository.existsByEmailForCAOrManager(email)) {
            model.addAttribute("error", "Email is already registered for CA or Manager.");
            return true;
        }
        if (userRole.equals("MEMBER") && userRepository.existsByEmailForNonMembers(email)) {
            model.addAttribute("error", "Email is already registered for non-member roles.");
            return true;
        }
        Role roleEnum = Role.valueOf(userRole);
        if (userRepository.existsByEmailAndRole(email, roleEnum)) {
            model.addAttribute("error", "Email is already registered for this role.");
            return true;
        }
        return false;
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam String email, @RequestParam String otp, RedirectAttributes redirectAttributes, Model model) {
        LOGGER.debug("Starting OTP verification for email: {}", email);

        if (otpService.verifyOTP(email, otp)) {
            LOGGER.debug("OTP verified for email: {}", email);

            Optional<User> tempUser = otpService.retrieveTempUser(email);
            if (tempUser.isPresent()) {
                User user = tempUser.get();
                LOGGER.debug("Temporary user found for email: {}", email);

                // Pass email and password to the multi-step signup page
                redirectAttributes.addFlashAttribute("email", email);
                redirectAttributes.addFlashAttribute("password", user.getPassword());
                return "redirect:/register-multi-step"; // Redirect to multi-step signup
            } else {
                LOGGER.error("Temporary user data not found for email: {}", email);
                model.addAttribute("error", "Temporary user data not found.");
                return "auth-two-steps"; // Stay on the OTP verification page
            }
        } else {
            LOGGER.warn("Invalid OTP provided for email: {}", email);
            model.addAttribute("error", "Invalid OTP. Please try again.");
            model.addAttribute("email", email);
            return "auth-two-steps"; // Stay on the OTP verification page
        }
    }

    @PostMapping("/register-multi-step")
    public String handleMultiStepRegistration(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam String lastName,
            @RequestParam String country,
            @RequestParam String state,
            @RequestParam String region,
            @RequestParam String city,
            @RequestParam String address,
            @RequestParam(required = false) String[] selectedRegions,
            @RequestParam String isBusiness,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String businessNumber,
            @RequestParam(required = false) String registrationCountry,
            @RequestParam String countryCode,
            @RequestParam String mobileNumber,
            Model model) {

        try {
            // Create a new Manager user entity
            User manager = new User();
            manager.setEmail(email);
            manager.setPassword(passwordEncoder.encode(password)); // Encode the password
            manager.setFirstName(firstName);
            manager.setMiddleName(middleName);
            manager.setLastName(lastName);
            manager.setCountry(country);
            manager.setState(state);
            manager.setRegion(region);
            manager.setCity(city.equals("not-listed") ? address : city); // Handle custom city
            manager.setAddress(address);
            manager.setCountryCode(countryCode);
            manager.setPhoneNumber(mobileNumber);

            if (isBusiness.equals("yes")) {
                manager.setBusinessName(companyName);
                manager.setBusinessRegistrationNumber(businessNumber);
                manager.setBusinessCountry(registrationCountry);
            }

            if (selectedRegions != null) {
                manager.setAssignedLocations(String.join(",", selectedRegions)); // Store regions as comma-separated values
            }

            // Set default role and status for Manager
            manager.setRole(Role.MANAGER);
            manager.setStatus(Status.APPLIED);
            manager.setCreatedAt(new Date());
            manager.setCreatedBy("SYSTEM");

            // Save the Manager user
            userRepository.save(manager);

            // Redirect to dashboard or success page
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred during registration: " + e.getMessage());
            return "auth-register-multisteps"; // Return to the form with an error
        }
    }

    @GetMapping("/set-password")
    public String showSetPasswordPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "auth-set-password";

    }

    @PostMapping("/set-password")
    public String handleSetPassword(
                @RequestParam(value = "error", required = false) String error,
                Model model, @RequestParam String password, @RequestParam String email) {

            model.addAttribute("password", password);
            model.addAttribute("email", email);
            // Fetch active countries for the first dropdown
            List<Country> countries = countryRepository.findByStatus("active");
            model.addAttribute("countries", countries);
            return "auth-register-multisteps";
    }

}

