package com.tony.automationserver.sqlhelper;

import java.util.List;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.tony.automationserver.sqlhelper.SchemaHelper.PropertyMap;

public abstract class SQLObject {

    private HashMap<String, Object> map;

    public SQLObject() {
        map = new HashMap<>();
    }

    public SQLObject(HashMap<String, Object> map) throws Exception {
        this.map = map;
        Field f = null;
        for (PropertyMap var : SchemaHelper.getColumns(this.getClass())) {
            String column = var.columnName;
            if (map.containsKey(column)) {
                f = var.field;
                f.setAccessible(true);
                f.set(this, map.get(column));
            }
        }
    }

    Object getPropertyValue(Field field) {
        field.setAccessible(true);
        try {
            return field.get(this);
        } catch (IllegalAccessException | IllegalArgumentException e) {
        }
        return null;
    }

    void setPropertyValue(Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
        field.setAccessible(true);
        field.set(this, value);
    }

    final Object[] getPropertyArray(boolean primary_is_last, boolean combined) {

        List<PropertyMap> properties = combined ? SchemaHelper.getCombinedColumns(this.getClass()) : SchemaHelper.getColumns(this.getClass());

        Object[] pros = new Object[properties.size()];

        PropertyMap key = SchemaHelper.getPrimaryKey(this.getClass());

        int i=0;
        boolean skipped = false;
        for (PropertyMap var : properties) {
            if(primary_is_last && !skipped) {
                if(var.equals(key)) {
                    skipped = true;
                    continue;
                }
            }
            pros[i++] = getPropertyValue(var.field);
        }

        pros[pros.length - 1] = getPropertyValue(key.field);

        return pros;
    }

    void PreInsert(){}

    void PostInsert(){}

    void PreUpdate(){}

    void PostUpdate(){}

    void PreDelete(){}

    void PostDelete(){}

    Object getKeyValue() {
        Field field = SchemaHelper.getPrimaryKey(this.getClass()).field;
        return getPropertyValue(field);
    }

    @Override
    public boolean equals(Object obj) {
        Object myKey = this.getKeyValue();
        if(obj == null)
            return false;
        if(!(obj instanceof SQLObject))
            return obj.equals(getKeyValue());
        if(!this.getClass().equals(obj.getClass()))
            return false;
        
        Object otherKey = ((SQLObject)obj).getKeyValue();
        if(myKey == null || otherKey == null)
            return false;
        return myKey.equals(otherKey);
    }

    Object getMapField(String columnName){
        return map.get(columnName);
    }

}