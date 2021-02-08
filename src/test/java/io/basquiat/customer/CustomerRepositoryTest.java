package io.basquiat.customer;

import io.basquiat.common.model.DateFormatType;
import io.basquiat.common.model.SearchDateType;
import io.basquiat.common.utils.DateUtils;
import io.basquiat.customer.model.dto.CustomerDto;
import io.basquiat.customer.model.entity.Address;
import io.basquiat.customer.model.entity.Customer;
import io.basquiat.customer.model.vo.SearchVo;
import io.basquiat.customer.repository.CustomerRepository;
import io.basquiat.customer.repository.spec.CustomerSpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static io.basquiat.common.utils.CommonUtils.convertJsonStringFromObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LOCAL_DATE;

@SpringBootTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void insertCustomer() {
        IntStream.range(1, 20)
                 .forEach(i -> {
                     Customer customer = Customer.builder()
                                                 .customerEmail("basquiat_email_" + i)
                                                 .customerName("basquiat_name_" + i)
                                                 .customerMobile("my_mobile_" + i)
                                                 .build();
                     customerRepository.save(customer);
                 });
    }

    @Test
    public void selectCustomer() {
        Optional<Customer> optional = customerRepository.findById(1L);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            assertThat("basquiat_name_1").isEqualTo(customer.getCustomerName());
        }
    }

    @Test
    public void updateCustomer() {
        Optional<Customer> optional = customerRepository.findById(1L);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            customer.changeCustomerMobile("000-000-0002");
            customerRepository.save(customer);
        }
    }

    @Test
    public void selectCustomerAfterUpdate() {
        Optional<Customer> optional = customerRepository.findById(1L);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            assertThat("000-000-0002").isEqualTo(customer.getCustomerMobile());
        }
    }

    @Test
    @Transactional
    public void selectCustomerWithAddress() {
        Optional<Customer> optional = customerRepository.findById(20L);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            assertThat("basquiat_email").isEqualTo(customer.getCustomerEmail());
            assertThat("basquiat_name").isEqualTo(customer.getCustomerName());
            List<Address> addressList = customer.getAddresses();
            assertThat("대표 주소").isEqualTo(addressList.get(0).getAddressName());
        }
    }

    @Test
    public void selectCustomerByEmail() {
        Optional<Customer> optional = customerRepository.findOne(CustomerSpec.condByEmail("basquiat_email_4"));
        if(optional.isPresent()) {
            Customer customer = optional.get();
            assertThat("basquiat_email_4").isEqualTo(customer.getCustomerEmail());
        }
    }

    @Test
    public void selectCustomerByEmailAndName() {
        String email = "basquiat_email_2";
        String name = "basquiat_name_2";
        Specification<Customer> spec = Specification.where(CustomerSpec.condByEmail(email));
        spec = spec.and(CustomerSpec.condByName(name)); // and는 앞에 선언된 조건에 AND 조건으로 조합한다는 의미이고 or은 OR 조건으로 조합한다.
        Optional<Customer> optional = customerRepository.findOne(spec);
        if(optional.isPresent()) {
            Customer customer = optional.get();
            assertThat("basquiat_email_2").isEqualTo(customer.getCustomerEmail());
            assertThat("basquiat_name_2").isEqualTo(customer.getCustomerName());
        }
    }

    @Test
    public void selectCustomerByQueryDSL() {
        List<Customer> customers = customerRepository.findAllCustomer();
        System.out.println(convertJsonStringFromObject(customers));
    }

    @Test
    public void selectCustomerByQueryDSLUsingTransform() {
        List<CustomerDto> customers = customerRepository.findAllCustomerDto();
        System.out.println(convertJsonStringFromObject(customers));
    }

    @Test
    @Transactional
    public void selectCustomerBySearchVO() {
        SearchVo searchVo = new SearchVo();
        searchVo.setName("basquiat_name_15");
        searchVo.setEmail("");
        Customer selected = customerRepository.findCustomerBySearchValue(searchVo);
        System.out.println(convertJsonStringFromObject(selected));
    }

    @Test
    @Transactional
    public void selectCustomerBySearchVOUsginBooleanBuilder() {
        SearchVo searchVo = new SearchVo();
        searchVo.setName("basquiat_name_15");
        searchVo.setEmail("");
        Customer selected = customerRepository.findCustomerBySearchValueUsginBooleanBuilder(searchVo);
        System.out.println(convertJsonStringFromObject(selected));
    }

    @Test
    @Transactional
    public void selectCustomerBySearchVOUsginQueryDelegate() {
        LocalDateTime now = LocalDateTime.now();
        String start = DateUtils.localDateTimeToDateString(now.minusDays(10L), DateFormatType.y_M_d);
        //String end = DateUtils.localDateTimeToDateString(now, DateFormatType.y_M_d);
        String end = DateUtils.localDateTimeToDateString(now.minusDays(7L), DateFormatType.y_M_d);
        SearchVo searchVo = new SearchVo();
        //searchVo.setName("basquiat_name_15");
        searchVo.setSearchDateType(SearchDateType.CREATED_AT);
        searchVo.setStart(start);
        //searchVo.setEnd(end);
        List<Customer> selected = customerRepository.findCustomerBySearchValueUsginQueryDelegate(searchVo);
        System.out.println(convertJsonStringFromObject(selected));
    }

}
