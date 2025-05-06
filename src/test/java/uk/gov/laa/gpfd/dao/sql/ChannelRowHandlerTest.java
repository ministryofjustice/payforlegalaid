package uk.gov.laa.gpfd.dao.sql;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
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

    @Test
    void shouldReturnNonNullHandler() {
        ChannelRowHandler handler = forStream(new ByteArrayOutputStream());
        assertNotNull(handler);
        assertInstanceOf(StreamChannelRowHandler.class, handler);
    }

    @Test
    void shouldThrowNullPointerExceptionForNullStream() {
        assertThrows(NullPointerException.class, () -> forStream(null));
    }

    @Test
    @SneakyThrows
    void shouldWriteHeaderAndData() {
        var handler = new StreamChannelRowHandler(stream);
        var rowWriter = mock(RowWriter.StreamRowWriter.class);

        setField(handler, "rowWriter", rowWriter);

        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(3);

        handler.processRow(resultSet);
        verify(rowWriter, times(2)).writeRow(any(), eq(3));

        handler.processRow(resultSet);
        verify(rowWriter, times(3)).writeRow(any(), eq(3));
    }

    @Test
    @SneakyThrows
    void shouldCloseUnderlyingStream() {
        var handler = new StreamChannelRowHandler(stream);

        handler.close();
        verify(stream).close();
    }

    @Test
    @SneakyThrows
    void shouldPropagateIOException() {
        doThrow(new IOException("Test error")).when(stream).close();

        var handler = new StreamChannelRowHandler(stream);
        assertThrows(IOException.class, handler::close);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}