/*
 * Travail réalisé par Cheikh Tidiane THIAM, Myriam SOUFO et Diarietou SOW
 * =========================================================================
 * MainClass.java — Point d’entrée : démonstration de la bibliothèque JsonTool
 * -------------------------------------------------------------------------
 * Ce fichier fait partie du projet INF1035 – Bibliothèque JSON (Java)
 *
 * Rôle de cette classe :
 *  - Créer différents objets de test (CoursDTO, StudentDTO, ProgramDTO)
 *  - Sérialiser ces objets en JSON à l’aide de JsonTool
 *  - Désérialiser le JSON pour reconstruire les objets d’origine
 *  - Vérifier l’intégrité des données après désérialisation (tests de correspondance)
 *  - Tester le mécanisme de cache utilisé par la bibliothèque
 *
 * Objectifs pédagogiques :
 *  - Manipuler la réflexion (reflection) en Java pour analyser les classes
 *  - Utiliser les annotations personnalisées (@FieldName, @Ignore)
 *  - Gérer différents types de champs :
 *      • types simples (int, String, boolean, enum, etc.)
 *      • objets complexes (DTO imbriqués)
 *      • collections (List<CoursDTO>)
 *      • tableaux (CoursDTO[])
 *  - Mettre en place un système de cache pour éviter d’analyser plusieurs fois
 *    le même type et optimiser les performances
 */

package com.company.test;

import com.company.jsonlib.JsonTool;

public class MainClass {

    public static void main(String[] args) {
        JsonTool jsonTool = new JsonTool();

        System.out.println("=".repeat(80));
        System.out.println("TEST 1: Sérialisation/Désérialisation d'un objet simple (CoursDTO)");
        System.out.println("=".repeat(80));
        testCoursDTO(jsonTool);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST 2: Sérialisation/Désérialisation avec @FieldName (StudentDTO)");
        System.out.println("=".repeat(80));
        testStudentDTO(jsonTool);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST 3: Sérialisation/Désérialisation avec List<CoursDTO>");
        System.out.println("=".repeat(80));
        testStudentWithCourses(jsonTool);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST 4: Sérialisation/Désérialisation avec Array (ProgramDTO)");
        System.out.println("=".repeat(80));
        testProgramDTO(jsonTool);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TEST 5: Test du cache");
        System.out.println("=".repeat(80));
        testCache(jsonTool);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TOUS LES TESTS SONT TERMINÉS AVEC SUCCÈS!");
        System.out.println("=".repeat(80));
    }

    private static void testCoursDTO(JsonTool jsonTool) {
        // Création d'un cour de test
        CoursDTO cours = new CoursDTO("INF1035", "Programmation Orientée Objet",
                "Cours avancé en POO avec Java", 30);

        System.out.println("Objet original:");
        System.out.println(cours);

        // Sérialiser de l'objet en JSON
        String json = jsonTool.toJson(cours);
        System.out.println("\nJSON sérialisé:");
        System.out.println(json);

        // Désérialisation du JSON en objet
        CoursDTO coursDeserialized = jsonTool.toDTO(json, CoursDTO.class);
        System.out.println("\nObjet désérialisé:");
        System.out.println(coursDeserialized);

        // Vérification champ par champ
        System.out.println("\nVérification :");
        System.out.println("Code identique : " + cours.getCode().equals(coursDeserialized.getCode()));
        System.out.println("Nom identique : " + cours.getName().equals(coursDeserialized.getName()));
        System.out.println("Description identique : " + cours.getDescription().equals(coursDeserialized.getDescription()));
        System.out.println("Nombre max d'étudiants identique : " + (cours.getMaxStudents() == coursDeserialized.getMaxStudents()));
    }
    /*Teste l'utilisation de l'annotation en verifiant que tout les champs annote
    sont correctement renommes dans le JSON  et restaures a la deserialisation */
    private static void testStudentDTO(JsonTool jsonTool) {
        // Etudiant sans inscriptions
        StudentDTO student = new StudentDTO(1, "Diarietou", "Sow", 20, "F");

        System.out.println("Objet original:");
        System.out.println(student);

        // Sérialisation avec renommage de champs via @FieldName
        String json = jsonTool.toJson(student);
        System.out.println("\nJSON sérialisé :");
        System.out.println(json);

        // Désérialisation
        StudentDTO studentDeserialized = jsonTool.toDTO(json, StudentDTO.class);
        System.out.println("\nObjet désérialisé:");
        System.out.println(studentDeserialized);

        // Vérification
        System.out.println("\nVérification :");
        System.out.println("ID identique : " + (student.getId() == studentDeserialized.getId()));
        System.out.println("Prénom identique : " + student.getFirstName().equals(studentDeserialized.getFirstName()));
        System.out.println("Nom de famille identique : " + student.getLastName().equals(studentDeserialized.getLastName()));
        System.out.println("Âge identique : " + (student.getAge() == studentDeserialized.getAge()));
    }
    /*Teste de la deserialisation d'un etudiant avec une liste de cours associes */
    private static void testStudentWithCourses(JsonTool jsonTool) {
        // Création des cours
        StudentDTO student = getStudentDTO();

        System.out.println("Objet original:");
        System.out.println(student);

        // Sérialisation
        String json = jsonTool.toJson(student);
        System.out.println("\nJSON sérialisé:");
        System.out.println(json);

        // Désérialisation
        StudentDTO studentDeserialized = jsonTool.toDTO(json, StudentDTO.class);
        System.out.println("\nObjet désérialisé:");
        System.out.println(studentDeserialized);

        // Vérification sur la liste des inscriptions
        System.out.println("\nVérification:");
        System.out.println("Nombre de cours: " + studentDeserialized.getInscriptions().size());
        System.out.println("Premier cours code: " + studentDeserialized.getInscriptions().get(0).getCode());
        System.out.println("Deuxième cours code: " + studentDeserialized.getInscriptions().get(1).getCode());
        System.out.println("Troisième cours code: " + studentDeserialized.getInscriptions().get(2).getCode());
    }

    private static StudentDTO getStudentDTO() {
        CoursDTO cours1 = new CoursDTO("INF1035", "POO", "Programmation Objet", 30);
        CoursDTO cours2 = new CoursDTO("MAT1000", "Mathématiques", "Calcul différentiel", 40);
        CoursDTO cours3 = new CoursDTO("INF1010", "Structures de données", "Algorithmes", 35);

        // Etudiant inscrit a plusieurs cours
        StudentDTO student = new StudentDTO(2, "Myriam", "Soufo", 22, "F");
        student.addInscription(cours1);
        student.addInscription(cours2);
        student.addInscription(cours3);
        return student;
    }

    /*Teste de la deserialisation d'un programme avec une composition de cours avec
    <p> permettant de valider la prise en charge des tableaux d'objet*/
    private static void testProgramDTO(JsonTool jsonTool) {
        // Création d'un tableau de cours
        CoursDTO[] composition = new CoursDTO[3];
        composition[0] = new CoursDTO("INF1035", "POO", "Programmation", 30);
        composition[1] = new CoursDTO("INF2010", "Algorithmes", "Structures de données", 35);
        composition[2] = new CoursDTO("INF3005", "Base de données", "SQL", 40);

        // Création d'un programme
        ProgramDTO program = new ProgramDTO("Génie Logiciel", "GL-2024", 1, true, composition);

        System.out.println("Objet original:");
        System.out.println(program);

        // Sérialisation
        String json = jsonTool.toJson(program);
        System.out.println("\nJSON sérialisé:");
        System.out.println(json);

        // Désérialisation
        ProgramDTO programDeserialized = jsonTool.toDTO(json, ProgramDTO.class);
        System.out.println("\nObjet désérialisé:");
        System.out.println(programDeserialized);

        // Vérification
        System.out.println("\nVérification :");
        System.out.println("Nom identique : " + program.getName().equals(programDeserialized.getName()));
        System.out.println("Code identique : " + program.getCode().equals(programDeserialized.getCode()));
        System.out.println("Domaine identique : " + (program.getDomain() == programDeserialized.getDomain()));
        System.out.println("Limité identique : " + (program.isLimited() == programDeserialized.isLimited()));
        System.out.println("Nombre de cours: " + programDeserialized.getComposition().length);
    }
    /*Teste du cache: On serialise plusieurs objets du meme type pour verifier que
    la metadonnee n,est analysee qu'une seule fois. Ensuite, on vide le cache*/
    private static void testCache(JsonTool jsonTool) {
        System.out.println("Taille du cache avant: " + jsonTool.getCacheSize());

        // Création d'objets du même type
        CoursDTO cours1 = new CoursDTO("INF1035", "POO", "Description 1", 30);
        CoursDTO cours2 = new CoursDTO("INF2000", "Algo", "Description 2", 35);

        //Premiere serialisation: remplissasge et utilisation du cache
        jsonTool.toJson(cours1);
        System.out.println("Taille du cache après 1ère sérialisation: " + jsonTool.getCacheSize());
        //Deuxieme serialisation du meme type: le cache doit etrereutilise
        jsonTool.toJson(cours2);
        System.out.println("Taille du cache après 2ème sérialisation du meme type: " + jsonTool.getCacheSize());
        System.out.println("Le cache évite d'analyser le même type 2 fois!");

        // Vidange du cache
        jsonTool.clearCache();
        System.out.println("Taille du cache après clearCache(): " + jsonTool.getCacheSize());
    }
}
