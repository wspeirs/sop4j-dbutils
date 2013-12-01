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

import com.sop4j.dbutils.BaseTestCase;
import com.sop4j.dbutils.ResultSetHandler;

/**
 * ArrayHandlerTest
 */
public class ArrayHandlerTest extends BaseTestCase {

    public void testHandle() throws SQLException {
        ResultSetHandler<Object[]> h = new ArrayHandler();
        Object[] results = h.handle(this.rs);

        assertNotNull(results);
        assertEquals(COLS, results.length);
        assertEquals("1", results[0]);
        assertEquals("2", results[1]);
        assertEquals("3", results[2]);
    }

    public void testEmptyResultSetHandle() throws SQLException {
        ResultSetHandler<Object[]> h = new ArrayHandler();
        Object[] results = h.handle(this.emptyResultSet);

        assertNull(results);
    }

}
