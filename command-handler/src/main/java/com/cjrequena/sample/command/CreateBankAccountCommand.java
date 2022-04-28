package com.cjrequena.sample.command;

import com.cjrequena.sample.dto.BankAccountDTO;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * <p>
 * <p>
 * <p>
 * <p>
 *
 * @author cjrequena
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
@EqualsAndHashCode(callSuper = false)
public class CreateBankAccountCommand extends Command<BankAccountDTO> implements Serializable {

  @Builder
  public CreateBankAccountCommand(BankAccountDTO bankAccountDTO) {
    super(UUID.randomUUID(), ECommandType.CREATE_BANK_ACCOUNT_COMMAND, bankAccountDTO, 0L);
    bankAccountDTO.setId(super.aggregateId);
  }
}
