/**
 *
 */
package com.sop4j.dbutils;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An EntityExecutor that is used to update entities.
 */
public class UpdateEntityExecutor<T> extends AbstractEntityExecutor<UpdateEntityExecutor<T>> {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateEntityExecutor.class);

    private final T entityInstance;
    private final Set<String> excludeColumns;

    /**
     * Constructor.
     *
     * @param entityInstance the entity instance to update.
     * @param conn the connection to use.
     */
    UpdateEntityExecutor(final T entityInstance, final Connection conn) {
        this(entityInstance, conn, new HashSet<String>());
    }

    /**
     * Constructor that takes a list of columns to exclude during the bind.
     *
     * @param entityInstance the entity instance to update.
     * @param conn the connection to use.
     * @param excludeColumns the columns to exclude during the bind.
     */
    UpdateEntityExecutor(final T entityInstance, final Connection conn, final Set<String> excludeColumns) {
        super(entityInstance.getClass(), conn);

        this.entityInstance = entityInstance;
        this.excludeColumns = excludeColumns;
    }

    /**
     * Updates an entity(ies) in the database.
     * @return the number of rows updated.
     * @throws SQLException thrown if any errors occur during updating.
     */
    public int update(final Class<? extends T> entityClass) throws SQLException {
        final Map<String, String> columns = EntityUtils.getColumnNames(entityClass);

        final StringBuilder sb = new StringBuilder("update ");

        // create the SQL command
        sb.append(tableName);
        sb.append(" set ");
        sb.append(EntityUtils.joinColumnsEquals(columns.keySet(), ", "));

        if(!params.isEmpty()) {
            sb.append(" where ");

            sb.append(EntityUtils.joinColumnsEquals(params.keySet(), " and "));
        }

        LOG.debug("UPDATE: {}", sb.toString());

        // setup the QueryExecutor
        final UpdateExecutor exec = new UpdateExecutor(conn, sb.toString(), true);

        // bind all the column values
        for(String column:columns.keySet()) {
            // skip anything in the exclude set
            if(excludeColumns.contains(column)) {
                continue;
            }

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
