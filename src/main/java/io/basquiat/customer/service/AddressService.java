package io.basquiat.customer.service;

import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.AddressVo;
import io.basquiat.customer.repository.AddressRepository;
import io.basquiat.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddressService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void save(Long customerId, AddressVo addressVo) {
        Optional<Customer> optional = customerRepository.findById(customerId);
        Customer customer = optional.get();
        customer = em.merge(customer);
        Address address = Address.builder()
                                 .addressName(addressVo.getName())
                                 .addressPostcode(addressVo.getPostcode())
                                 .addressInfo(addressVo.getInfo())
                                 .addressDetail(addressVo.getDetail())
                                 .addressDefaultStatus(addressVo.getAddressDefaultStatus())
                                 .customer(customer)
                                 .build();
        addressRepository.save(address);
    }

}
