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
abstract class AbstractEntityExecutor<T> {

    protected final Class<T> entity;
    protected final Connection conn;
    protected final String tableName;
    protected final Map<String, Object> params = new HashMap<String, Object>();

    /**
     * Constructs the EntityExecutor.
     */
    public AbstractEntityExecutor(final Class<T> entity, final Connection conn) {
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

    /**
     * Bind properties to values before execution.
     * @param property the property to bind.
     * @param value the value to bind.
     * @return this.
     */
    public AbstractEntityExecutor<T> bind(final String property, final Object value) {
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
}
