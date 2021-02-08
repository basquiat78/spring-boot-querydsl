package io.basquiat.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * pageable 처리를 위해 JpaRepository와 JpaSpecificationExecutor를 상속받은 공통 리파지토리를 만든다.
 * @param <M>
 * @param <I>
 */
@NoRepositoryBean
public interface BaseRepository<M, I extends Serializable> extends JpaRepository<M, I>, JpaSpecificationExecutor<M> {
}
