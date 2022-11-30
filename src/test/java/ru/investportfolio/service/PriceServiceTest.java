package ru.investportfolio.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.investportfolio.dto.ShareUpdateDTO;
import ru.investportfolio.exception.EmptyShareDataException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @Mock
    private RestTemplate mockRestTemplate;
    @Mock
    private ResponseEntity<ShareUpdateDTO> mockResponseEntity;

    private static final String SHARE_PRICE_REQUEST_URL =
            "https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities/";
    private static final String SHARE_PRICE_REQUEST_PARAMS =
            ".json?iss.meta=off&iss.only=marketdata&marketdata.columns=LAST";
    private static final String TICKER = "GAZP";
    private static final String[] TICKERS = new String[]{"GAZP","LKOH"};
    private static final String REQUEST_URL = SHARE_PRICE_REQUEST_URL + TICKER + SHARE_PRICE_REQUEST_PARAMS;

    @InjectMocks
    private PriceService priceService;

    @Nested
    class GetSharePriceByTicker {
        @Test
        void getSharePriceByTicker() {
            String[] columns = new String[]{"LAST"};
            Double[][] data = new Double[][]{{150.0d}};
            ShareUpdateDTO.MarketData marketData = new ShareUpdateDTO.MarketData();
            marketData.setData(data);
            marketData.setColumns(columns);
            ShareUpdateDTO shareUpdateDTO = new ShareUpdateDTO();
            shareUpdateDTO.setMarketdata(marketData);
            ResponseEntity<ShareUpdateDTO> responseEntity = new ResponseEntity<>(shareUpdateDTO, HttpStatus.OK);
            doReturn(responseEntity)
                    .when(mockRestTemplate)
                    .exchange(REQUEST_URL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ShareUpdateDTO>() {
                            });
            BigDecimal actualPrice = priceService.getSharePriceByTicker(TICKER);
            BigDecimal expectedPrice = BigDecimal.valueOf(150.0d);
            assertEquals(expectedPrice, actualPrice);
        }
        @Test
        void restClientException() {
            doThrow(new RestClientException("message")).when(mockRestTemplate)
                    .exchange(REQUEST_URL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ShareUpdateDTO>() {
                            });
            BigDecimal actualResult = null;
            try {
                actualResult = priceService.getSharePriceByTicker(TICKER);
            } catch (Exception ex) {
                Assertions.fail("Failed due to exception");
            }
            assertEquals(BigDecimal.ZERO, actualResult);
            verify(mockRestTemplate, times(1))
                    .exchange(anyString(), any(), any(), eq(new ParameterizedTypeReference<ShareUpdateDTO>() {
                    }));
        }
        @Test
        void emptyResponseBodyException() {
            doReturn(mockResponseEntity)
                    .when(mockRestTemplate)
                    .exchange(REQUEST_URL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ShareUpdateDTO>() {
                            });
            doThrow(new EmptyShareDataException()).when(mockResponseEntity).getBody();
            BigDecimal actualResult = null;
            try {
                actualResult = priceService.getSharePriceByTicker(TICKER);
            } catch (Exception ex) {
                Assertions.fail("Failed due to exception");
            }
            assertEquals(BigDecimal.ZERO, actualResult);
            verify(mockResponseEntity, times(1)).getBody();
        }
        @Test
        void indexOutOfBoundsException() {
            ShareUpdateDTO.MarketData marketData = mock(ShareUpdateDTO.MarketData.class);
            ShareUpdateDTO shareUpdateDTO = new ShareUpdateDTO();
            shareUpdateDTO.setMarketdata(marketData);
            ResponseEntity<ShareUpdateDTO> responseEntity = new ResponseEntity<>(shareUpdateDTO, HttpStatus.OK);
            doReturn(responseEntity)
                    .when(mockRestTemplate)
                    .exchange(REQUEST_URL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ShareUpdateDTO>() {
                            });
            doThrow(new IndexOutOfBoundsException()).when(marketData).getData();
            BigDecimal actualResult = null;
            try {
                actualResult = priceService.getSharePriceByTicker(TICKER);
            } catch (Exception ex) {
                Assertions.fail("Failed due to exception");
            }
            assertEquals(BigDecimal.ZERO, actualResult);
            verify(marketData, times(1)).getData();
        }
        @Test
        void exception() {
            doThrow(new RuntimeException()).when(mockRestTemplate)
                    .exchange(REQUEST_URL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<ShareUpdateDTO>() {
                            });
            BigDecimal actualResult = null;
            try {
                actualResult = priceService.getSharePriceByTicker(TICKER);
            } catch (Exception ex) {
                Assertions.fail("Failed due to exception");
            }
            assertEquals(BigDecimal.ZERO, actualResult);
            verify(mockRestTemplate, times(1))
                    .exchange(anyString(), any(), any(), eq(new ParameterizedTypeReference<ShareUpdateDTO>() {
                    }));
        }
    }

    @Test
    void getSharePricesByTickers() {
        PriceService spyPriceService = spy(priceService);
        doReturn(BigDecimal.valueOf(150)).when(spyPriceService).getSharePriceByTicker("GAZP");
        doReturn(BigDecimal.valueOf(5000)).when(spyPriceService).getSharePriceByTicker("LKOH");
        Map<String, BigDecimal> expectedResult = new HashMap<>();
        expectedResult.put("GAZP", BigDecimal.valueOf(150));
        expectedResult.put("LKOH", BigDecimal.valueOf(5000));
        Map<String, BigDecimal> actualResult = spyPriceService.getSharePricesByTickers(TICKERS);

        assertEquals(expectedResult, actualResult);
    }
}