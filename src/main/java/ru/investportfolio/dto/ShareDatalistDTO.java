package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareDatalistDTO implements Comparable<ShareDatalistDTO> {

    private String ticker;

    private String shortName;

    @Override
    public String toString() {
        return shortName + " (" + ticker + ")";
    }
    @Override
    public int compareTo(ShareDatalistDTO o) {
        return shortName.compareTo(o.getShortName());
    }
}
