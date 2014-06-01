/**
 *
 */
package com.sop4j.dbutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sop4j.dbutils.handlers.BeanHandler;

/**
 * An EntityExecutor that is used to read entities.
 */
public class ReadEntityExecutor<T> extends AbstractEntityExecutor<ReadEntityExecutor<T>> {
    private static final Logger LOG = LoggerFactory.getLogger(ReadEntityExecutor.class);

    private final Set<String> excludeColumns;

    /**
     * Constructor.
     *
     * @param entity the entity to read.
     * @param conn the connection to use.
     */
    ReadEntityExecutor(final Class<T> entity, final Connection conn) {
        this(entity, conn, new HashSet<String>());
    }

    /**
     * Constructor that excludes columns during binding.
     *
     * @param entity the entity to read.
     * @param conn the connection to use.
     */
    ReadEntityExecutor(final Class<T> entity, final Connection conn, final Set<String> excludeColumns) {
        super(entity, conn);

        this.excludeColumns = excludeColumns;
    }

    /**
     * Reads the entity from the database.
     * @return the entity read from the database.
     * @throws SQLException thrown if any errors occur during reading.
     */
    public T read() throws SQLException {
        final StringBuilder sb = new StringBuilder("select * from ");

        sb.append(tableName);

        if(!params.isEmpty()) {
            sb.append(" where ");

            sb.append(EntityUtils.joinColumnsEquals(params.keySet(), " and "));
        }

        LOG.debug("SELECT: {}", sb.toString());

        // setup the QueryExecutor
        final QueryExecutor exec = new QueryExecutor(conn, sb.toString(), true);

        // go through and bind all the params
        for(Map.Entry<String, Object> entity:params.entrySet()) {
            if(excludeColumns.contains(entity.getKey())) {
                continue;
            }

            exec.bind(entity.getKey(), entity.getValue());
        }

        // execute using the BeanHandler
        return exec.execute(new BeanHandler<T>((Class<T>) entity));
    }
}
