package io.basquiat.customer.repository.spec;

import io.basquiat.customer.model.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

/**
 * Condition Spec using Specification
 * createc by baquiat
 *
 */
public class CustomerSpec {

    /**
     * 이메일로 검색하기
     * @param customerEmail
     * @return Specification<Customer>
     */
    public static Specification<Customer> condByEmail(String customerEmail) {
        return (Specification<Customer>) ((root, query, builder) ->
                builder.equal(root.get("customerEmail"), customerEmail)
        );
    }

    /**
     * 이름으로 검색하기
     * @param customerName
     * @return Specification<Customer>
     */
    public static Specification<Customer> condByName(String customerName) {
        return (Specification<Customer>) ((root, query, builder) ->
                builder.equal(root.get("customerName"), customerName)
        );
    }
}
