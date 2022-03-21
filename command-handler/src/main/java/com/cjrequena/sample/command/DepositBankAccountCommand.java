package com.cjrequena.sample.command;

import com.cjrequena.sample.dto.DepositBankAccountDTO;
import lombok.*;

import java.io.Serializable;

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
public class DepositBankAccountCommand extends Command<DepositBankAccountDTO> implements Serializable {

  @Builder
  public DepositBankAccountCommand(DepositBankAccountDTO depositBankAccountDTO, Long version) {
    super(depositBankAccountDTO.getAccountId(), ECommandType.DEPOSIT_BANK_ACCOUNT_COMMAND, depositBankAccountDTO, version);
  }
}
