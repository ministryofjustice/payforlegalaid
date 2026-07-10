package uk.gov.laa.pfla.util;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ResponseEntity;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.util.workbook.WorkbookCreator;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;

public interface WorkbookUtil {

    Workbook getExcelWorkbook();
    WorkbookCreator workbookCreator();

    @SneakyThrows
    default Workbook getExcelWorkbook(ScenarioContext scenarioContext) {
        return ofNullable(scenarioContext.getResponseAs(byte[].class))
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .map(workbookCreator()::construct)
                .orElseThrow(() -> new RuntimeException("Failed to download the file or invalid response."));
    }

    @SneakyThrows
    default Workbook loadExcelFromResources(String pattern, Object ... arguments) {
        return ofNullable(loadResourceAsBytes(format(pattern, arguments)))
                .map(workbookCreator()::construct)
                .orElseThrow(() -> new FileNotFoundException("Failed to load the file " + format(pattern, arguments)));
    }

    private byte[] loadResourceAsBytes(String fileName) {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return inputStream == null ? null : inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + fileName, e);
        }
    }

}
