package ru.ali.ecommerce.customer;

import org.apache.commons.lang.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ali.ecommerce.exception.CustomerNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    public String createCustomer(CustomerRequest request) {
        var customer = customerRepository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    public void updateCustomer(CustomerRequest request) {
        var customer = customerRepository.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                        String.format("Cannot update customer:: Customer with id %s not found", request.id())
                ));
        mergerCustomer(customer, request);
        customerRepository.save(customer);
    }

    private void mergerCustomer(Customer customer, CustomerRequest request) {
        if(StringUtils.isNotBlank(request.firstname())){
            customer.setFirstname(request.firstname());
        }
        if(StringUtils.isNotBlank(request.lastname())){
            customer.setLastname(request.lastname());
        }
        if(StringUtils.isNotBlank(request.email())){
            customer.setEmail(request.email());
        }
        if(request.address() != null){
            customer.setAddress(request.address());
        }
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .stream().map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public Boolean exists(String customerId) {
        return customerRepository.findById(customerId).isPresent();
    }

    public CustomerResponse findById(String customerId) {
        return customerRepository
                .findById(customerId)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException(
                    String.format("Cannot update customer:: Customer with id %s not found", customerId)
                ));
    }

    public void deleteById(String customerId) {
        customerRepository.deleteById(customerId);
    }
}
