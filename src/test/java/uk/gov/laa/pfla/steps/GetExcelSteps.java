package uk.gov.laa.pfla.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import uk.gov.laa.pfla.assertion.CellCheck;
import uk.gov.laa.pfla.assertion.FieldValidators;
import uk.gov.laa.pfla.assertion.WorkbookAssert;
import uk.gov.laa.pfla.assertion.WorkbookHelper;
import uk.gov.laa.pfla.enums.ReportLabelEnum;
import uk.gov.laa.pfla.enums.ReportSheetEnum;
import uk.gov.laa.pfla.scenario.ScenarioContext;
import uk.gov.laa.pfla.service.HttpProvider;
import uk.gov.laa.pfla.util.WorkbookUtil;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.ContentDisposition.attachment;
import static org.springframework.http.MediaType.parseMediaType;

public record GetExcelSteps(HttpProvider httpProvider, ScenarioContext scenarioContext, WorkbookUtil excelService) {

    private static final String APPLICATION_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @When("a request is made to the Excel endpoint with the report ID {string}")
    public void aRequestIsMadeToTheExcelEndpointWithTheReportId(String reportId) {
        scenarioContext.setResponse(httpProvider.getClient().getForEntity("/reports/" + reportId + "/excel", byte[].class));
    }

    @Then("the response should include the Excel file with {string} report")
    public void verifyExcelResponse(String file) {
        var currentResult = excelService.getExcelWorkbook();
        var expectedResult = excelService.loadExcelFromResources("expected_results/{0}.xlsx", file);
        var headers = scenarioContext.getResponseAs(Object.class).getHeaders();

        assertAll("Verify Excel response",
                () -> assertThat(headers.getContentType()).isEqualTo(parseMediaType(APPLICATION_EXCEL)),
                () -> assertThat(headers.getContentDisposition()).isEqualTo(
                    attachment().filename(file + ".xlsx").build()),
                () -> WorkbookAssert.assertThat(currentResult).isEqualTo(expectedResult),
                () -> WorkbookAssert.assertThat(currentResult).hasAllFormulasEvaluated(),
                () -> WorkbookAssert.assertThat(currentResult).hasValidPivotTables()
        );
    }

    @Then("the report is not empty")
    public void theExcelReportShouldNotBeEmpty() {
        var workbook = excelService.getExcelWorkbook();

        assertThat(workbook).isNotNull();
        assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);
        assertThat(workbook.getSheetAt(0)).isNotNull();
        assertThat(WorkbookHelper.getRequiredSheet(workbook, ReportSheetEnum.MAIN.getName())).isNotNull();

    }
    @Then("the report should contain all mandatory fields")
    public void theExcelReportShouldPassMainTabHybridChecks() {
        var workbook = excelService.getExcelWorkbook();

        var sheet = workbook.getSheet(ReportSheetEnum.MAIN.getName());

        var requiredCellChecks = List.of(
                new CellCheck(ReportLabelEnum.USER_DEFINED_PAYMENT_GROUPS_LABEL.getAddress(), FieldValidators.STRING_NONBLANK),
                new CellCheck(ReportLabelEnum.USER_DEFINED_PAYMENT_GROUPS_LABEL.getAddress(), FieldValidators.stringEquals(ReportLabelEnum.USER_DEFINED_PAYMENT_GROUPS_LABEL.getLabel())),

                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_DEFINED_LABEL.getAddress(), FieldValidators.STRING_NONBLANK),
                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_DEFINED_LABEL.getAddress(), FieldValidators.stringEquals(ReportLabelEnum.TOTAL_PAYABLE_DEFINED_LABEL.getLabel())),
                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_DEFINED_VOLUME_OF_OFFICES.getAddress(), FieldValidators.NUMERIC),
                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_DEFINED_TOTAL_VALUE.getAddress(), FieldValidators.CURRENCY_GBP),

                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_NOT_DEFINED_LABEL.getAddress(), FieldValidators.STRING_NONBLANK),
                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_NOT_DEFINED_LABEL.getAddress(), FieldValidators.stringEquals(ReportLabelEnum.TOTAL_PAYABLE_NOT_DEFINED_LABEL.getLabel())),
                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_NOT_DEFINED_VOLUME_OF_OFFICES.getAddress(), FieldValidators.NUMERIC),
                new CellCheck(ReportLabelEnum.TOTAL_PAYABLE_NOT_DEFINED_TOTAL_VALUE.getAddress(), FieldValidators.CURRENCY_GBP),

                new CellCheck(ReportLabelEnum.TOTAL_UNPAID_ON_HOLD_LABEL.getAddress(), FieldValidators.STRING_NONBLANK),
                new CellCheck(ReportLabelEnum.TOTAL_UNPAID_ON_HOLD_LABEL.getAddress(), FieldValidators.stringEquals(ReportLabelEnum.TOTAL_UNPAID_ON_HOLD_LABEL.getLabel())),
                new CellCheck(ReportLabelEnum.TOTAL_UNPAID_ON_HOLD_VOLUME_OF_OFFICES.getAddress(), FieldValidators.NUMERIC),
                new CellCheck(ReportLabelEnum.TOTAL_UNPAID_ON_HOLD_TOTAL_VALUE.getAddress(), FieldValidators.CURRENCY_GBP)
        );
        assertThat(sheet).isNotNull();
        WorkbookAssert.assertThat(workbook).validateRequiredCells(sheet, requiredCellChecks);
    }
}