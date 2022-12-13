package ru.investportfolio.dto.mapper;

import org.springframework.stereotype.Component;
import ru.investportfolio.database.entity.Share;
import ru.investportfolio.dto.ShareDatalistDTO;

@Component
public class ShareDatalistMapper implements Mapper<Share, ShareDatalistDTO> {

    @Override
    public ShareDatalistDTO map(Share object) {
        ShareDatalistDTO dto = new ShareDatalistDTO();
        dto.setShortName(object.getShortName());
        dto.setTicker(object.getTicker());
        return dto;
    }
    @Override
    public ShareDatalistDTO map(Share fromObject, ShareDatalistDTO toObject) {
        toObject.setShortName(fromObject.getShortName());
        toObject.setTicker(fromObject.getTicker());
        return toObject;
    }
}
