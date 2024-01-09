package uk.gov.laa.pfla.auth.service.models.report_view_models;

import lombok.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//TODO - This class will be a bean, dont forget to annotate it with  @org.springframework.beans.factory.annotation.Autowired to make sonarlint happy
// Bean guide: https://www.baeldung.com/spring-bean

/**
 * A class representing the data in the MOJFIN reports Table. This data/a subset of this data will eventually be
 * added to a newly generated .csv file when a new report is requested
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VCisToCcmsInvoiceSummaryModel implements ReportModel{


    private Date dateAuthorisedCis;
    private String theSystem;
    private double cisValue;
    private double ccmsValue;


    //overriding the lombok builders, so that we can set the correct date format
    public static class VCisToCcmsInvoiceSummaryModelBuilder{
        public VCisToCcmsInvoiceSummaryModel.VCisToCcmsInvoiceSummaryModelBuilder dateAuthorisedCis(String dateAuthorisedCis){
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                java.util.Date utilDate = dateFormat.parse(dateAuthorisedCis);
                this.dateAuthorisedCis = new Date(utilDate.getTime());
            }catch(ParseException e){
                throw new RuntimeException("Error parsing dateAuthorisedCis", e);
            }
            return this;
        }

    }



}
