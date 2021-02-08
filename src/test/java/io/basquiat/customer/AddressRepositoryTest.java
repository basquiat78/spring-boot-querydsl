package io.basquiat.customer;

import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.AddressVo;
import io.basquiat.customer.repository.AddressRepository;
import io.basquiat.customer.repository.CustomerRepository;
import io.basquiat.customer.service.AddressService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static io.basquiat.common.utils.CommonUtils.convertJsonStringFromObject;

@SpringBootTest
public class AddressRepositoryTest {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    @PersistenceContext
    private EntityManager em;

    @Test
    public void insertAddress_One() {
        Customer customer = Customer.builder()
                                    .customerEmail("basquiat_email_NEW1")
                                    .customerName("basquiat_name_NEW1")
                                    .customerMobile("my_mobile_NEW1")
                                    .build();
         Address address = Address.builder()
                                  .addressName("대표 주소_NEW")
                                  .addressPostcode("postcode")
                                  .addressInfo("나는 서울에서 산다._NEW")
                                  .addressDetail("서울에 있는 내집_NEW")
                                  .customer(customer)
                                  .build();
        addressRepository.save(address);
    }

    @Test
    public void insertAddress_Two() {
        IntStream.range(1, 8)
                 .forEach(i -> {
                         Long customerId = 20L;
                         AddressVo addressVo = new AddressVo();
                         addressVo.setName("새로운 주소_" + i);
                         addressVo.setPostcode("새로운우편번호_" + i);
                         addressVo.setInfo("서울");
                         addressVo.setDetail("우리집_" + i);
                         addressVo.setAddressDefaultStatus(Address.AddressDefaultStatus.N);
                         addressService.save(customerId, addressVo);
                 });
    }

    @Test
    public void getAddressListByCustomerId() {
        Long customerId = 20L;
        int page = 0; // 첫번째 페이지
        int size = 5; // 5개씩 보여주기
        Pageable pageable = PageRequest.of(page, size);
        Page<Address> addresses = addressRepository.findAddressListByCustomerId(customerId, pageable);
        System.out.println(addresses.getContent());
        System.out.println(addresses.getPageable());

    }

    @Test
    public void getAddressListByCustomerIdUsingQueryDSL() {
        Long customerId = 20L;
        int page = 0; // 첫번째 페이지
        int size = 5; // 5개씩 보여주기
        Pageable pageable = PageRequest.of(page, size);
        List<Address> addressList = addressRepository.findAddressList(customerId, pageable);
        System.out.println(addressList.get(0).getCustomer());
    }

    @Test
    public void getAddressListByCustomerIdUsingQueryDSL_ONE() {
        //Long customerId = 2L;
        Long customerId = 20L;
        int page = 0; // 첫번째 페이지
        int size = 5; // 5개씩 보여주기
        Pageable pageable = PageRequest.of(page, size);
        CustomerDto dto = addressRepository.findAddressListOne(customerId, pageable);
        System.out.println(convertJsonStringFromObject(dto));
    }

}
