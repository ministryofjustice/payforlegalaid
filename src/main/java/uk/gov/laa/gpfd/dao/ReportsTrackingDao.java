package uk.gov.laa.gpfd.dao;

public class ReportsTrackingDao {
    private static final String ADD_CREATOR_SQL = "INSERT INTO GPFDS.REPORT_TRACKING (" +
            "ID, NAME, REPORT_ID, CREATION_DATE, REPORT_CREATOR, REPORT_OWNER, REPORT_OUTPUT_TYPE, ) " +
            "VALUES (?,?,?,?,?,?)";

}
