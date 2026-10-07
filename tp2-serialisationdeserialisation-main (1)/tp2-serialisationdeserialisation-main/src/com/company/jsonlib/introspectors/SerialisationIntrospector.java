package com.company.jsonlib.instrospectors;

import com.company.jsonlib.fields.*;
import java.lang.reflect.Field;
import java.util.Collection;

public class SerialisationIntrospector extends Introspector {

    public SerialisationIntrospector(Object instance) {
        super();
        // fill different inherited fields
        dtoType = instance.getClass();
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

    public String toJson(Object instance) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        // Sérialiser les champs simples
        for (FieldInfo fieldInfo : simpleFields) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(fieldInfo.getName()).append("\":");
            json.append(fieldInfo.toJson(instance));
            first = false;
        }

        // Sérialiser les champs objets
        for (FieldInfo fieldInfo : objectFields) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(fieldInfo.getName()).append("\":");
            json.append(fieldInfo.toJson(instance));
            first = false;
        }

        // Sérialiser les champs collections
        for (FieldInfo fieldInfo : collectionFields) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(fieldInfo.getName()).append("\":");
            json.append(fieldInfo.toJson(instance));
            first = false;
        }

        json.append("}");
        return json.toString();
    }
}
