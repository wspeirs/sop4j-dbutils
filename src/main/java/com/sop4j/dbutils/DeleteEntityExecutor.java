/**
 *
 */
package com.sop4j.dbutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An EntityExecutor that is used to read entities.
 */
public class DeleteEntityExecutor<T> extends AbstractEntityExecutor<DeleteEntityExecutor<T>> {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteEntityExecutor.class);

    /**
     * Constructor.
     *
     * @param entity the entity to read.
     * @param conn the connection to use.
     */
    DeleteEntityExecutor(final Class<T> entity, final Connection conn) {
        super(entity, conn);
    }

    /**
     * Reads the entity from the database.
     * @return the entity read from the database.
     * @throws SQLException thrown if any errors occur during reading.
     */
    public int delete() throws SQLException {
        final StringBuilder sb = new StringBuilder("delete from ");

        sb.append(tableName);

        if(!params.isEmpty()) {
            sb.append(" where ");

            sb.append(EntityUtils.joinColumnsEquals(params.keySet(), " and "));
        } else {
            throw new IllegalArgumentException("No parameters were bound, so this would delete the whole table.");
        }

        LOG.debug("DELETE: {}", sb.toString());

        // setup the QueryExecutor
        final UpdateExecutor exec = new UpdateExecutor(conn, sb.toString(), true);

        // go through and bind all the params
        for(Map.Entry<String, Object> entity:params.entrySet()) {
            exec.bind(entity.getKey(), entity.getValue());
        }

        // execute using the BeanHandler
        return exec.execute();
    }
}
