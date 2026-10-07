package com.company.jsonlib.instrospectors;

import com.company.jsonlib.fields.FieldInfo;

import java.util.ArrayList;
import java.util.List;

public class Introspector {
    protected Class<?> dtoType;
    protected List<FieldInfo> simpleFields;
    protected List<FieldInfo> objectFields;
    protected List<FieldInfo> collectionFields;

    public Introspector() {
        this.simpleFields = new ArrayList<>();
        this.objectFields = new ArrayList<>();
        this.collectionFields = new ArrayList<>();
    }

    public Class<?> getDtoType() {
        return dtoType;
    }

    public List<FieldInfo> getSimpleFields() {
        return simpleFields;
    }

    public List<FieldInfo> getObjectFields() {
        return objectFields;
    }

    public List<FieldInfo> getCollectionFields() {
        return collectionFields;
    }
}
