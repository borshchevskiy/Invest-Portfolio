package ru.investportfolio.utils.share;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.investportfolio.dto.SharesInfoUpdateDTO;
import ru.investportfolio.exception.EmptyShareDataException;
import ru.investportfolio.service.ShareService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@RequiredArgsConstructor
@Slf4j
public class ShareListUpdater {

    private final ShareService shareService;
    private final RestTemplate restTemplate;

    private static final String SHARES_INFO_REQUEST_URL =
            "https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities.json?"
                    + "iss.meta=off&iss.only=securities&securities.columns=SECID,SHORTNAME,SECNAME,ISIN";

    /*
        This method retrieves all shares info from MOEX via iss query (see https://www.moex.com/a2193).
        Parameter - no parameters
        Returns - the array of arrays with share data. eg. response = [[share data], [share data] ... [share data]].
        [share data] = {SECID, SHORTNAME, SECNAME, ISIN}.
        SECID - ticker;
        SHORTNAME - short company name;
        SECNAME - full company name;
        ISIN - share identification number.
         */
    private Set<String[]> getSharesFromMarket() {
        String[][] data = new String[][]{};
        try {
            ResponseEntity<SharesInfoUpdateDTO> responseEntity = restTemplate
                    .exchange(SHARES_INFO_REQUEST_URL,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {
                            });

            SharesInfoUpdateDTO sharesInfo = responseEntity.getBody();
            if (sharesInfo == null) {
                throw new EmptyShareDataException("No data retrieved  for request " + SHARES_INFO_REQUEST_URL);
            }
            SharesInfoUpdateDTO.Info info = sharesInfo.getInfo();
            data = info.getInfo();

        } catch (RestClientException exception) {
            log.error("External server response error while retrieving data from market", exception);
        } catch (NullPointerException exception) {
            log.error("No data retrieved from market server", exception);
        } catch (IndexOutOfBoundsException exception) {
            log.error("Data retrieved from market server, but it is blank", exception);
        }

        return new HashSet<>(Arrays.asList(data));
    }

    private Set<String[]> getSharesFromDatabase() {
        return shareService.findAll()
                .stream()
                .map(share -> new String[]{
                        share.getTicker(),
                        share.getShortName(),
                        share.getFullName(),
                        share.getIsin()
                })
                .collect(toSet());
    }

    /*
    Compares shares sets from database and from market.
    If they are not equal, it means that some new shares were added to market, or some shares were de-listed.
    Performs retainAll to remove de-listed shares and addAll to add new shares.
     */
    private void performUpdate() {
        Set<String[]> fromDatabase = getSharesFromDatabase();
        Set<String[]> fromMarket = getSharesFromMarket();
        if (!fromDatabase.equals(fromMarket)) {
            fromDatabase.retainAll(fromMarket);
            fromDatabase.addAll(fromMarket);
            String updateRequest = createSQLRequest(fromDatabase);
            shareService.updateShareList(updateRequest);
        }
    }

    private String createSQLRequest(Set<String[]> dataset) {
        StringBuilder request = new StringBuilder("INSERT INTO stocks (ticker, short_name, full_name, isin, market) VALUES ('");
        for (String[] data : dataset) {
            String join = String.join("', '", data);
            request.append(join);
            request.append("', 'MOEX'),");
        }
        request.setCharAt(request.length() - 1, ';');
        return request.toString();
    }
}
