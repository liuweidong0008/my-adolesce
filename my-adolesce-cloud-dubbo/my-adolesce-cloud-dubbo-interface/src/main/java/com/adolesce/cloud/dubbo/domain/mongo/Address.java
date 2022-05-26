package com.adolesce.cloud.dubbo.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    //城市
    private String city;
    //街道
    private String street;

    private Location location;

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }
}