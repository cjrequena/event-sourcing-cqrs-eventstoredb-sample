package com.cjrequena.sample.mapper;

import com.cjrequena.sample.db.entity.BankAccountEntity;
import com.cjrequena.sample.dto.BankAccountDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
  componentModel = "spring",
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BankAccountMapper {

  BankAccountEntity toEntity(BankAccountDTO dto);

  BankAccountDTO toDTO(BankAccountEntity entity);

}
