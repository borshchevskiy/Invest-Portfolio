package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;
import ru.investportfolio.database.entity.CashAction;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Setter
@Getter
public class CashEditDTO {

    @NotNull(message = "Field can't be blank.")
    @Positive(message = "Can not add negative amount.\r\n Use \"Remove cash\" function to decrease cash amount in portfolio.")
    @DecimalMax(value = "99999999999999", message = "Cash amount is too big.")
    private BigDecimal cash;

    @NotNull(message = "Cash action error. Try again!")
    private CashAction cashAction;
}
