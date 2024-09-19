package com.womack.spring6restmvcmaven.mappers;

import com.womack.spring6restmvcmaven.entities.Customer;
import com.womack.spring6restmvcmaven.model.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDTO dto);

    CustomerDTO customerToCustomerDTO(Customer customer);
}
