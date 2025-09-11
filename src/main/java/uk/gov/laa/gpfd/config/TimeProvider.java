package uk.gov.laa.gpfd.config;

import java.time.LocalTime;

public interface TimeProvider {
    LocalTime getCurrentTime();
}