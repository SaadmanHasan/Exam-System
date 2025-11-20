package comp3111.examsystem.Utils;

import comp3111.examsystem.Utils.FileUtil;
import comp3111.examsystem.Utils.Entity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic database class for handling CRUD operations on entities.
 *
 * @param <T> the type of the entity
 */
public class Database<T> {
    Class<T> entitySample;
    String tableName;
    String jsonFile;

    /**
     * Constructs a new Database instance for the specified entity class.
     *
     * @param entity the class of the entity
     */
    public Database(Class<T> entity) {
        entitySample = entity;
        tableName = entitySample.getSimpleName().toLowerCase();
        String currentDir = locateProjectRoot("COMP3111_Project_Team30");
        jsonFile = Paths.get(currentDir,"ExamSystem","ExamSystem", "src", "main", "resources", "database", tableName + ".txt").toString();
        File file = new File(jsonFile);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Locates the project root directory.
     *
     * @param projectName the name of the project
     * @return the absolute path of the project root directory
     */
    private String locateProjectRoot(String projectName) {
        String currentDir = System.getProperty("user.dir");
        File dir = new File(currentDir);
        while (dir != null && !dir.getName().equals(projectName)) {
            dir = dir.getParentFile();
        }
        if (dir != null) {
            return dir.getAbsolutePath();
        } else {
            throw new RuntimeException("Project root directory not found");
        }
    }

    /**
     * Queries the database based on the key.
     *
     * @param key the key to query
     * @return the entity if found, otherwise null
     */
    // Query database based on key
    public T queryByKey(String key) {
        List<String> slist = FileUtil.readFileByLines(jsonFile);

        T res = null;
        for (int i = 0; i < slist.size(); i++) {
            T t = txtToEntity(slist.get(i));
            Object tvalue = getValue(t, "id");
            if (tvalue.toString().equals(key)) {
                res = t;
                break;
            }
        }
        return res;
    }

    /**
     * Queries the database based on multiple keys.
     *
     * @param keys the list of keys to query
     * @return the list of entities matching the keys
     */
    public List<T> queryByKeys(List<String> keys) {
        List<String> slist = FileUtil.readFileByLines(jsonFile);

        List<T> res = new ArrayList<>();
        for (int i = 0; i < slist.size(); i++) {
            T t = txtToEntity(slist.get(i));
            Object tvalue = getValue(t, "id");
            for (String key : keys) {
                if (tvalue.toString().equals(key)) {
                    res.add(t);
                    break;
                }
            }
        }
        return res;
    }


    /**
     * Queries the database based on a field.
     *
     * @param fieldName  the name of the field
     * @param fieldValue the value of the field
     * @return the list of entities matching the field
     */
    // Query database based on field
    public List<T> queryByField(String fieldName, String fieldValue) {
        List<T> list = getAll();
        List<T> resList = new ArrayList<>();
        for (T e : list) {
            Object value = getValue(e, fieldName);
            if ((value == null && fieldValue != null) || (value != null && fieldValue == null) || !value.toString().equals(fieldValue)) {
                continue;
            }
            resList.add(e);
        }
        list.clear();
        list.addAll(resList);
        return list;
    }


    /**
     * Queries the database based on multiple fields.
     *
     * @param fieldNames  the list of field names
     * @param fieldValues the list of field values
     * @return the list of entities matching the fields
     */
    public List<T> queryByFields(List<String> fieldNames, List<String> fieldValues){
        List<T> list = getAll();
        List<T> relist = new ArrayList<>();
        for(T e: list){
            boolean flag = true;
            for(int i = 0; i < fieldNames.size(); i++){
                if(fieldNames.get(i).equals("Question")){
                    System.out.println("Question");
                    Object value = getValue(e, fieldNames.get(i));
                    if(value != null && !value.toString().contains(fieldValues.get(i))){
                        flag = false;
                        break;
                    }
                }
                else{
                    Object value = getValue(e, fieldNames.get(i));
                    if((value == null && fieldValues.get(i) != null) || (value != null && fieldValues.get(i) == null) || !value.toString().equals(fieldValues.get(i))){
                        flag = false;
                        break;
                    }
                }
            }
            if(flag){
                relist.add(e);
            }
        }
        list.clear();
        list.addAll(relist);
        return list;
    }

    /**
     * Queries the database based on a field with fuzzy matching.
     *
     * @param fieldName  the name of the field
     * @param fieldValue the value of the field
     * @return the list of entities matching the field with fuzzy matching
     */
    // Query database based on field, but fuzzy matching
    public List<T> queryFuzzyByField(String fieldName, String fieldValue) {
        List<T> list = getAll();
        List<T> resList = new ArrayList<>();
        for (T e : list) {
            Object value = getValue(e, fieldName);
            if (fieldValue == null || value.toString().contains(fieldValue)) {
                resList.add(e);
            }
        }
        list.clear();
        list.addAll(resList);
        return list;
    }

    /**
     * Queries the database based on an entity.
     *
     * @param entity the entity to query
     * @return the list of entities matching the entity
     */
    // Query database based on entity
    public List<T> queryByEntity(T entity) {
        List<T> list = getAll();
        List<String> prolist = new ArrayList<>();
        Class<?> clazz = entitySample;
        while (true) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.getName().equals("id") && !field.getName().equals("dbutil")) {
                    Object obj = getValue(entity, field.getName());
                    if (obj != null && !obj.toString().isEmpty()) {
                        prolist.add(field.getName());
                    }
                }
            }
            if (clazz.equals(Entity.class)) {
                break;
            }
            else {
                clazz = clazz.getSuperclass();
            }
        }
        List<T> resList = new ArrayList<>();
        for (T e : list) {
            boolean flag = true;
            for (int i = 0; i < prolist.size(); i++) {
                String filterProp = prolist.get(i);
                String queryValue = getValue(entity, filterProp).toString();
                Object value = getValue(e, filterProp);
                if ((queryValue == null && value != null) || (queryValue != null && value == null) || !value.toString().equals(queryValue)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                resList.add(e);
            }
        }
        list.clear();
        list.addAll(resList);
        return list;
    }


    /**
     * Retrieves all the data from the database.
     *
     * @return the list of all entities
     */
    public List<T> getAll() {
        List<String> slist = FileUtil.readFileByLines(jsonFile);
        List<T> tlist = new ArrayList<>();
        for (int i = 0; i < slist.size(); i++) {
            tlist.add(txtToEntity(slist.get(i)));
        }
        return tlist;
    }


    /**
     * Joins two lists of entities.
     *
     * @param list1 the first list of entities
     * @param list2 the second list of entities
     * @return the list of joined entities
     */
    public List<T> join(List<T> list1, List<T> list2) {
        List<T> resList = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                Long id1 = (Long) getValue(list1.get(i), "id");
                Long id2 = (Long) getValue(list2.get(j), "id");
                if (id1.toString().equals(id2.toString())) {
                    resList.add(list1.get(i));
                    break;
                }
            }
        }
        return resList;
    }

    /**
     * Deletes an entity from the database by key.
     *
     * @param key the key of the entity to delete
     */
    public void delByKey(String key) {
        List<T> tlist = getAll();
        for (int i = 0; i < tlist.size(); i++) {
            Object value = getValue(tlist.get(i), "id");
            if (value.toString().equals(key)) {
                tlist.remove(i);
                break;
            }
        }
        try {
            FileUtil.writeTxtFile(listToStr(tlist), new File(jsonFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes entities from the database by field.
     *
     * @param fieldName  the name of the field
     * @param fieldValue the value of the field
     */
    public void delByFiled(String fieldName, String fieldValue) {
        List<T> tlist = getAll();
        for (int i = 0; i < tlist.size(); i++) {
            Object value = getValue(tlist.get(i), fieldName);
            if (value.toString().equals(fieldValue)) {
                tlist.remove(i);
                break;
            }
        }
        try {
            FileUtil.writeTxtFile(listToStr(tlist), new File(jsonFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates an entity in the database.
     *
     * @param entity the entity to update
     */
    public void update(T entity) {
        Long key1 = (Long) getValue(entity, "id");

        System.out.println(key1);

        List<T> tlist = getAll();
        for (int i = 0; i < tlist.size(); i++) {
            Long key = (Long) getValue(tlist.get(i), "id");

            if (key.toString().equals(key1.toString())) {
                Class<?> clazz = entitySample;
                while (true) {
                    for (Field field : clazz.getDeclaredFields()) {
                        if (!field.getName().equals("id") && !field.getName().equals("dbutil")) {
                            Object o = getValue(entity, field.getName());
                            setValue(tlist.get(i), field.getName(), o);
                        }
                    }
                    if (clazz.equals(Entity.class)) {
                        break;
                    }
                    else {
                        clazz = clazz.getSuperclass();
                    }
                }
                break;
            }
        }
        try {
            FileUtil.writeTxtFile(listToStr(tlist), new File(jsonFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an entity to the database.
     *
     * @param entity the entity to add
     */
    public void add(T entity) {
        setValue(entity, "id", System.currentTimeMillis());
        List<T> tlist = getAll();
        tlist.add(entity);
        try {
            FileUtil.writeTxtFile(listToStr(tlist), new File(jsonFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Retrieves the value of a field from an entity.
     *
     * @param entity    the entity
     * @param fieldName the name of the field
     * @return the value of the field
     */
    private Object getValue(Object entity, String fieldName) {
        Object value;
        Class<?> clazz = entity.getClass();
        while (true) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                value = field.get(entity);
                break;
            }
            catch (NoSuchFieldException e) {
                if (clazz.equals(Object.class))
                    throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            clazz = clazz.getSuperclass();
        }
        return value;
    }


    /**
     * Sets the value of a field in an entity.
     *
     * @param entity     the entity
     * @param fieldName  the name of the field
     * @param fieldValue the value to set
     */
    private void setValue(Object entity, String fieldName, Object fieldValue) {
        Class<?> clazz = entity.getClass();
        while (true) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(entity, fieldValue);
                break;
            }
            catch (NoSuchFieldException e) {
                if (clazz.equals(Object.class))
                    throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            clazz = clazz.getSuperclass();
        }
    }


    /**
     * Converts a list of entities to a string.
     *
     * @param tlist the list of entities
     * @return the string representation of the list
     */
    private String listToStr(List<T> tlist) {
        StringBuilder sbf = new StringBuilder();
        for (T t : tlist) {
            sbf.append(entityToTxt(t)).append("\r\n");
        }
        return sbf.toString();
    }

    /**
     * Converts a text representation to an entity.
     *
     * @param txt the text representation
     * @return the entity
     */
    private T txtToEntity(String txt) {
        T t = null;
        try {
            t = entitySample.getConstructor().newInstance();
            String[] pros = txt.split(",");
            for (String proStr : pros) {
                String[] pro = proStr.split(":");

                if (pro[0].equals("id")) {
                    setValue(t, pro[0], Long.valueOf(pro[1]));
                } else {
                    Field field = entitySample.getDeclaredField(pro[0]);
                    if (List.class.isAssignableFrom(field.getType())) {
                        ParameterizedType listType = (ParameterizedType) field.getGenericType();
                        Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                        if (listClass.equals(String.class)) {
                            String[] longStrings = pro[1].substring(1, pro[1].length() - 1).split(";");
                            List<String> longList = new ArrayList<>();

                            for (String longStr : longStrings) {
                                longList.add(longStr);
                            }
                            //
                            setValue(t, pro[0], longList);
                        }
                    } else {
                        setValue(t, pro[0], pro[1]);
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return t;
    }

    /**
     * Sets the JSON file path for the database.
     *
     * @param jsonFile the path of the JSON file
     */
    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
        File file = new File(jsonFile);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Converts an entity to a text representation.
     *
     * @param t the entity
     * @return the text representation of the entity
     */
    private String entityToTxt(T t) {
        StringBuffer sbf = new StringBuffer();
        Class<?> clazz = entitySample;
        while (true) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.getName().equals("dbutil")) {
                    Object obj = getValue(t, field.getName());
                    if (obj != null && !obj.toString().isEmpty()) {
                        sbf.append(field.getName()).append(":");
                        if (obj instanceof List) {
                            List<?> list = (List<?>) obj;
                            sbf.append("[");
                            for (int i = 0; i < list.size(); i++) {
                                sbf.append(list.get(i));
                                if (i < list.size() - 1) {
                                    sbf.append(";"); // Use semicolon as the separator
                                }
                            }
                            sbf.append("]");
                        } else {
                            sbf.append(obj);
                        }
                        sbf.append(",");
                    }
                }
            }
            if (clazz.equals(Entity.class)) {
                break;
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        return sbf.toString();
    }}