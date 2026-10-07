package com.company.jsonlib.fields;

import com.company.jsonlib.instrospectors.SerialisationIntrospector;

import java.lang.reflect.Field;

public class ObjectTypeFieldInfo extends FieldInfo {

    private SerialisationIntrospector reflector;

    public ObjectTypeFieldInfo(Field field) {
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

        // Création d'un introspector pour l'objet imbriqué
        reflector = new SerialisationIntrospector(value);
        return reflector.toJson(value);
    }
}
