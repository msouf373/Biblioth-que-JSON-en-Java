package com.company.jsonlib.fields;

import com.company.jsonlib.annotations.FieldName;
import com.company.jsonlib.annotations.Ignore;

import java.lang.reflect.Field;

public abstract class FieldInfo {

    private String name;
    private FieldName overrideAnnotation;
    private Ignore ignoredAnnotation;
    private Field field;

    public FieldInfo(Field field) {
        this.field = field;
        this.field.setAccessible(true);
        this.name = field.getName();
        this.overrideAnnotation = field.getAnnotation(FieldName.class);
        this.ignoredAnnotation = field.getAnnotation(Ignore.class);
    }

    public String getName() {
        if (overrideAnnotation != null) {
            return overrideAnnotation.override();
        }
        return name;
    }

    public String getOriginalName() {
        return name;
    }

    public boolean isIgnored() {
        return ignoredAnnotation != null;
    }

    public Field getField() {
        return field;
    }

    public Object getValue(Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Modifiable
    public abstract void fillField(Object instance, Object value);

    public abstract String toJson(Object instance);
}
