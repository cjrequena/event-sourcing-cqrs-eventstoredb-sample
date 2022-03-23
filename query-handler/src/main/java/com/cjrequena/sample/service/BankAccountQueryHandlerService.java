package com.cjrequena.sample.service;

import com.cjrequena.sample.db.entity.BankAccountEntity;
import com.cjrequena.sample.db.repository.BankAccountRepository;
import com.cjrequena.sample.dto.BankAccountDTO;
import com.cjrequena.sample.exception.service.BankAccountNotFoundServiceException;
import com.cjrequena.sample.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BankAccountQueryHandlerService {

  private final BankAccountRepository bankAccountRepository;
  private final BankAccountMapper bankAccountMapper;

  public BankAccountDTO retrieveById(UUID accountId) throws BankAccountNotFoundServiceException {
    //--
    Optional<BankAccountEntity> entity = this.bankAccountRepository.findById(accountId);
    if (!entity.isPresent()) {
      log.error("Bank account {} does not exist", accountId);
      throw new BankAccountNotFoundServiceException("Bank account {} does not exist");
    }
    return bankAccountMapper.toDTO(entity.get());
    //--
  }

  public List<BankAccountDTO> retrieve() {
    //--
    try {
      final List<BankAccountEntity> bankAccountEntities = Streamable.of(this.bankAccountRepository.findAll()).toList();
      return bankAccountEntities.stream().map(entity -> bankAccountMapper.toDTO(entity)).collect(Collectors.toList());
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw ex;
    }
    //--
  }

}
