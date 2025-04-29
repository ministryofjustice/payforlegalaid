package uk.gov.laa.gpfd.dao.sql;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

class RowWriterTest {

    private ByteArrayOutputStream testOutputStream;
    private RowWriter rowWriter;
    private ValueExtractor mockExtractor;

    @BeforeEach
    void setUp() {
        testOutputStream = new ByteArrayOutputStream();
        rowWriter = RowWriter.forStream(testOutputStream);
        mockExtractor = mock(ValueExtractor.HeaderValueExtractor.class);
    }

    @Test
    void shouldCreatesNonNullInstance() {
        assertNotNull(RowWriter.forStream(testOutputStream));
    }

    @Test
    void shouldThrowsNpeForNullStream() {
        assertThrows(NullPointerException.class, () -> RowWriter.forStream(null));
    }

    @Test
    @SneakyThrows
    void shouldWriteSingleColumn() {
        when(mockExtractor.extract(1)).thenReturn("value1");

        rowWriter.writeRow(mockExtractor, 1);

        assertEquals("value1\n", testOutputStream.toString());
    }

    @Test
    @SneakyThrows
    void shouldWriteMultipleColumns() {
        when(mockExtractor.extract(1)).thenReturn("val1");
        when(mockExtractor.extract(2)).thenReturn("val2");
        when(mockExtractor.extract(3)).thenReturn("val3");

        rowWriter.writeRow(mockExtractor, 3);
        assertEquals("val1,val2,val3\n", testOutputStream.toString());
    }

    @Test
    @SneakyThrows
    void shouldWriteEmptyValues() {
        when(mockExtractor.extract(1)).thenReturn("");
        when(mockExtractor.extract(2)).thenReturn("value");

        rowWriter.writeRow(mockExtractor, 2);

        assertEquals(",value\n", testOutputStream.toString());
    }

    @Test
    @SneakyThrows
    void shouldThrowsIOExceptionOnStreamError() {
        var failingStream = mock(OutputStream.class);
        doThrow(new IOException("Stream error")).when(failingStream).write(any());

        var failingWriter = RowWriter.forStream(failingStream);
        when(mockExtractor.extract(1)).thenReturn("value");

        assertThrows(IOException.class, () -> failingWriter.writeRow(mockExtractor, 1));
    }

    @Test
    @SneakyThrows
    void shouldConvertsSqlExceptionToIoException() {
        when(mockExtractor.extract(1)).thenThrow(new SQLException("DB error"));

        var thrown = assertThrows(IOException.class, () -> rowWriter.writeRow(mockExtractor, 1));

        assertEquals("Error extracting row data", thrown.getMessage());
        assertInstanceOf(SQLException.class, thrown.getCause());
    }

    @Test
    void writeRow_throwsNpeForNullExtractor() {
        assertThrows(NullPointerException.class, () -> rowWriter.writeRow(null, 1));
    }

    @Test
    @SneakyThrows
    void shouldWriteZeroColumns() {
        rowWriter.writeRow(mockExtractor, 0);

        assertEquals("\n", testOutputStream.toString());
        verify(mockExtractor, never()).extract(anyInt());
    }

    @Test
    @SneakyThrows
    void shouldWriteValuesInCorrectOrder() {
        when(mockExtractor.extract(1)).thenReturn("first");
        when(mockExtractor.extract(2)).thenReturn("second");

        rowWriter.writeRow(mockExtractor, 2);

        assertEquals("first,second\n", testOutputStream.toString());
    }

}