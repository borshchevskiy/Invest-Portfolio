package ru.investportfolio.dto.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.investportfolio.database.entity.Deal;
import ru.investportfolio.database.entity.DealType;
import ru.investportfolio.database.entity.Portfolio;
import ru.investportfolio.database.repository.PortfolioRepository;
import ru.investportfolio.dto.DealCreateDTO;
import ru.investportfolio.exception.ItemNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DealCreateMapper implements Mapper<DealCreateDTO, Deal> {


    @Override
    public Deal map(DealCreateDTO object) {
        Deal deal = new Deal();
        copy(object, deal);
        return deal;
    }

    @Override
    public Deal map(DealCreateDTO fromObject, Deal toObject) {
        copy(fromObject, toObject);
        return toObject;
    }

    private void copy(DealCreateDTO dto, Deal deal) {
        deal.setSecurityName(dto.getSecurityName());
        deal.setTicker(dto.getTicker());
        deal.setAcquisitionPrice(dto.getAcquisitionPrice());
        deal.setQuantity(dto.getQuantity());
        deal.setDealType(dto.getDealType());
        deal.setAcquisitionValue(dto.getAcquisitionValue());
        deal.setMarketCommission(dto.getMarketCommission());
        deal.setBrokerCommission(dto.getBrokerCommission());
        deal.setOtherCommission(dto.getOtherCommission());
        deal.setTotalAcquisitionValue(dto.getTotalAcquisitionValue());
        deal.setDate(dto.getDate());
        deal.setComment(dto.getComment());
        deal.setPosition(dto.getPosition());
    }
}
