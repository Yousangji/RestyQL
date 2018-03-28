package io.restyql.util;


import io.restyql.annotation.RestyQL;
import io.restyql.model.RestySchema;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class RestyQLBuilder {

    /**
     * MD5 HASH
     */
    public static String RESTY_SCHEMA_ATTRIBUTE = "C8D4037B72F0BCA9A43A05C872F71A1D";

    /**
     * Created at : sangji 2018. 03
     * <p>
     * build schema which is model container filtered by restyQL annotation(value,ignore), given param(query,ignore) by client
     * represents what fields should be sent as a response in the model
     *
     * @param MethodReturnType Model Class will be sent as response
     * @param annotation       restyQL annotation to filter model
     * @param query            given param by client to filter model
     * @param ignore           given param by client to filter model
     * @return filtered model Schema
     */
    public static HashMap<String, Object> buildSchema(Class MethodReturnType, RestyQL annotation, String query, String ignore) {
        HashMap<String, HashMap> annotationValueJson = ParseFromRestyFormat(annotation != null ? annotation.value() : new String[0]);
        HashMap<String, HashMap> annotationIgnoreJson = ParseFromRestyFormat(annotation != null ? annotation.ignore() : new String[0]);
        HashMap<String, HashMap> paramQuerySchema = ParseFromRestyFormat(query != null ? query.split(",") : new String[0]);
        HashMap<String, HashMap> paramIgnoreSchema = ParseFromRestyFormat(ignore != null ? ignore.split(",") : new String[0]);
        return buildFilteredSchema(MethodReturnType, annotationValueJson, annotationIgnoreJson, paramQuerySchema, paramIgnoreSchema);
    }

    /**
     * Created at : sangji 2018. 03
     * <p>
     * parsing RestyQL format to Hashmap as a schema
     * return schema is Used as a criteria if it needs to be added or removed
     * restyQL format : {id, password, name, location.timezone.a, location.lat, location.len}
     *
     * @param valueArr given parameter would be always string array format
     * @return parsed Hashmap from array
     */
    private static HashMap<String, HashMap> ParseFromRestyFormat(String[] valueArr) {
        HashMap<String, HashMap> Schema = new HashMap<>();

        for (String value : valueArr) {
            BuildSchemaWithParentMap(Schema, value);
        }
        return Schema;
    }


    /**
     * Created at : sangji 2018.03
     * called in 'ParseFromRestyFormat' method
     * build Schema with given map
     * if fieldName already exists as a key, childMap should be embedded.
     *
     * @param parentFieldSchema Parent Field's schema to put child's schema as a value
     * @param parentFieldValue  substring after first "\\." represent
     */
    private static void BuildSchemaWithParentMap(HashMap<String, HashMap> parentFieldSchema, String parentFieldValue) {

        String[] keyValuePair = parentFieldValue.split("\\.", 2);
        String fieldName = keyValuePair[0];
        try {
            String fieldValue = keyValuePair[1]; // this would cause exception if keyValuePair.length == 1
            if (parentFieldSchema.containsKey(fieldName)) {
                BuildSchemaWithParentMap(parentFieldSchema.get(fieldName), fieldValue);
            } else {
                parentFieldSchema.put(fieldName, buildChildMap(fieldValue));
            }
        } catch (Exception e) {
            parentFieldSchema.put(fieldName, new HashMap());
        }
    }

    /**
     * Created at :  sangji 2018.03
     * <p>
     * <p>
     * for building new child schema as a parent field's Value of parent schema in 'BuildSchemaWithParentMap' method
     *
     * @param fieldValue substring as a field's value
     * @return parent schema object including embedded schema object
     */
    private static HashMap buildChildMap(String fieldValue) {
        String[] keyValuePair = fieldValue.split("\\.", 2);
        HashMap<String, HashMap> json = new HashMap<>();
        if (keyValuePair.length > 1) {
            json.put(keyValuePair[0], buildChildMap(keyValuePair[1]));
        } else {
            json.put(keyValuePair[0], new HashMap());
        }
        return json;
    }


    /**
     * Created at :  sangji 2018. 03
     * <p>
     * recursively build schema by filtering with restyQl annotations(value,ignore) & resty param(query,ignore)
     * <p>
     * if field is simple value type, hashmap has null value on field's name as a key
     * if field is bean or entity type, hashmap recursively call buildFilteredSchema to build schema as a key's value.
     *
     * @param fieldTypeAsClass declaring field's class type
     * @param annotationValue  format value to compare
     * @param annotationIgnore format ignore to compare
     * @param paramQuery       format param to compare
     * @return for given class type //key: field's type of given class,  value : embedded hasmap of a field whose type is class
     */
    private static HashMap<String, Object> buildFilteredSchema(Class fieldTypeAsClass, HashMap<String, HashMap> annotationValue, HashMap<String, HashMap> annotationIgnore, HashMap<String, HashMap> paramQuery, HashMap<String, HashMap> paramIgnore) {
        HashMap<String, Object> restySchema = new HashMap<>();
        Field[] allFields = fieldTypeAsClass.getDeclaredFields();


        for (Field field : allFields) {
            String fieldName = field.getName();
            if (annotationValue.containsKey(fieldName) || annotationValue.size() == 0) {// filtered by annotation value
                if (!annotationIgnore.containsKey(fieldName) || annotationIgnore.get(fieldName).size() != 0) {//filtered by annotation ignore
                    if (paramQuery.containsKey(fieldName) || paramQuery.size() == 0) { // filtered by param query
                        if (!paramIgnore.containsKey(fieldName) || paramIgnore.get(fieldName).size() != 0) {//filtered by param ignore
                            Class fieldType = field.getType();
                            if (isCollectionType(fieldType)) {//if field's type is collection type, fieldType should be element's type of the collection not collection itself
                                fieldType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                            }

                            if (isSimpleValueType(fieldType)) {
                                restySchema.put(fieldName, new HashMap<>());

                            } else {
                                HashMap embeddedFieldValue = annotationValue.containsKey(fieldName) ? annotationValue.get(fieldName) : new HashMap<>();
                                HashMap embeddedFieldIgnore = annotationIgnore.containsKey(fieldName) ? annotationIgnore.get(fieldName) : new HashMap<>();
                                HashMap embeddedParamQuery = paramQuery.containsKey(fieldName) ? paramQuery.get(fieldName) : new HashMap<>();
                                HashMap embeddedParamIgnore = paramIgnore.containsKey(fieldName) ? paramIgnore.get(fieldName) : new HashMap<>();
                                restySchema.put(fieldName, buildFilteredSchema(fieldType, embeddedFieldValue, embeddedFieldIgnore, embeddedParamQuery, embeddedParamIgnore));
                            }
                        }
                    }
                }
            }
        }
        return restySchema;
    }

    /**
     * Created at: sangji 2018. 3
     * <p>
     * Check if given type represents "simple value" like primitive, String, number, date, URI, URL, Locale
     *
     * @param clazz field's type as class
     * @return if field's type is simple value or not
     */
    private static boolean isSimpleValueType(Class clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz)
            || Date.class.isAssignableFrom(clazz) || URI.class == clazz || URL.class == clazz || Locale.class == clazz || Class.class == clazz || Calendar.class == clazz || clazz.getClassLoader()!=ClassLoader.getSystemClassLoader();

    }

    private static boolean isCollectionType(Class clazz) {
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
    }


    /**
     * put data to build response with schema
     *
     * @param restySchema resolved restySchema of the model
     * @param data        model's data which is being returned as a http response
     * @return resolved restyschema as a http response put data
     */
    public static Object buildDataFromSchemaMap(RestySchema restySchema, Object data) {
        return buildDataFromSchemaMap(restySchema.getRestyMap(), data);
    }


    /**
     * for make input value as java convention
     *
     * @param input field name
     * @return field Name fit java convention
     */
    private static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


    /**
     * check the data type and if collection do loop and get data from every object else get data from the object directly
     *
     * @param restySchemaMap schema for comparing
     * @param data           model data
     * @return HashMap or Array of HashMap
     */
    private static Object buildDataFromSchemaMap(HashMap restySchemaMap, Object data) {
        if (data == null) {
            return null;
        }
        Class<?> clazz = data.getClass();
        if (isCollectionType(clazz)) {
            Collection collection = (Collection) data;
            List<HashMap> collectionData = new ArrayList<>();
            for (Object item : collection) {
                collectionData.add(getSchemaDataFromObject(restySchemaMap, item));
            }
            return collectionData;
        } else {
            return getSchemaDataFromObject(restySchemaMap, data);
        }
    }

    /**
     * getting schema data from single object
     *
     * @param restySchemaMap resty schema
     * @param data           sing object
     * @return schema filled with data from single object
     */
    private static HashMap<String, Object> getSchemaDataFromObject(HashMap restySchemaMap, Object data) {
        Class<?> clazz = data.getClass();
        HashMap<String, Object> mapData = new HashMap<>();
        for (Object prop : restySchemaMap.keySet()) {
            String fieldName = prop.toString();
            Object fieldValue = null;

            try {
                Method fieldGetter = clazz.getMethod("get" + capitalize(fieldName)); //Note, this can throw an exception if the field doesn't exist.
                HashMap propSchema = (HashMap) restySchemaMap.get(fieldName);
                if (propSchema.size() == 0) {
                    fieldValue = fieldGetter.invoke(data);
                } else {
                    fieldValue = buildDataFromSchemaMap(propSchema, fieldGetter.invoke(data));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapData.put(fieldName, fieldValue);
        }
        return mapData;
    }
}

