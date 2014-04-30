/**
 *
 */
package com.sop4j.dbutils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.sop4j.dbutils.handlers.BeanHandler;

/**
 * An EntityExecutor that is used to read entities.
 */
public class ReadEntityExecutor<T> extends AbstractEntityExecutor<ReadEntityExecutor<T>> {

    /**
     * Constructor.
     *
     * @param entity the entity to read.
     * @param conn the connection to use.
     */
    ReadEntityExecutor(final Class<T> entity, final Connection conn) {
        super(entity, conn);
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

        // setup the QueryExecutor
        final QueryExecutor exec = new QueryExecutor(conn, sb.toString(), true);

        // go through and bind all the params
        for(Map.Entry<String, Object> entity:params.entrySet()) {
            exec.bind(entity.getKey(), entity.getValue());
        }

        // execute using the BeanHandler
        return exec.execute(new BeanHandler<T>((Class<T>) entity));
    }
}
