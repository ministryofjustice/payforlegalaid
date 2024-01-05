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
public class VCisToCcmsInvoiceSummaryModel implements ReportModel{


    private Date dateAuthorisedCis;
    private String theSystem;
    private double cisValue;
    private double ccmsValue;
    String sql = "SELECT ";




    public VCisToCcmsInvoiceSummaryModel(Date dateAuthorisedCis, String theSystem, double cisValue, double ccmsValue) {
        this.dateAuthorisedCis = dateAuthorisedCis;
        this.theSystem = theSystem;
        this.cisValue = cisValue;
        this.ccmsValue = ccmsValue;

    }

    public VCisToCcmsInvoiceSummaryModel() {
        //no args constructor needed for ModelMapper
    }




}
