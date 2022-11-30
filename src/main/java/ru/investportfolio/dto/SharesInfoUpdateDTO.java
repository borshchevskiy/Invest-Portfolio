package ru.investportfolio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharesInfoUpdateDTO {
    private Info info;

    public Info getInfo() {
        return info;
    }
    public void setInfo(Info info) {
        this.info = info;
    }

    @Getter
    @Setter
    public class Info {
        private String[] columns;
        private String[][] info;

    }
}
