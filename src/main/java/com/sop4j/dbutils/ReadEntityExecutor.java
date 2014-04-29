/**
 *
 */
package com.sop4j.dbutils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.sop4j.dbutils.handlers.BeanHandler;

/**
 *
 *
 */
public class ReadEntityExecutor<T> {

    private final Class<T> entity;
    private final Connection conn;
    private final String tableName;

    private final Map<String, Object> params = new HashMap<String, Object>();

    /**
     *
     */
    public ReadEntityExecutor(final Class<T> entity, final Connection conn) {
        this.entity = entity;
        this.conn = conn;

        final Entity annotation = entity.getAnnotation(Entity.class);

        if(annotation == null) {
            throw new IllegalArgumentException(entity.getName() + " does not have the Entity annotation");
        }

        // get the table's name from the annotation
        if(annotation.name().length() == 0) {
            tableName = entity.getName();
        } else {
            tableName = annotation.name();
        }

    }

    public ReadEntityExecutor<T> bind(final String property, final Object value) {
        if(property == null || property.length() == 0) {
            throw new IllegalArgumentException("Property is null or blank string");
        }

        boolean found = false;

        for(Field field:entity.getDeclaredFields()) {
            // check to see if the property is a field
            if(field.getName().equals(property)) {
                params.put(property, value);
                found = true;
                break;
            } else {
                // we also check the column name
                final Column column = field.getAnnotation(Column.class);

                if(column != null && column.name().equals(property)) {
                    params.put(property, value);
                    found = true;
                    break;
                }
            }
        }

        if(!found) {
            throw new IllegalArgumentException(property + " is not a property of the entity " + entity.getName());
        }

        return this;
    }

    public T read() throws SQLException {
        final StringBuilder sb = new StringBuilder("select * from ");

        sb.append(tableName);

        if(!params.isEmpty()) {
            sb.append(" where ");

            for(String property:params.keySet()) {
                sb.append(property);
                sb.append(" = :");
                sb.append(property);
                sb.append(" and ");
            }

            // remove the last and
            sb.delete(sb.length() - " and ".length(), sb.length());
        }

        // setup the QueryExecutor
        final QueryExecutor exec = new QueryExecutor(conn, sb.toString(), true);

        // go through and bind all the params
        for(Map.Entry<String, Object> entity:params.entrySet()) {
            exec.bind(entity.getKey(), entity.getValue());
        }

        // execute using the BeanHandler
        return exec.execute(new BeanHandler<T>(entity));
    }
}
