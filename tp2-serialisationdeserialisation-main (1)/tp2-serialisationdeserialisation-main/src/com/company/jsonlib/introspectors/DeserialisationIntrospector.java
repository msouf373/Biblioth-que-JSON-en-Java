package com.company.jsonlib.instrospectors;

import com.company.jsonlib.fields.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class DeserialisationIntrospector extends Introspector {

    public DeserialisationIntrospector(Class<?> dtoType) {
        super();
        this.dtoType = dtoType;
        analyzeFields();
    }

    private void analyzeFields() {
        Field[] fields = dtoType.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            // Créer le FieldInfo approprié
            FieldInfo fieldInfo = createFieldInfo(field);

            // Ignorer les champs avec @Ignore
            if (fieldInfo.isIgnored()) {
                continue;
            }

            // Classifier le champ
            if (isSimpleType(field.getType())) {
                simpleFields.add(fieldInfo);
            } else if (isCollectionOrArray(field.getType())) {
                collectionFields.add(fieldInfo);
            } else {
                objectFields.add(fieldInfo);
            }
        }
    }

    private FieldInfo createFieldInfo(Field field) {
        if (isSimpleType(field.getType())) {
            return new SimpleTypeFieldInfo(field);
        } else if (isCollectionOrArray(field.getType())) {
            return new CollectionTypeFieldInfo(field);
        } else {
            return new ObjectTypeFieldInfo(field);
        }
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                type == Integer.class ||
                type == Long.class ||
                type == Double.class ||
                type == Float.class ||
                type == Boolean.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Short.class;
    }

    private boolean isCollectionOrArray(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    public <T> T toDTO(String json, Class<T> dtoType) {
        try {
            // Créer une instance du DTO
            T instance = dtoType.getDeclaredConstructor().newInstance();

            // Parser le JSON
            Map<String, String> jsonMap = parseJson(json);

            // Remplir les champs simples
            for (FieldInfo fieldInfo : simpleFields) {
                String value = jsonMap.get(fieldInfo.getName());
                if (value != null) {
                    fieldInfo.fillField(instance, cleanValue(value));
                }
            }

            // Remplir les champs objets
            for (FieldInfo fieldInfo : objectFields) {
                String value = jsonMap.get(fieldInfo.getName());
                if (value != null && !value.equals("null")) {
                    Class<?> fieldType = fieldInfo.getField().getType();
                    DeserialisationIntrospector nestedIntrospector = new DeserialisationIntrospector(fieldType);
                    Object nestedObject = nestedIntrospector.toDTO(value, fieldType);
                    fieldInfo.fillField(instance, nestedObject);
                }
            }

            // Remplir les champs collections
            for (FieldInfo fieldInfo : collectionFields) {
                String value = jsonMap.get(fieldInfo.getName());
                if (value != null && !value.equals("null")) {
                    Object collectionValue = parseCollection(value, fieldInfo.getField());
                    fieldInfo.fillField(instance, collectionValue);
                }
            }

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();

        // Enlever les accolades
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1);
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }

        // Parser les paires clé-valeur
        int i = 0;
        while (i < json.length()) {
            // Trouver la clé
            int keyStart = json.indexOf('"', i);
            if (keyStart == -1) break;
            int keyEnd = json.indexOf('"', keyStart + 1);
            String key = json.substring(keyStart + 1, keyEnd);

            // Trouver la valeur
            int valueStart = json.indexOf(':', keyEnd) + 1;
            int valueEnd = findValueEnd(json, valueStart);
            String value = json.substring(valueStart, valueEnd).trim();

            map.put(key, value);
            i = valueEnd + 1;
        }

        return map;
    }

    private int findValueEnd(String json, int start) {
        int i = start;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }

        if (i >= json.length()) return json.length();

        char firstChar = json.charAt(i);

        if (firstChar == '"') {
            // Valeur string
            i++;
            while (i < json.length()) {
                if (json.charAt(i) == '"' && json.charAt(i - 1) != '\\') {
                    return i + 1;
                }
                i++;
            }
        } else if (firstChar == '{') {
            // Valeur objet
            int braceCount = 1;
            i++;
            while (i < json.length() && braceCount > 0) {
                if (json.charAt(i) == '{') braceCount++;
                if (json.charAt(i) == '}') braceCount--;
                i++;
            }
            return i;
        } else if (firstChar == '[') {
            // Valeur array
            int bracketCount = 1;
            i++;
            while (i < json.length() && bracketCount > 0) {
                if (json.charAt(i) == '[') bracketCount++;
                if (json.charAt(i) == ']') bracketCount--;
                i++;
            }
            return i;
        } else {
            // Valeur simple (nombre, boolean, null)
            while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') {
                i++;
            }
            return i;
        }

        return json.length();
    }

    private Object parseCollection(String json, Field field) {
        try {
            // Enlever les crochets
            json = json.trim();
            if (json.startsWith("[")) {
                json = json.substring(1);
            }
            if (json.endsWith("]")) {
                json = json.substring(0, json.length() - 1);
            }

            if (json.trim().isEmpty()) {
                if (field.getType().isArray()) {
                    return Array.newInstance(field.getType().getComponentType(), 0);
                } else {
                    return new ArrayList<>();
                }
            }

            // Parser les éléments
            List<String> elements = parseArrayElements(json);

            if (field.getType().isArray()) {
                // Array
                Class<?> componentType = field.getType().getComponentType();
                Object array = Array.newInstance(componentType, elements.size());

                for (int i = 0; i < elements.size(); i++) {
                    Object element = parseElement(elements.get(i), componentType);
                    Array.set(array, i, element);
                }

                return array;
            } else {
                // Collection (List)
                List<Object> list = new ArrayList<>();
                Type genericType = field.getGenericType();

                if (genericType instanceof ParameterizedType) {
                    ParameterizedType paramType = (ParameterizedType) genericType;
                    Class<?> elementType = (Class<?>) paramType.getActualTypeArguments()[0];

                    for (String elementJson : elements) {
                        Object element = parseElement(elementJson, elementType);
                        list.add(element);
                    }
                }

                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> parseArrayElements(String json) {
        List<String> elements = new ArrayList<>();
        int i = 0;

        while (i < json.length()) {
            int start = i;
            int end = findValueEnd(json, start);
            String element = json.substring(start, end).trim();

            if (!element.isEmpty() && !element.equals(",")) {
                elements.add(element);
            }

            i = end;
            // Passer la virgule
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) {
                i++;
            }
        }

        return elements;
    }

    private Object parseElement(String elementJson, Class<?> elementType) {
        elementJson = elementJson.trim();

        if (elementJson.equals("null")) {
            return null;
        }

        if (isSimpleType(elementType)) {
            String cleanValue = cleanValue(elementJson);

            if (elementType == int.class || elementType == Integer.class) {
                return Integer.parseInt(cleanValue);
            } else if (elementType == double.class || elementType == Double.class) {
                return Double.parseDouble(cleanValue);
            } else if (elementType == boolean.class || elementType == Boolean.class) {
                return Boolean.parseBoolean(cleanValue);
            } else if (elementType == String.class) {
                return cleanValue;
            }
            return cleanValue;
        } else {
            // Objet complexe
            DeserialisationIntrospector nestedIntrospector = new DeserialisationIntrospector(elementType);
            return nestedIntrospector.toDTO(elementJson, elementType);
        }
    }

    private String cleanValue(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }
}
