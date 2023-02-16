package ru.investportfolio.controller.attributes;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.investportfolio.dto.ShareDatalistDTO;
import ru.investportfolio.service.ShareService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.naturalOrder;

//@ControllerAdvice(basePackages = "ru.investportfolio.controller.DealController")
//@ControllerAdvice(basePackageClasses = )
@ControllerAdvice()
@RequiredArgsConstructor
public class ShareNames {

    private final ShareService shareService;

    @ModelAttribute("shareNames")
    private List<String> getShareNamesAndTickers() {
        List<String> shareNames = new ArrayList<>(shareService.getShareDatalist()
                .stream()
                .map(ShareDatalistDTO::toString)
                .toList());

        shareNames.sort(naturalOrder());

        return shareNames;
    }
}
