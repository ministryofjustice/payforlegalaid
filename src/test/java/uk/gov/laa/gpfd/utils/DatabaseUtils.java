package uk.gov.laa.gpfd.utils;

import org.springframework.stereotype.Component;

// MOJFIN access has been removed (LPF-1614); retained as a no-op until dependent IT/unit tests are cleaned up (LPF-1613)
@Component
public class DatabaseUtils {

  public void setUpMockMojfinDatabase() {
    // no-op: MOJFIN (ANY_REPORT) is no longer queried by the application
  }

  public void cleanUpMockMojfinDatabase() {
    // no-op: MOJFIN (ANY_REPORT) is no longer queried by the application
  }

}
