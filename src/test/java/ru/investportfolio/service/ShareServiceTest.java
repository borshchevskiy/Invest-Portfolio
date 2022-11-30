package ru.investportfolio.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.investportfolio.database.entity.Share;
import ru.investportfolio.database.repository.ShareRepository;
import ru.investportfolio.dto.ShareDatalistDTO;
import ru.investportfolio.dto.mapper.ShareDatalistMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShareServiceTest {

    @Mock
    private ShareRepository shareRepository;
    @Mock
    private ShareDatalistMapper shareDatalistMapper;

    private static final List<Share> SHARES = new ArrayList<>();
    private static final List<ShareDatalistDTO> SHARES_DTO = new ArrayList<>();

    @BeforeAll
    static void prepare() {
        Share firstShare = new Share();
        firstShare.setTicker("GAZP");
        firstShare.setShortName("Газпром");
        firstShare.setFullName("Акционерное общество Газпром");
        Share secondShare = new Share();
        secondShare.setTicker("LKOH");
        secondShare.setShortName("Лукойл");
        secondShare.setFullName("Акционерное общество Лукойл");
        SHARES.add(firstShare);
        SHARES.add(secondShare);
        ShareDatalistDTO firstDTO = new ShareDatalistDTO();
        firstDTO.setTicker(firstShare.getTicker());
        firstDTO.setShortName(firstShare.getShortName());
        ShareDatalistDTO secondDTO = new ShareDatalistDTO();
        secondDTO.setTicker(secondShare.getTicker());
        secondDTO.setShortName(secondShare.getShortName());
        SHARES_DTO.add(firstDTO);
        SHARES_DTO.add(secondDTO);
    }

    @InjectMocks
    private ShareService shareService;

    @Test
    void findAll() {
        doReturn(SHARES).when(shareRepository).findAll();
        var actualResult = shareService.findAll();
        assertEquals(SHARES, actualResult);
    }
    @Test
    void getTickersList() {
        doReturn(SHARES).when(shareRepository).findAll();
        var actualResult = shareService.getTickersList();
        var expectedResult = new ArrayList<>();
        expectedResult.add("GAZP");
        expectedResult.add("LKOH");
        assertEquals(expectedResult, actualResult);
    }
    @Test
    void getFullNamesList() {
        doReturn(SHARES).when(shareRepository).findAll();
        var actualResult = shareService.getFullNamesList();
        var expectedResult = new ArrayList<>();
        expectedResult.add("Акционерное общество Газпром");
        expectedResult.add("Акционерное общество Лукойл");
        assertEquals(expectedResult, actualResult);
    }
    @Test
    void getShortNamesList() {
        doReturn(SHARES).when(shareRepository).findAll();
        var actualResult = shareService.getShortNamesList();
        var expectedResult = new ArrayList<>();
        expectedResult.add("Газпром");
        expectedResult.add("Лукойл");
        assertEquals(expectedResult, actualResult);
    }
    @Test
    void getShortNamesAndTickers() {
        doReturn(SHARES).when(shareRepository).findAll();
        var actualResult = shareService.getShortNamesAndTickers();
        var expectedResult = new HashMap<>();
        expectedResult.put("Газпром", "GAZP");
        expectedResult.put("Лукойл", "LKOH");
        assertEquals(expectedResult, actualResult);
    }
    @Test
    void getFullNamesAndTickers() {
        doReturn(SHARES).when(shareRepository).findAll();
        var actualResult = shareService.getFullNamesAndTickers();
        var expectedResult = new HashMap<>();
        expectedResult.put("Акционерное общество Газпром", "GAZP");
        expectedResult.put("Акционерное общество Лукойл", "LKOH");
        assertEquals(expectedResult, actualResult);
    }
    @Test
    void getShareDatalist() {
        ShareDatalistDTO firstDTO = new ShareDatalistDTO();
        firstDTO.setTicker("GAZP");
        firstDTO.setShortName("Газпром");
        ShareDatalistDTO secondDTO = new ShareDatalistDTO();
        secondDTO.setTicker("LKOH");
        secondDTO.setShortName("Лукойл");
        doReturn(SHARES).when(shareRepository).findAll();
        doReturn(firstDTO).when(shareDatalistMapper).map(SHARES.get(0));
        doReturn(secondDTO).when(shareDatalistMapper).map(SHARES.get(1));
        var actualResult = shareService.getShareDatalist();
        var expectedResult = new ArrayList<>();
        expectedResult.add(firstDTO);
        expectedResult.add(secondDTO);
        assertEquals(expectedResult, actualResult);
    }
    @Test
    void updateShareList() {
        String request = "SQL request";
        shareService.updateShareList(request);
        verify(shareRepository, times(1)).insertWithQuery(request);
    }
}