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
package com.sop4j.dbutils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Executes SQL queries with pluggable strategies for handling
 * <code>ResultSet</code>s.  This class is thread safe.
 *
 * @see ResultSetHandler
 */
public class QueryRunner {
    /**
     * The DataSource to retrieve connections from.
     */
    private final DataSource ds;

    /**
     * Constructor for QueryRunner.
     */
    public QueryRunner() {
        ds = null;
    }

    /**
     * Constructor for QueryRunner that takes a <code>DataSource</code> to use.
     *
     * Methods that do not take a <code>Connection</code> parameter will retrieve connections from this
     * <code>DataSource</code>.
     *
     * @param ds The <code>DataSource</code> to retrieve connections from.
     */
    public QueryRunner(final DataSource ds) {
        this.ds = ds;
    }

    /**
     * Returns the <code>DataSource</code> this runner is using.
     * <code>QueryRunner</code> methods always call this method to get the
     * <code>DataSource</code> so subclasses can provide specialized behavior.
     *
     * @return DataSource the runner is using
     */
    public DataSource getDataSource() {
        return this.ds;
    }

    /**
     * Factory method that creates and initializes a <code>Connection</code>
     * object. <code>QueryRunner</code> methods always call this method to
     * retrieve connections from its DataSource. Subclasses can override this
     * method to provide special <code>Connection</code> configuration if
     * needed. This implementation simply calls <code>ds.getConnection()</code>.
     *
     * @return An initialized <code>Connection</code>.
     * @throws SQLException if a database access error occurs
     */
    protected Connection prepareConnection() throws SQLException {
        if (this.getDataSource() == null) {
            throw new SQLException(
                    "QueryRunner requires a DataSource to be "
                            + "invoked in this way, or a Connection should be passed in");
        }
        return this.getDataSource().getConnection();
    }

    /**
     * Close a <code>Connection</code>. This implementation avoids closing if
     * null and does <strong>not</strong> suppress any exceptions. Subclasses
     * can override to provide special handling like logging.
     *
     * @param conn Connection to close
     * @throws SQLException if a database access error occurs
     */
    private void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(String sql) throws SQLException {
        return this.batch(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the batch call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(Connection conn, String sql) throws SQLException {
        return this.batch(conn, true, sql);
    }

    /**
     * Creates an {@link BatchExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the batch call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link BatchExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public BatchExecutor batch(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new BatchExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return A {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(String sql) throws SQLException {
        return this.query(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the update call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(Connection conn, String sql) throws SQLException {
        return this.query(conn, false, sql);
    }

    /**
     * Creates an {@link QueryExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the query call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link QueryExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public QueryExecutor query(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new QueryExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException if a database access error occurs
     */
    public UpdateExecutor update(String sql) throws SQLException {
        return this.update(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL statement and connection.
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the update call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public UpdateExecutor update(Connection conn, String sql) throws SQLException {
        return this.update(conn, false, sql);
    }

    /**
     * Creates an {@link UpdateExecutor} for the given SQL statement and connection.
     *
     * @param conn The connection to use for the update call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link UpdateExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public UpdateExecutor update(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new UpdateExecutor(conn, sql, closeConn);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL.
     * <code>Connection</code> is retrieved from the <code>DataSource</code>
     * set in the constructor.  This <code>Connection</code> must be in
     * auto-commit mode or the insert will not be saved. The <code>Connection</code> is
     * closed after the call.
     *
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(String sql) throws SQLException {
        return insert(this.prepareConnection(), true, sql);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL and connection
     * The connection is <b>NOT</b> closed after execution.
     *
     * @param conn The connection to use for the query call.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(Connection conn, String sql) throws SQLException {
        return insert(conn, false, sql);
    }

    /**
     * Creates an {@link InsertExecutor} for the given SQL and connection.
     *
     * @param conn The connection to use for the insert call.
     * @param closeConn True if the connection should be closed, false otherwise.
     * @param sql The SQL statement to execute.
     *
     * @return An {@link InsertExecutor} for this SQL statement.
     * @throws SQLException If there are database or parameter errors.
     */
    public InsertExecutor insert(Connection conn, boolean closeConn, String sql) throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }

        if (sql == null) {
            if (closeConn) {
                close(conn);
            }
            throw new SQLException("Null SQL statement");
        }

        return new InsertExecutor(conn, sql, closeConn);
    }

    //
    // Entity methods
    //

    /**
     * Creates a new entity in the database by calling insert.
     * @param entity the entity to insert.
     * @throws SQLException if there is a problem inserting the entity.
     */
    public <T> void create(final T entity) throws SQLException {
        final String tableName = EntityUtils.getTableName(entity.getClass());
        final Map<String, String> columns = EntityUtils.getColumnNames(entity.getClass());

        final StringBuilder sb = new StringBuilder("insert into ");

        // create the SQL command
        sb.append(tableName);
        sb.append(" (");
        sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), null));
        sb.append(") values(");
        sb.append(EntityUtils.joinColumnsWithComma(columns.keySet(), ":"));
        sb.append(")");

        // create the executor
        final InsertExecutor exec = new InsertExecutor(this.prepareConnection(), sb.toString(), true);

        for(String column:columns.keySet()) {
            try {
                // bind all of the values
                final Object value = PropertyUtils.getSimpleProperty(entity, columns.get(column));

                if(value == null) {
                    exec.bindNull(column);
                } else {
                    exec.bind(column, value);
                }
            } catch (final IllegalAccessException e) {
                throw new SQLException(e);
            } catch (final InvocationTargetException e) {
                throw new SQLException(e);
            } catch (final NoSuchMethodException e) {
                throw new SQLException(e);
            }
        }

        // execute the insert
        exec.execute();
    }

    /**
     * Constructs an entity ReadEntityExecutor used to read entities.
     * @param entity an entity marked with the {@link Entity} annotation.
     * @return a {@link ReadEntityExecutor} used to read entities.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> ReadEntityExecutor<T> read(final Class<T> entity) throws SQLException {
        return new ReadEntityExecutor<T>(entity, this.prepareConnection());
    }

    /**
     * Constructs an {@link UpdateEntityExecutor} used to update entities.
     * @param entity an entity marked with the {@link Entity} annotation.
     * @return a {@link UpdateEntityExecutor} used to update entities.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> UpdateEntityExecutor<T> update(final T entity) throws SQLException {
        return new UpdateEntityExecutor<T>(entity, this.prepareConnection());
    }

    /**
     * Constructs an {@link DeleteEntityExecutor} used to delete entities.
     * @param entity an entity marked with the {@link Entity} annotation.
     * @return a {@link DeleteEntityExecutor} used to delete entities.
     * @throws SQLException If there are database or parameter errors.
     */
    public <T> DeleteEntityExecutor<T> delete(final Class<T> entity) throws SQLException {
        return new DeleteEntityExecutor<T>(entity, this.prepareConnection());
    }
}
