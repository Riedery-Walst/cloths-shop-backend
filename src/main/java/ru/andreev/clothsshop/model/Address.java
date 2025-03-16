package ru.andreev.clothsshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Entity
@Data
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Country is required")
    private String country;

    @NotEmpty(message = "City is required")
    private String city;

    @NotEmpty(message = "Street is required")
    private String street;

    @NotEmpty(message = "Street is required")
    private String house;

    private String apartment;

    @NotEmpty(message = "Postal Code is required")
    private String postalCode;
}
