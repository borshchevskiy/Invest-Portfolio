package ru.investportfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.investportfolio.dto.ShareUpdateDTO;
import ru.investportfolio.exception.EmptyShareDataException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceService {

    private final RestTemplate restTemplate;
    private static final String SHARE_PRICE_REQUEST_URL =
            "https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities/";
    private static final String SHARE_PRICE_REQUEST_PARAMS =
            ".json?iss.meta=off&iss.only=marketdata&marketdata.columns=LAST";


    /*
    This method retrieves share price from MOEX via iss query (see https://www.moex.com/a2193).
    Parameter - share ticker on MOEX eg. "GAZP" for Gazprom
    Returns - the Double value of the current price of the share. Current price - the price of the last trade on MOEX.
     */
    public BigDecimal getSharePriceByTicker(String shareTicker) {
        String requestUrl = SHARE_PRICE_REQUEST_URL + shareTicker + SHARE_PRICE_REQUEST_PARAMS;
        BigDecimal currentPrice = BigDecimal.ZERO;
        try {
            ResponseEntity<ShareUpdateDTO> responseEntity = restTemplate
                    .exchange(requestUrl,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {
                            });
            ShareUpdateDTO share = responseEntity.getBody();
            if (share == null) {
                throw new EmptyShareDataException();
            }
            ShareUpdateDTO.MarketData marketData = share.getMarketdata();
            currentPrice = BigDecimal.valueOf(marketData.getData()[0][0]);
        } catch (RestClientException exception) {
            log.error("Server response error while retrieving data from market", exception);
        } catch (EmptyShareDataException exception) {
            log.error("No data retrieved from market server", exception);
        } catch (IndexOutOfBoundsException exception) {
            log.error("Data retrieved from market server is blank", exception);
        } catch (Exception exception) {
            log.error("Error while retrieving data from market server", exception);
        }
        return currentPrice;
    }


    public Map<String, BigDecimal> getSharePricesByTickers(String... tickers) {
        Map<String, BigDecimal> prices = new HashMap<>();
        for (String ticker : tickers) {
            BigDecimal price = getSharePriceByTicker(ticker);
            prices.put(ticker, price);
        }
        return prices;
    }


}
