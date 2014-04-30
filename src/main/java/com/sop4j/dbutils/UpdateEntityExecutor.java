/**
 *
 */
package com.sop4j.dbutils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * An EntityExecutor that is used to update entities.
 */
public class UpdateEntityExecutor<T> extends AbstractEntityExecutor<T> {

    private final T entityInstance;

    /**
     * Constructor.
     *
     * @param entityInstance the entity instance to update.
     * @param conn the connection to use.
     */
    UpdateEntityExecutor(final T entityInstance, final Connection conn) {
        super((Class<T>)entityInstance.getClass(), conn);

        this.entityInstance = entityInstance;
    }

    /**
     * Updates an entity(ies) in the database.
     * @return the number of rows updated.
     * @throws SQLException thrown if any errors occur during updating.
     */
    public int update() throws SQLException {
        final Map<String, String> columns = EntityUtils.getColumnNames(entity.getClass());

        final StringBuilder sb = new StringBuilder("update ");

        // create the SQL command
        sb.append(tableName);
        sb.append(" set ");
        sb.append(EntityUtils.joinColumnsEquals(columns.keySet(), ", "));

        if(!params.isEmpty()) {
            sb.append(" where ");

            sb.append(EntityUtils.joinColumnsEquals(params.keySet(), " and "));
        }

        // setup the QueryExecutor
        final UpdateExecutor exec = new UpdateExecutor(conn, sb.toString(), true);

        // bind all the column values
        for(String column:columns.keySet()) {
            try {
                // bind all of the values
                final Object value = PropertyUtils.getSimpleProperty(entityInstance, columns.get(column));

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

        // go through and bind all the params
        for(Map.Entry<String, Object> entity:params.entrySet()) {
            exec.bind(entity.getKey(), entity.getValue());
        }

        // execute using the BeanHandler
        return exec.execute();
    }
}
