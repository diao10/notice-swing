package com.notice.enums;

import lombok.Getter;

/**
 * @author diaoyn
 * @create 2024-07-07 23:16:56
 */
@Getter
public enum FundEnum {

    CCB_FUNDS("建信基金"),
    CS_FUNDS("长盛基金"),
    EAST_MONEY("天天基金");

    private final String name;

    FundEnum(String name) {
        this.name = name;
    }
}
