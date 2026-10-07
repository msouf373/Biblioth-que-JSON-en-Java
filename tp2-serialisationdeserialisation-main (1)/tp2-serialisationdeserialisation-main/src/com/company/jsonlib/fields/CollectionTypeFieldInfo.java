package com.company.jsonlib.fields;

import com.company.jsonlib.instrospectors.SerialisationIntrospector;

import java.lang.reflect.Field;
import java.util.Collection;

public class CollectionTypeFieldInfo extends FieldInfo {

    private SerialisationIntrospector reflector;

    public CollectionTypeFieldInfo(Field field) {
        super(field);
    }

    @Override
    public void fillField(Object instance, Object value) {
        try {
            getField().set(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJson(Object instance) {
        Object value = getValue(instance);
        if (value == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder("[");

        /*
        Gestion des collections et des arrays: dans une collection, pour chaque element, on sérialise selon son type
        -Si c'est un type simple on le transforme directement en JSON
        -Si c'est un objet complexe, on utilise un introspector pour le sérialiser
        */

        if (value.getClass().isArray()) {
            // Gestion des arrays
            Object[] array = (Object[]) value;
            for (int i = 0; i < array.length; i++) {
                if (i > 0) {
                    json.append(",");
                }
                json.append(serializeElement(array[i]));
            }
        } else if (value instanceof Collection) {
            // Gestion des collections (List, Set, etc.)
            Collection<?> collection = (Collection<?>) value;
            int index = 0;
            for (Object element : collection) {
                if (index > 0) {
                    json.append(",");
                }
                json.append(serializeElement(element));
                index++;
            }
        }

        json.append("]");
        return json.toString();
    }

    private String serializeElement(Object element) {
        if (element == null) {
            return "null";
        }

        Class<?> elementType = element.getClass();

        // Types simples
        if (isPrimitiveOrWrapper(elementType) || elementType == String.class) {
            if (elementType == String.class) {
                return "\"" + escapeJson(element.toString()) + "\"";
            } else {
                return element.toString();
            }
        } else {
            // Objets complexes
            reflector = new SerialisationIntrospector(element);
            return reflector.toJson(element);
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Integer.class ||
                type == Long.class ||
                type == Double.class ||
                type == Float.class ||
                type == Boolean.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Short.class;
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
