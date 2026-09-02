package com.bookstore.customer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerDetailsRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must contain exactly 10 digits"
    )
    private String phoneNumber;

    @Valid
    private AddressRequest address;

    @NotBlank(message = "Delivery preference is required")
    private String deliveryPreference;

    public CustomerDetailsRequest() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AddressRequest getAddress() {
        return address;
    }

    public void setAddress(AddressRequest address) {
        this.address = address;
    }

    public String getDeliveryPreference() {
        return deliveryPreference;
    }

    public void setDeliveryPreference(String deliveryPreference) {
        this.deliveryPreference = deliveryPreference;
    }

    public static class AddressRequest {

        @NotBlank(message = "House number is required")
        private String houseNo;

        @NotBlank(message = "Street is required")
        private String street;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Pincode is required")
        @Pattern(
                regexp = "^[0-9]{6}$",
                message = "Pincode must contain exactly 6 digits"
        )
        private String pincode;

        @NotBlank(message = "Country is required")
        private String country;

        public AddressRequest() {
        }

        public String getHouseNo() {
            return houseNo;
        }

        public void setHouseNo(String houseNo) {
            this.houseNo = houseNo;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}