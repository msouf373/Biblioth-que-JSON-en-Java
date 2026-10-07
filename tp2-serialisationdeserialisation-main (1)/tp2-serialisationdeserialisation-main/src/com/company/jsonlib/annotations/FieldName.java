package com.company.jsonlib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cette annotation sert à définir un alias à un champ d'un DTO
 * Elle doit contenir une propriété override qui permette de "renommer" le champ
 * Exemple
 * dans un DTO
 *     @FieldName(override= "prenom")
 *     private String firstName
 *     ==> lors de la sérialisation par exemple {...; prenom: "xxxx";...}
 *     à la place de  {...; firstName: "xxxx";...}
 *
 *     Lors de la désérialisation, il faudra trouver le champ "prenom" dans le json et NON firstName.
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldName {
    String override();
}
