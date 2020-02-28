package com.tony.automationserver.sqlhelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import com.tony.automationserver.sqlhelper.SQLHelper.SQLTypes;
import com.tony.automationserver.sqlhelper.annotation.ManyToMany;
import com.tony.automationserver.sqlhelper.annotation.ManyToOne;
import com.tony.automationserver.sqlhelper.annotation.OneToMany;
import com.tony.automationserver.sqlhelper.annotation.OneToOne;
import com.tony.automationserver.sqlhelper.annotation.PrimaryKey;
import com.tony.automationserver.sqlhelper.annotation.Property;
import com.tony.automationserver.sqlhelper.annotation.Table;
import com.tony.automationserver.sqlhelper.exception.PrimaryKeyNotFoundException;
import com.tony.automationserver.sqlhelper.exception.TableNameNotSetException;

public class SchemaHelper {

    public static class PropertyMap {
        public final Field field;
        public final String columnName;
        public final String inversedColumnName;
        public final SQLTypes type;
        public final boolean isPrimary;
        public final String tableName;
        public final Class<? extends SQLObject> clazz;

        public PropertyMap(Field field, String columnName, SQLTypes type, boolean isPrimary){
            this.field = field;
            this.columnName = columnName;
            this.type = type;
            this.isPrimary = isPrimary;
            clazz = null;
            inversedColumnName = null;
            tableName = null;
        }

        public PropertyMap(Field field, String columnName, Class<? extends SQLObject> clazz) {
            this.field = field;
            this.columnName = columnName;
            type = null;
            isPrimary = false;
            this.clazz = clazz;
            inversedColumnName = null;
            tableName = null;
        }

        public PropertyMap(Field field, String columnName, String inversedColumnName, Class<? extends SQLObject> clazz) {
            this.field = field;
            this.columnName = columnName;
            type = null;
            isPrimary = false;
            this.clazz = clazz;
            this.inversedColumnName = inversedColumnName;
            tableName = null;
        }

        public PropertyMap(Field field, String columnName, Class<? extends SQLObject> clazz, SQLTypes type) {
            this.field = field;
            this.columnName = columnName;
            this.type = type;
            isPrimary = false;
            this.clazz = clazz;
            inversedColumnName = null;
            tableName = null;
        }

        public PropertyMap(Field field, String columnName, String inversedColumnName, String tableName, Class<? extends SQLObject> clazz, SQLTypes type) {
            this.field = field;
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
        ManyToMany,
        OneToOne
    }

    private static HashMap<CacheKeys, HashMap<Class<? extends SQLObject>, List<PropertyMap>>> cachedData;
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

    private static BiFunction<Field, OneToMany, PropertyMap> OneToManyBiFunction = (f, a) -> {
        return new PropertyMap(f, a.mappedBy(), a.targetEntity());
    };

    private static BiFunction<Field, ManyToMany, PropertyMap> ManyToManyBiFunction = (f, a) -> {
        return new PropertyMap(f, a.mappedBy(), a.inversedBy(), a.joinTable(), a.targetEntity(), SQLTypes.Long);
    };

    private static BiFunction<Field, ManyToOne, PropertyMap> ManyToOneBiFunction = (f, a) -> {
        return new PropertyMap(f, 
                    a.inverserdBy(), 
                    a.targetEntity(), 
                    SchemaHelper.getPrimaryKey(a.targetEntity()).type);
    };

    private static BiFunction<Field, OneToOne, PropertyMap> OneToOneBiFunction = (f, a) -> {
        return new PropertyMap(f, a.inverserdBy(), a.mappedBy(), a.targetEntity());
    };

    public static <T extends SQLObject, A extends Annotation> PropertyMap getXProperty(Class<T> clazz, Field f, Class<A> relation,
            CacheKeys c, BiFunction<Field, A, PropertyMap> filler) {
        List<PropertyMap> lst = getAnnotatedColumns(clazz, relation, c, filler);
        for (PropertyMap p : lst) {
            if (p.field.equals(f))
                return p;
        }
        return null;
    }

    public static <T extends SQLObject> PropertyMap getOneToManyProperty(Class<T> clazz, Field f){
        return getXProperty(clazz, f, OneToMany.class, CacheKeys.OneToMany, OneToManyBiFunction);
    }

    public static <T extends SQLObject> PropertyMap getOneToOneProperty(Class<T> clazz, Field f) {
        return getXProperty(clazz, f, OneToOne.class, CacheKeys.OneToOne, OneToOneBiFunction);
    }

    public static <T extends SQLObject> PropertyMap getManyToManyProperty(Class<T> clazz, Field f) {
        return getXProperty(clazz, f, ManyToMany.class, CacheKeys.ManyToMany, ManyToManyBiFunction);
    }

    public static <T extends SQLObject> PropertyMap getManyToOneProperty(Class<T> clazz, Field f) {
        return getXProperty(clazz, f, ManyToOne.class, CacheKeys.ManyToOne, ManyToOneBiFunction);
    }

    public static <T extends SQLObject> List<PropertyMap> getOneToManyColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, OneToMany.class, CacheKeys.OneToMany, OneToManyBiFunction);
    }

    public static <T extends SQLObject> List<PropertyMap> getManyToOneColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, ManyToOne.class, CacheKeys.ManyToOne, ManyToOneBiFunction);
    }

    public static <T extends SQLObject> List<PropertyMap> getManyToManyColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, ManyToMany.class, CacheKeys.ManyToMany, ManyToManyBiFunction);
    }

    public static <T extends SQLObject> List<PropertyMap> getCombinedColumns(Class<T> clazz)
    {
        List<PropertyMap> map = new ArrayList<>(getAnnotatedColumns(clazz, Property.class, CacheKeys.Properties,
                    (f, p) -> new PropertyMap(f, p.name(), p.type(), f.isAnnotationPresent(PrimaryKey.class))
                ));
            map.addAll(getManyToOneColumns(clazz));
        return map;
    }

    public static <T extends SQLObject> List<PropertyMap> getColumns(Class<T> clazz) {
        return getAnnotatedColumns(clazz, Property.class, CacheKeys.Properties,
                (f, p) -> new PropertyMap(f, p.name(), p.type(), f.isAnnotationPresent(PrimaryKey.class)));
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

    public static <T extends SQLObject> SQLTypes[] getColumnsType(Class<T> clazz)
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
                primaryKey = new PropertyMap(field, p.name(), p.type(), true);
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

    public static Field getRelatedField(Method thisMethod, Class<?> clazz){
        Class<?> returnType = thisMethod.getReturnType();

        Field myField = null;

        do{
            for(Field f : clazz.getDeclaredFields()){
                if(!f.getType().equals(returnType))
                    continue;
                if(!thisMethod.getName().toLowerCase().endsWith(f.getName().toLowerCase()))
                    continue;
                myField = f;
                clazz = null;
                break;
            }
            if(clazz != null)
                clazz = clazz.getSuperclass();
        }while(clazz != null);

        return myField;
    }
}