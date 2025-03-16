package ru.andreev.clothsshop.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

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
    @Pattern(regexp = "^[0-9]{5}(?:-[0-9]{4})?$", message = "Invalid postal code")
    private String postalCode;
}