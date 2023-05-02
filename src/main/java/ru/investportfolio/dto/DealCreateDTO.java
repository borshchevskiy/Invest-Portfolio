package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;
import ru.investportfolio.database.entity.DealType;
import ru.investportfolio.database.entity.Position;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DealCreateDTO {

    @NotBlank(message = "{deal-create-dto.security-name-and-ticker.blank}")
    private String securityNameAndTicker;

    @NotNull(message = "{deal-create-dto.acquisition-price.null}")
    @Positive
    private BigDecimal acquisitionPrice;

    @NotNull(message = "{deal-create-dto.quantity.null}")
    @Positive
    private Long quantity;

    private DealType dealType;

    private boolean isCommission;

    private String marketCommissionType;

    private BigDecimal marketCommission;

    private BigDecimal brokerCommission;

    private BigDecimal otherCommission;

    private BigDecimal acquisitionValue;

    private BigDecimal totalAcquisitionValue; //incl. commissions

    @PastOrPresent
    private LocalDate date;

    private String comment;

    private Long portfolioId;

    private Position position;

    public String getSecurityName() {
        int tickerStartIndex = securityNameAndTicker.lastIndexOf('(');
        return securityNameAndTicker.substring(0, tickerStartIndex - 1);
    }

    public String getTicker() {
        int tickerStartIndex = securityNameAndTicker.lastIndexOf('(');
        return securityNameAndTicker.substring(tickerStartIndex + 1, securityNameAndTicker.length() - 1);
    }
}
