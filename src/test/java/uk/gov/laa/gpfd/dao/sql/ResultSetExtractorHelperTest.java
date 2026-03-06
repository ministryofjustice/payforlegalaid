package uk.gov.laa.gpfd.dao.sql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultSetExtractorHelperTest {

    @Mock
    ResultSet mockResultSet;
    @Mock
    RowCallbackHandler mockRowCallbackHandler;

    ResultSetExtractorHelper<Void> resultSetExtractorHelper;

    @BeforeEach
    void beforeEach() {
        resultSetExtractorHelper = new ResultSetExtractorHelper<>(mockRowCallbackHandler);
        reset(mockResultSet, mockRowCallbackHandler);
    }

    @Test
    void shouldProcessAllRows() throws Exception {
        when(mockResultSet.next()).thenReturn(true, true, true, false);

        resultSetExtractorHelper.extractData(mockResultSet);

        verify(mockRowCallbackHandler, times(3)).processRow(mockResultSet);
        verifyNoMoreInteractions(mockRowCallbackHandler);

        InOrder inOrder = inOrder(mockResultSet, mockRowCallbackHandler);
        inOrder.verify(mockResultSet).next();
        inOrder.verify(mockRowCallbackHandler).processRow(mockResultSet);
        inOrder.verify(mockResultSet).next();
        inOrder.verify(mockRowCallbackHandler).processRow(mockResultSet);
        inOrder.verify(mockResultSet).next();
        inOrder.verify(mockRowCallbackHandler).processRow(mockResultSet);
        inOrder.verify(mockResultSet).next();
    }

    @Test
    void shouldDoNothingIfEmptyResultSet() throws Exception {
        when(mockResultSet.next()).thenReturn(false);

        resultSetExtractorHelper.extractData(mockResultSet);

        verify(mockRowCallbackHandler, never()).processRow(any());
        verify(mockResultSet, times(1)).next();
    }

    @Test
    void shouldPropagateSqlExceptionFromResultSetNext() throws Exception {
        when(mockResultSet.next()).thenThrow(new SQLException("result set error"));

        assertThrows(SQLException.class, () -> resultSetExtractorHelper.extractData(mockResultSet));
        verify(mockRowCallbackHandler, never()).processRow(any());
    }

    @Test
    void shouldPropagateSqlExceptionFromRowCallbackHandler() throws Exception {
        when(mockResultSet.next()).thenReturn(true);
        doThrow(new SQLException("row callback handler error")).when(mockRowCallbackHandler).processRow(mockResultSet);

        assertThrows(SQLException.class, () -> resultSetExtractorHelper.extractData(mockResultSet));

        verify(mockResultSet).next();
        verify(mockRowCallbackHandler, times(1)).processRow(mockResultSet);
    }

}