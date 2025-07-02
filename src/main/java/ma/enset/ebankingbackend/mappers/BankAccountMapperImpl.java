package ma.enset.ebankingbackend.mappers;

import ma.enset.ebankingbackend.dtos.CustomerDTO;
import ma.enset.ebankingbackend.entities.Customer;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    public CustomerDTO customerToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        // c'est mieux d'utiliser la class BeansUtils : premier solution y'as aussi des framework qui font ca comme mappstruct
        BeanUtils.copyProperties(customer, customerDTO);
//      customerDTO.setId(customer.getId());
//      customerDTO.setName(customer.getName());
//      customerDTO.setEmail(customer.getEmail());
        return customerDTO;
    }

    public Customer customerDTOToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        return customer;
    }
}
