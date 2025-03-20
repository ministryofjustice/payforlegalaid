package uk.gov.laa.gpfd.services.excel.editor;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPivotCacheDefinition;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class PivotTableRefresherTest implements PivotTableRefresher {

    @Test
    void shouldRefreshPivotTablesGivenValidWorkbook() {
        // Given
        var workbook = mock(XSSFWorkbook.class);
        var cacheDefinition = new XSSFPivotCacheDefinition();
        var packagePart = mock(POIXMLDocumentPart.RelationPart.class);
        var relationship = mock(PackageRelationship.class);

        when(workbook.getRelationParts()).thenReturn(List.of(packagePart));
        when(packagePart.getRelationship()).thenReturn(relationship);
        when(relationship.getRelationshipType()).thenReturn("pivotCacheDefinition");
        when(workbook.getRelationById(relationship.getId())).thenReturn(cacheDefinition);

        // When
        refreshPivotTables(workbook);

        // Then
        assertTrue(cacheDefinition.getCTPivotCacheDefinition().getRefreshOnLoad());
    }

    @Test
    void shouldRefreshPivotTablesGivenNullWorkbook() {
        // When
        var exception = assertThrows(IllegalArgumentException.class, () -> refreshPivotTables(null));

        // Then
        assertEquals("Workbook cannot be null", exception.getMessage());
    }

    @Test
    void shouldRefreshPivotTablesGivenNonXSSFWorkbook() {
        // Given
        var workbook = mock(Workbook.class);

        // Then
        refreshPivotTables(workbook);

        // Then
        verifyNoInteractions(workbook);
    }

    @Test
    void shouldRefreshPivotTablesGivenNoPivotCacheDefinitions() {
        // Given
        var workbook = mock(XSSFWorkbook.class);
        when(workbook.getRelationParts()).thenReturn(Collections.emptyList());

        // Then
        refreshPivotTables(workbook);

        // Then
        verify(workbook).getRelationParts();
        verifyNoMoreInteractions(workbook);
    }

    @Test
    void shouldRefreshPivotTablesGivenMultiplePivotCacheDefinitions() {
        // Given
        var workbook = mock(XSSFWorkbook.class);
        var cacheDefinition1 = new XSSFPivotCacheDefinition();
        var cacheDefinition2 = new XSSFPivotCacheDefinition();
        var packagePart1 = mock(POIXMLDocumentPart.RelationPart.class);
        var packagePart2 = mock(POIXMLDocumentPart.RelationPart.class);
        var relationship1 = mock(PackageRelationship.class);
        var relationship2 = mock(PackageRelationship.class);

        when(workbook.getRelationParts()).thenReturn(List.of(packagePart1, packagePart2));
        when(packagePart1.getRelationship()).thenReturn(relationship1);
        when(packagePart2.getRelationship()).thenReturn(relationship2);
        when(relationship1.getRelationshipType()).thenReturn("pivotCacheDefinition");
        when(relationship2.getRelationshipType()).thenReturn("pivotCacheDefinition");
        when(workbook.getRelationById(relationship1.getId())).thenReturn(cacheDefinition1);
        when(workbook.getRelationById(relationship2.getId())).thenReturn(cacheDefinition2);

        // Then
        refreshPivotTables(workbook);

        // Then
        assertTrue(cacheDefinition1.getCTPivotCacheDefinition().getRefreshOnLoad());
        assertTrue(cacheDefinition2.getCTPivotCacheDefinition().getRefreshOnLoad());
    }
}

