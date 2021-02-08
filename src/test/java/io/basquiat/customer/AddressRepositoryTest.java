package io.basquiat.customer;

import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.repository.AddressRepository;
import io.basquiat.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Test
    public void insertAddress() {
        Customer customer = Customer.builder()
                                    .customerEmail("basquiat_email")
                                    .customerName("basquiat_name")
                                    .customerMobile("my_mobile")
                                    .build();

        Address address = Address.builder()
                                 .addressName("대표 주소")
                                 .addressPostcode("postcode")
                                 .addressInfo("나는 서울에서 산다.")
                                 .addressDetail("서울에 있는 내집")
                                 .addressDefaultStatus(Address.AddressDefaultStatus.Y)
                                 .customer(customer)
                                 .build();
        addressRepository.save(address);
    }

}
