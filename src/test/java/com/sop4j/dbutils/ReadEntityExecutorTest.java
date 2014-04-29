package com.sop4j.dbutils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.persistence.Entity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReadEntityExecutorTest {

    @Mock private Connection conn;
    @Mock private PreparedStatement stmt;
    @Mock private ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(conn.prepareStatement(any(String.class))).thenReturn(stmt);
        when(stmt.getGeneratedKeys()).thenReturn(resultSet);
        when(stmt.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
    }

    @Test
    public void testReadEntityExecutor() throws Exception {
        ReadEntityExecutor<TestClass> exec = new ReadEntityExecutor<TestClass>(TestClass.class, conn);

        exec.bind("param1", "something").read();

        verify(conn, times(1)).close();
        verify(stmt, times(1)).close();
    }

    @Entity
    public static class TestClass {
        private String param1;
    }
}
