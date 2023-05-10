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

    @NotNull(message = "{cash-edit-dto.cash.null}")
    @Positive(message = "{cash-edit-dto.cash.positive}")
    @DecimalMax(value = "99999999999999", message = "{cash-edit-dto.cash.max}")
    private BigDecimal cash;

    @NotNull(message = "{cash-edit-dto.cash-action.null}")
    private CashAction cashAction;
}
