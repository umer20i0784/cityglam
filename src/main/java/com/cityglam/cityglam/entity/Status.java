package com.cityglam.cityglam.entity;

public enum Status {
    APPLIED,    // Newly registered
    PENDING,    // Awaiting approval or KYC
    ACTIVE,     // Fully operational
    SUSPENDED,  // Suspended account
    CANCELLED   // Deleted or cancelled account
}
