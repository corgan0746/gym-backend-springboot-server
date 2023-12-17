package com.corgan.angularbackend.datamodels;

import com.corgan.angularbackend.entity.Customer;
import lombok.Data;

@Data
public class CustomerAndUrl {

    private Customer customer;

    private String url;

}
