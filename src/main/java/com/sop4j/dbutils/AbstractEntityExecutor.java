/**
 *
 */
package com.sop4j.dbutils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * An abstract class that makes it easier to build EntityExecutors.
 */
public abstract class AbstractEntityExecutor<T extends AbstractEntityExecutor<T>> {

    protected final Class<?> entity;
    protected final Connection conn;
    protected final String tableName;
    protected final Map<String, Object> params = new HashMap<String, Object>();

    /**
     * Constructs the EntityExecutor.
     */
    AbstractEntityExecutor(final Class<?> entity, final Connection conn) {
        this.entity = entity;
        this.conn = conn;

        final Entity annotation = entity.getAnnotation(Entity.class);

        if(annotation == null) {
            throw new IllegalArgumentException(entity.getName() + " does not have the Entity annotation");
        }

        // get the table's name
        tableName = EntityUtils.getTableName(entity);
    }

    /**
     * Bind properties to values before execution.
     * @param property the property to bind.
     * @param value the value to bind.
     * @return this.
     */
    public T bind(final String property, final Object value) {
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

        return (T)this;
    }
}
