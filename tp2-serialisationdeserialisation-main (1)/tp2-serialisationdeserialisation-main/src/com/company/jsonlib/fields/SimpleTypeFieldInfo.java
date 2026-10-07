package com.company.jsonlib.fields;

import java.lang.reflect.Field;

public class SimpleTypeFieldInfo extends FieldInfo {

    public SimpleTypeFieldInfo(Field field) {
        super(field);
    }

    @Override
    public void fillField(Object instance, Object value) {
        try {
            Field field = getField();
            Class<?> fieldType = field.getType();

            // Conversion selon le type
            if (fieldType == int.class || fieldType == Integer.class) {
                field.set(instance, Integer.parseInt(value.toString()));
            } else if (fieldType == double.class || fieldType == Double.class) {
                field.set(instance, Double.parseDouble(value.toString()));
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                field.set(instance, Boolean.parseBoolean(value.toString()));
            } else if (fieldType == long.class || fieldType == Long.class) {
                field.set(instance, Long.parseLong(value.toString()));
            } else if (fieldType == float.class || fieldType == Float.class) {
                field.set(instance, Float.parseFloat(value.toString()));
            } else if (fieldType == String.class) {
                field.set(instance, value.toString());
            } else {
                field.set(instance, value);
            }
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

        Field field = getField();
        Class<?> fieldType = field.getType();

        // Gestion des cas speicials des "String" avec des guillemets par les methodes toJson et escapeJson
        if (fieldType == String.class) {
            return "\"" + escapeJson(value.toString()) + "\"";
        } else {
            // Pour les types primitifs (int, double, boolean, etc.)
            return value.toString();
        }
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
