package com.cjrequena.sample.db.repository;

import com.cjrequena.sample.db.entity.BankAccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Repository
public interface BankAccountRepository extends CrudRepository<BankAccountEntity, UUID>, QueryByExampleExecutor<BankAccountEntity> {

}
