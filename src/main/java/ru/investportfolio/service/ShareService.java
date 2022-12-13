package ru.investportfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.investportfolio.database.entity.Share;
import ru.investportfolio.database.repository.ShareRepository;
import ru.investportfolio.dto.ShareDatalistDTO;
import ru.investportfolio.dto.mapper.ShareDatalistMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final ShareDatalistMapper shareDatalistMapper;

    public List<Share> findAll() {
        return shareRepository.findAll();
    }

    public List<String> getTickersList() {
        return shareRepository.findAll()
                .stream()
                .map(Share::getTicker)
                .collect(toList());
    }

    public List<String> getFullNamesList() {
        return shareRepository.findAll()
                .stream()
                .map(Share::getFullName)
                .collect(toList());
    }

    public List<String> getShortNamesList() {
        return shareRepository.findAll()
                .stream()
                .map(Share::getShortName)
                .collect(toList());
    }

    public Map<String, String> getShortNamesAndTickers() {
        return shareRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Share::getShortName, Share::getTicker));
    }

    public Map<String, String> getFullNamesAndTickers() {
        return shareRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Share::getFullName, Share::getTicker));
    }

    public List<ShareDatalistDTO> getShareDatalist() {
        return shareRepository.findAll()
                .stream()
                .map(shareDatalistMapper::map)
                .collect(toList());
    }

    public void updateShareList(String updateRequest) {
        shareRepository.insertWithQuery(updateRequest);
    }
}
