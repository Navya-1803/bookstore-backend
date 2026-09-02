package com.bookstore.customer.service;

import com.bookstore.customer.dto.CustomerDetailsRequest;
import com.bookstore.customer.entity.Address;
import com.bookstore.customer.entity.CustomerProfile;
import com.bookstore.customer.repository.CustomerProfileRepository;
import com.bookstore.user.entity.User;
import com.bookstore.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;

    public CustomerService(
            CustomerProfileRepository customerProfileRepository,
            UserRepository userRepository
    ) {
        this.customerProfileRepository = customerProfileRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> getCustomerDetailsByEmail(
            String email
    ) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        CustomerProfile profile =
                customerProfileRepository
                        .findByUserId(user.getId())
                        .orElse(null);

        if (profile == null) {
            return new HashMap<>();
        }

        return convertToResponse(profile);
    }

    @Transactional
    public Map<String, Object> updateCustomerDetailsByEmail(
            String email,
            CustomerDetailsRequest request
    ) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        CustomerProfile profile =
                customerProfileRepository
                        .findByUserId(user.getId())
                        .orElseGet(() -> {

                            CustomerProfile newProfile =
                                    new CustomerProfile();

                            newProfile.setUser(user);

                            return newProfile;
                        });

        profile.setPhoneNumber(
                request.getPhoneNumber()
        );

        profile.setDeliveryPreference(
                request.getDeliveryPreference()
        );

        Address address = new Address();

        address.setHouseNo(
                request.getAddress().getHouseNo()
        );

        address.setStreet(
                request.getAddress().getStreet()
        );

        address.setCity(
                request.getAddress().getCity()
        );

        address.setState(
                request.getAddress().getState()
        );

        address.setPincode(
                request.getAddress().getPincode()
        );

        address.setCountry(
                request.getAddress().getCountry()
        );

        profile.setAddress(address);

        CustomerProfile savedProfile =
                customerProfileRepository.save(profile);

        return convertToResponse(savedProfile);
    }

    private Map<String, Object> convertToResponse(
            CustomerProfile profile
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put("id", profile.getId());
        response.put(
                "phoneNumber",
                profile.getPhoneNumber()
        );
        response.put(
                "deliveryPreference",
                profile.getDeliveryPreference()
        );

        Address address = profile.getAddress();

        if (address != null) {

            Map<String, String> addressResponse =
                    new HashMap<>();

            addressResponse.put(
                    "houseNo",
                    address.getHouseNo()
            );

            addressResponse.put(
                    "street",
                    address.getStreet()
            );

            addressResponse.put(
                    "city",
                    address.getCity()
            );

            addressResponse.put(
                    "state",
                    address.getState()
            );

            addressResponse.put(
                    "pincode",
                    address.getPincode()
            );

            addressResponse.put(
                    "country",
                    address.getCountry()
            );

            response.put(
                    "address",
                    addressResponse
            );
        }

        return response;
    }
}