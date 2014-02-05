/*
 * Copyright (C) 2014 SOP4J
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
import com.sop4j.dbutils.TestBean;

/**
 * BeanListHandlerTest
 */
public class BeanListHandlerTest extends BaseTestCase {

    public void testHandle() throws SQLException {
        ResultSetHandler<List<TestBean>> h = new BeanListHandler<TestBean>(TestBean.class);
        List<TestBean> results = h.handle(this.rs);

        assertNotNull(results);
        assertEquals(ROWS, results.size());

        Iterator<TestBean> iter = results.iterator();
        TestBean row = null;
        assertTrue(iter.hasNext());
        row = iter.next();
        assertEquals("1", row.getOne());
        assertEquals("2", row.getTwo());
        assertEquals("3", row.getThree());
        assertEquals("not set", row.getDoNotSet());

        assertTrue(iter.hasNext());
        row = iter.next();

        assertEquals("4", row.getOne());
        assertEquals("5", row.getTwo());
        assertEquals("6", row.getThree());
        assertEquals("not set", row.getDoNotSet());

        assertFalse(iter.hasNext());
    }

    public void testEmptyResultSetHandle() throws SQLException {
        ResultSetHandler<List<TestBean>> h = new BeanListHandler<TestBean>(TestBean.class);
        List<TestBean> results = h.handle(this.emptyResultSet);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

}
