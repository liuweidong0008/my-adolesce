package com.adolesce.cloud.dubbo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserDemo implements Serializable {
    private Long id;
    private String username;
    private String address;
}