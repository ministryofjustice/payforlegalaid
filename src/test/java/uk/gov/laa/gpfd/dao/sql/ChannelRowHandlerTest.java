package uk.gov.laa.gpfd.dao.sql;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SequenceWriter;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;
import uk.gov.laa.gpfd.exception.CsvGenerationException;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.laa.gpfd.dao.sql.ChannelRowHandler.StreamChannelRowHandler;
import static uk.gov.laa.gpfd.dao.sql.ChannelRowHandler.forStream;

@ExtendWith(MockitoExtension.class)
class ChannelRowHandlerTest {

    @Mock
    private OutputStream stream;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    @Mock
    private CsvMapper csvMapper;

    @Mock
    private SequenceWriter sequenceWriter;

    @Mock
    private ObjectWriter objectWriter;

    private final Map<String, String> expectedDataRow = new LinkedHashMap<>();
    private Map<String, String> row;
    private StreamChannelRowHandler handler;

    @BeforeEach
    void setup() {
        reset(sequenceWriter, metaData);
        row = new LinkedHashMap<>();
        handler = new StreamChannelRowHandler(stream, csvMapper, row, 1000);

        for (int i = 1; i <= 10; i++) {
            expectedDataRow.put("column_" + i, "data");
        }
    }

    @Test
    void shouldReturnNonNullHandler() {
        ChannelRowHandler handlerToTest = forStream(stream, csvMapper, row, 1000);
        assertNotNull(handlerToTest);
        assertInstanceOf(StreamChannelRowHandler.class, handlerToTest);
    }

    @Test
    void shouldThrowNullPointerExceptionForNullStream() {
        assertThrows(NullPointerException.class, () -> forStream(null, csvMapper, row, 1000));
    }

    @Test
    @SneakyThrows
    void willSetupHeaderRowFirstTimeAround() {
        setupResultSetData(1, "data");

        when(csvMapper.writer(any(CsvSchema.class))).thenReturn(objectWriter);
        when(objectWriter.writeValues(stream)).thenReturn(sequenceWriter);

        handler.processRow(resultSet);
        verify(sequenceWriter).write(expectedDataRow);
    }

    @SneakyThrows
    @Test
    void buildsOutputForSubsequentRowsWithoutRebuildingSequenceWriter() {
        buildFirstRowAndSchema(handler);

        var secondRowMap = new LinkedHashMap<String, String>();
        setupResultSetData(10, "second_data_row");
        for (int i = 1; i <= 10; i++) {
            secondRowMap.put("column_" + i, "second_data_row");
        }
        handler.processRow(resultSet);
        verify(sequenceWriter, times(1)).write(secondRowMap);
        verify(csvMapper, times(0)).writer();

    }

    @SneakyThrows
    @Test
    void willNotFlushBufferIfDataSizeIsSmallerThanBufferFlushValue() {
        setupResultSetData(1, "data");

        when(csvMapper.writer(any(CsvSchema.class))).thenReturn(objectWriter);
        when(objectWriter.writeValues(stream)).thenReturn(sequenceWriter);

        handler.processRow(resultSet);
        verify(sequenceWriter, times(0)).flush();
    }

    @SneakyThrows
    @Test
    void willFlushWhenRowNumberEqualsFlushSize() {
        var testHandler = new StreamChannelRowHandler(stream, csvMapper, row, 1);
        setupResultSetData(1, "data");

        when(csvMapper.writer(any(CsvSchema.class))).thenReturn(objectWriter);
        when(objectWriter.writeValues(stream)).thenReturn(sequenceWriter);

        testHandler.processRow(resultSet);
        verify(sequenceWriter, times(1)).flush();
    }

    @SneakyThrows
    @Test
    void willHandleCommaInData()  {
        var expectedResultWithComma = new LinkedHashMap<String, String>();
        setupResultSetData(1, "Data, Mrs. S");
        for (int i = 1; i <= 10; i++) {
            expectedResultWithComma.put("column_" + i, "Data, Mrs. S");
        }

        when(csvMapper.writer(any(CsvSchema.class))).thenReturn(objectWriter);
        when(objectWriter.writeValues(stream)).thenReturn(sequenceWriter);

        handler.processRow(resultSet);
        verify(sequenceWriter).write(expectedResultWithComma);
    }

    @Test
    @SneakyThrows
    void shouldCloseUnderlyingStream() {
        handler.close();
        verify(stream).close();
    }

    @Test
    @SneakyThrows
    void shouldPropagateIOException() {
        doThrow(new IOException("Test error")).when(stream).close();
        assertThrows(IOException.class, handler::close);
    }


    @Test
    @SneakyThrows
    void willThrowIfMetadataIsNull() {
        when(resultSet.getMetaData()).thenReturn(null);
        assertThrows(CsvGenerationException.MetadataInvalidException.class, () -> handler.processRow(resultSet));
    }

    @Test
    @SneakyThrows
    void willThrowCsvCreationExceptionIfWriterThrows() {
        setupResultSetData(1, "data", true);

        when(csvMapper.writer(any(CsvSchema.class))).thenReturn(objectWriter);
        when(objectWriter.writeValues(stream)).thenReturn(sequenceWriter);
        when(sequenceWriter.write(any())).thenThrow(IOException.class);
        assertThrows(CsvGenerationException.WritingToCsvException.class, () -> handler.processRow(resultSet));
    }

    @Test
    void willThrowSqlExceptionUpToJdbcLevel() throws SQLException {
        when(resultSet.getMetaData()).thenThrow(SQLException.class);
        assertThrows(SQLException.class, () -> handler.processRow(resultSet));
    }

    private void setupResultSetData(int rowNo, String data) throws SQLException {
        setupResultSetData(rowNo, data, false);
    }

    private void setupResultSetData(int rowNo, String data, boolean skipGetRow) throws SQLException {
        when(resultSet.getMetaData()).thenReturn(metaData);
        if (!skipGetRow) {
            when(resultSet.getRow()).thenReturn(rowNo);
        }
        when(resultSet.getString(anyInt())).thenReturn(data);
        when(metaData.getColumnCount()).thenReturn(10);
        for (int i = 1; i <= 10; i++) {
            when(metaData.getColumnName(i)).thenReturn("column_" + i);
        }
    }

    // To process subsequent rows you need to process 1st row so it builds the headers etc
    @SneakyThrows
    private void buildFirstRowAndSchema(StreamChannelRowHandler handler) {
        setupResultSetData(1, "data");
        when(csvMapper.writer(any(CsvSchema.class))).thenReturn(objectWriter);
        when(objectWriter.writeValues(stream)).thenReturn(sequenceWriter);

        handler.processRow(resultSet);

        // Resets verify counters
        reset(sequenceWriter);
    }

}