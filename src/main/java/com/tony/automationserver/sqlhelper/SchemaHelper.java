package com.tony.automationserver.sqlhelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;
import com.tony.automationserver.sqlhelper.annotation.ManyToMany;
import com.tony.automationserver.sqlhelper.annotation.ManyToOne;
import com.tony.automationserver.sqlhelper.annotation.OneToMany;
import com.tony.automationserver.sqlhelper.annotation.PrimaryKey;
import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;
import com.tony.automationserver.sqlhelper.exception.PrimaryKeyNotFoundException;
import com.tony.automationserver.sqlhelper.exception.TableNameNotSetException;

public class SchemaHelper {

    public static class PropertyMap {
        public final String fieldName;
        public final String columnName;
        public final String inversedColumnName;
        public final SQLTypes type;
        public final boolean isPrimary;
        public final String tableName;
        public final Class<? extends SQLObject> clazz;

        public PropertyMap(String fieldName, String columnName, SQLTypes type, boolean isPrimary){
            this.fieldName = fieldName;
            this.columnName = columnName;
            this.type = type;
            this.isPrimary = isPrimary;
            clazz = null;
            inversedColumnName = null;
            tableName = null;
        }

        public PropertyMap(String fieldName, String columnName, Class<? extends SQLObject> clazz) {
            this.fieldName = fieldName;
            this.columnName = columnName;
            type = null;
            isPrimary = false;
            this.clazz = clazz;
            inversedColumnName = null;
            tableName = null;
        }

        public PropertyMap(String fieldName, String columnName, Class<? extends SQLObject> clazz, SQLTypes type) {
            this.fieldName = fieldName;
            this.columnName = columnName;
            this.type = type;
            isPrimary = false;
            this.clazz = clazz;
            inversedColumnName = null;
            tableName = null;
        }

        public PropertyMap(String fieldName, String columnName, String inversedColumnName, String tableName, Class<? extends SQLObject> clazz, SQLTypes type) {
            this.fieldName = fieldName;
            this.columnName = columnName;
            this.type = type;
            isPrimary = false;
            this.clazz = clazz;
            this.inversedColumnName = inversedColumnName;
            this.tableName = tableName;
        }

        @Override
        public boolean equals(Object obj){
            if(!(obj instanceof PropertyMap))
                    return false;
            PropertyMap map = (PropertyMap) obj;
            return this.columnName.equals(map.columnName);
        }
    }

    private enum CacheKeys {
        Properties,
        OneToMany,
        ManyToOne,
        ManyToMany
    }

    private static HashMap<CacheKeys, HashMap<Class<? extends SQLObject>, List<PropertyMap>>> cachedData;
    // private static HashMap<Class<? extends SQLObject>, List<PropertyMap>> cachedOneToMany;
    // private static HashMap<Class<? extends SQLObject>, List<PropertyMap>> cachedManyToOne;
    private static HashMap<Class<? extends SQLObject>, String> cachedTableNames;
    private static HashMap<Class<? extends SQLObject>, PropertyMap> cachedPrimaryKeys;

    static {
        cachedData = new HashMap<>();
        for(CacheKeys key : CacheKeys.values())
        {
            cachedData.put(key, new HashMap<>());
        }
        cachedPrimaryKeys = new HashMap<>();
        cachedTableNames = new HashMap<>();
    }

    public static <T extends SQLObject, A extends Annotation> List<PropertyMap> getAnnotatedColumns(Class<T> clazz, Class<A> annotation, CacheKeys lookupTable, BiFunction<Field, A, PropertyMap> filler) {
        HashMap<Class<? extends SQLObject>, List<PropertyMap>> tbl = cachedData.get(lookupTable);
        if (tbl.containsKey(clazz))
            return tbl.get(clazz);

        List<PropertyMap> list = new ArrayList<>();

        Class<?> tempClass = clazz;
        do {

            for (Field field : tempClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(annotation))
                    continue;
                A p = (A) field.getAnnotation(annotation);
                PropertyMap map = filler.apply(field, p);
                list.add(map);
            }

            tempClass = tempClass.getSuperclass();

            if (tempClass.equals(SQLObject.class))
                break;
        } while (true);

        tbl.put(clazz, list);
        return list;
    }

    public static <T extends SQLObject> List<PropertyMap> getOneToManyColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, OneToMany.class, CacheKeys.OneToMany, 
                (f, p) -> new PropertyMap(f.getName(), 
                        p.mappedBy(), 
                        p.targetEntity())
                );
    }

    public static <T extends SQLObject> List<PropertyMap> getManyToOneColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, ManyToOne.class, CacheKeys.ManyToOne,
                (f, p) -> new PropertyMap("inversedBy-".concat(f.getName()), 
                    p.inverserdBy(), 
                    p.targetEntity(), 
                    SchemaHelper.getPrimaryKey(p.targetEntity()).type));
    }

    public static <T extends SQLObject> List<PropertyMap> getManyToManyColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, ManyToMany.class, CacheKeys.ManyToMany,
                (f, p) -> new PropertyMap(f.getName(), p.mappedBy(), p.inversedBy(), p.joinTable(), p.targetEntity(), SQLTypes.Long));
    }

    public static <T extends SQLObject> List<PropertyMap> getCombinedColumns(Class<T> clazz)
    {
        List<PropertyMap> map = new ArrayList<>(getAnnotatedColumns(clazz, Property.class, CacheKeys.Properties,
                    (f, p) -> new PropertyMap(f.getName(), p.name(), p.type(), f.isAnnotationPresent(PrimaryKey.class))
                ));
            map.addAll(getManyToOneColumns(clazz));
        return map;
    }

    public static <T extends SQLObject> List<PropertyMap> getColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, Property.class, CacheKeys.Properties,
                (f, p) -> new PropertyMap(f.getName(), p.name(), p.type(), f.isAnnotationPresent(PrimaryKey.class)));
    }

    public static <T extends SQLObject> String getTableName(Class<T> clazz)
    {
        if(cachedTableNames.containsKey(clazz))
            return cachedTableNames.get(clazz);
        Class<?> tempClass = clazz;
        String tableName = null;
        
        do {

            if(tempClass.isAnnotationPresent(Table.class))
            {
                tableName = tempClass.getAnnotation(Table.class).name();
                break;
            }

            tempClass = tempClass.getSuperclass();

            if (tempClass.equals(SQLObject.class))
                break;
        } while (true);

        if(tableName == null)
            throw new TableNameNotSetException();
        return tableName;
    }

    public static <T extends SQLObject> SQLTypes[] getCombinedColumnsType(Class<T> clazz)
    {
        List<PropertyMap> properties = getCombinedColumns(clazz);
        SQLTypes[] types = new SQLTypes[properties.size()];
        int i=0;
        for(PropertyMap p : properties){
            types[i++] = p.type;
        }
        return types;
    }

    public static <T extends SQLObject> PropertyMap getPrimaryKey(Class<T> clazz) 
    {
        if (cachedPrimaryKeys.containsKey(clazz))
            return cachedPrimaryKeys.get(clazz);

        Class<?> tempClass = clazz;
        PropertyMap primaryKey = null;
        do {

            for (Field field : tempClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(PrimaryKey.class))
                    continue;
                Property p = field.getAnnotation(Property.class);
                primaryKey = new PropertyMap(field.getName(), p.name(), p.type(), true);
                break;
            }

            tempClass = tempClass.getSuperclass();

            if (tempClass.equals(SQLObject.class))
                break;
        } while (true);

        if(primaryKey == null)
            throw new PrimaryKeyNotFoundException();
        return primaryKey;
    }
}