package uk.gov.laa.pfla.auth.service.models.report_view_models;

import lombok.Data;

import java.sql.Date;

//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN reports Table. A subset of this data will eventually be
 * returned to the user via the /report endpoint, in the form of a ReportListResponse
 */
@Data
public class VBankMonth implements ReportModel{

    private String source;
    private String invSource;
    private String subSource;
    private Date paymentDate;
    private Date paymentMonth;
    private String settlementType;
    private String scheme;
    private String subScheme;
    private String detailDesc;
    private String catCode;
    private String apArMovement;
    private double total;


    public VBankMonth(String source, String invSource, String subSource, Date paymentDate, Date paymentMonth, String settlementType, String scheme, String subScheme, String detailDesc, String catCode, String apArMovement, double total) {
        this.source = source;
        this.invSource = invSource;
        this.subSource = subSource;
        this.paymentDate = paymentDate;
        this.paymentMonth = paymentMonth;
        this.settlementType = settlementType;
        this.scheme = scheme;
        this.subScheme = subScheme;
        this.detailDesc = detailDesc;
        this.catCode = catCode;
        this.apArMovement = apArMovement;
        this.total = total;
    }

    public VBankMonth() {
        //no args constructor needed for ModelMapper
    }




}
