package com.cjrequena.sample.command;

import com.cjrequena.sample.dto.WithdrawBankAccountDTO;
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
public class WithdrawBankAccountCommand extends Command<WithdrawBankAccountDTO> implements Serializable {

  @Builder
  public WithdrawBankAccountCommand(WithdrawBankAccountDTO withdrawBankAccountDTO, Long version) {
    super(withdrawBankAccountDTO.getAccountId(), ECommandType.WITHDRAW_BANK_ACCOUNT_COMMAND, withdrawBankAccountDTO, version);
  }
}
