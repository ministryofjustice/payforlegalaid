package uk.gov.laa.pfla.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportLabelEnum {
    USER_DEFINED_PAYMENT_GROUPS_LABEL("User defined payment groups", "A3"),
    USER_DEFINED_PAYMENT_GROUPS_VALUE("User defined payment groups value", "A4"),
    TOTAL_PAYABLE_DEFINED_LABEL("Total payable under payment groups define above", "A9"),
    TOTAL_PAYABLE_DEFINED_VOLUME_OF_OFFICES("Total payable under payment groups define above", "B9"),
    TOTAL_PAYABLE_DEFINED_TOTAL_VALUE("Total payable under payment groups define above", "C9"),
    TOTAL_PAYABLE_NOT_DEFINED_LABEL("Total payable under payment groups not define above", "A10"),
    TOTAL_PAYABLE_NOT_DEFINED_VOLUME_OF_OFFICES("Total payable under payment groups not define above", "B10"),
    TOTAL_PAYABLE_NOT_DEFINED_TOTAL_VALUE("Total payable under payment groups not define above", "C10"),
    TOTAL_UNPAID_ON_HOLD_LABEL("Total unpaid due to office on hold", "A11"),
    TOTAL_UNPAID_ON_HOLD_VOLUME_OF_OFFICES("Total unpaid due to office on hold", "B11"),
    TOTAL_UNPAID_ON_HOLD_TOTAL_VALUE("Total unpaid due to office on hold", "C11");


    private final String label;
    private final String address;

}