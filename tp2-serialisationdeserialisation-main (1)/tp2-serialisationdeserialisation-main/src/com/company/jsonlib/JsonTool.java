package com.company.jsonlib;

import com.company.jsonlib.instrospectors.DeserialisationIntrospector;
import com.company.jsonlib.instrospectors.SerialisationIntrospector;

import java.util.HashMap;

public class JsonTool {
    // Permettra d'éviter d'analyser un type 2x.
    // stocker ici les types rencontrés
    private HashMap<Class<?>, SerialisationIntrospector> serialisationCache;
    private HashMap<Class<?>, DeserialisationIntrospector> deserialisationCache;

    public JsonTool() {
        serialisationCache = new HashMap<>();
        deserialisationCache = new HashMap<>();
    }

    /**
     * Sérialise un objet en JSON
     * @param o L'objet à sérialiser
     * @return La chaîne JSON
     */
    public String toJson(Object o) {
        if (o == null) {
            return "null";
        }

        Class<?> objectType = o.getClass();

        // Vérifier si le type a déjà été analysé (cache)
        SerialisationIntrospector introspector;
        if (serialisationCache.containsKey(objectType)) {
            introspector = serialisationCache.get(objectType);
        } else {
            // Créer un nouvel introspector et le mettre en cache
            introspector = new SerialisationIntrospector(o);
            serialisationCache.put(objectType, introspector);
        }

        return introspector.toJson(o);
    }

    /**
     * Désérialise une chaîne JSON en objet
     * @param jsonString La chaîne JSON
     * @param toType Le type de classe cible
     * @return L'objet désérialisé
     */
    public <T> T toDTO(String jsonString, Class<T> toType) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }

        // Vérifier si le type a déjà été analysé (cache)
        DeserialisationIntrospector introspector;
        if (deserialisationCache.containsKey(toType)) {
            introspector = deserialisationCache.get(toType);
        } else {
            // Créer un nouvel introspector et le mettre en cache
            introspector = new DeserialisationIntrospector(toType);
            deserialisationCache.put(toType, introspector);
        }

        return introspector.toDTO(jsonString, toType);
    }

    /**
     * Vide le cache des introspectors
     */
    public void clearCache() {
        serialisationCache.clear();
        deserialisationCache.clear();
    }

    /**
     * Retourne la taille totale du cache
     */
    public int getCacheSize() {
        return serialisationCache.size() + deserialisationCache.size();
    }
}
