package io.basquiat.customer;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.basquiat.common.utils.CommonUtils.convertJsonStringFromObject;

@SpringBootTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void selectCustomerByQueryDSLUsingTransform() {
        List<CustomerDto> customers = customerRepository.findAllCustomerDto();
        System.out.println(convertJsonStringFromObject(customers));
    }

}
