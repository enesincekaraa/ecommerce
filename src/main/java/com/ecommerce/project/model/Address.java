package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5,message = "Street name must be at least 5 characters")
    private String street;

    @NotBlank
    @Size(min = 2,message = "Building name must be at least 2 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 3,message = "City name must be at least 3 characters")
    private String city;

    @NotBlank
    @Size(min = 2,message = "State name must be at least 2 characters")
    private String state;

    @NotBlank
    @Size(min = 2,message = "Country name must be at least 2 characters")
    private String country;

    @NotBlank
    @Size(min = 6,message = "Pincode name must be at least 6 characters")
    private String pincode;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String pincode, String country, String state, String city, String buildingName, String street) {
        this.pincode = pincode;
        this.country = country;
        this.state = state;
        this.city = city;
        this.buildingName = buildingName;
        this.street = street;
    }
}
