package uk.gov.laa.pfla.auth.service.models.report_view_models;

import com.azure.core.annotation.Get;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
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


    //overriding the lombok builders, so that we can set the correct date format
    public static class VBankMonthBuilder{
        public VBankMonthBuilder paymentDate(String paymentDate){
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                java.util.Date utilDate = dateFormat.parse(paymentDate);
                this.paymentDate = new Date(utilDate.getTime());
            }catch(ParseException e){
                throw new RuntimeException("Error parsing paymentDate", e);
            }
                return this;
            }

        public VBankMonthBuilder paymentMonth(String paymentMonth){
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                java.util.Date utilDate = dateFormat.parse(paymentMonth);
                this.paymentMonth = new Date(utilDate.getTime());
            }catch(ParseException e){
                throw new RuntimeException("Error parsing paymentMonth", e);
            }
            return this;
        }

    }


//    @Override
//    public String asCSV() {
//        return null;
//    }


}
