//This class is here in case we need to deal with converting dates in MOJFIN from one format to another

//package uk.gov.laa.pfla.auth.service.helpers;
//import org.modelmapper.Converter;
//import org.modelmapper.spi.MappingContext;
//
//import java.sql.Date;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//
//public class DateMapper {
//
//
//    public static Converter<String, Date> getDateConverter() {
//        return new Converter<String, Date>() {
//            @Override
//            public Date convert(MappingContext<String, Date> context) {
//                String inputDate = context.getSource();
//
//                // Parse the input date
//                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
//                try {
//                    java.util.Date parsedDate = inputFormat.parse(inputDate);
//
//                    // Format the date in "DDMMMYYYY" format
//                    SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMyyyy");
//                    String formattedDate = outputFormat.format(parsedDate);
//
//                    // Parse the formatted date to java.sql.Date
//                    return Date.valueOf(formattedDate);
//                } catch (ParseException e) {
//                    throw new RuntimeException("Error parsing date", e);
//                }
//            }
//        };
//    }
//}
//
