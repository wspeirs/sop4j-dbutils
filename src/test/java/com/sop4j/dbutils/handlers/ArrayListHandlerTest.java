/*
 * Copyright (C) 2013 SOP4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sop4j.dbutils.handlers;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.sop4j.dbutils.BaseTestCase;
import com.sop4j.dbutils.ResultSetHandler;

/**
 * ArrayListHandlerTest
 */
public class ArrayListHandlerTest extends BaseTestCase {

    public void testHandle() throws SQLException {
        ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        List<Object[]> results = h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Iterator<Object[]> iter = results.iterator();
        Object[] row = null;
        assertTrue(iter.hasNext());
        row = iter.next();
        assertEquals(COLS, row.length);
        assertEquals("1", row[0]);
        assertEquals("2", row[1]);
        assertEquals("3", row[2]);

        assertTrue(iter.hasNext());
        row = iter.next();
        assertEquals(COLS, row.length);

        assertEquals("4", row[0]);
        assertEquals("5", row[1]);
        assertEquals("6", row[2]);

        assertFalse(iter.hasNext());
    }

    public void testEmptyResultSetHandle() throws SQLException {
        ResultSetHandler<List<Object[]>> h = new ArrayListHandler();
        List<Object[]> results = h.handle(this.emptyResultSet);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

}
