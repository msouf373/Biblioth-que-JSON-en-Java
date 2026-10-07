# JsonTool : Bibliothèque JSON en Java

Projet du cours **INF1035** : sérialisation et désérialisation d'objets Java en JSON, à l'aide de la **réflexion** et d'**annotations personnalisées**, sans bibliothèque externe.

## Auteurs

- Cheikh Tidiane Thiam
- Myriam Soufo
- Diarietou Sow

## Description

`JsonTool` est une petite bibliothèque capable de :

- **sérialiser** un objet Java (DTO) en chaîne JSON : `toJson(objet)`
- **désérialiser** une chaîne JSON en objet Java : `toDTO(json, Classe.class)`
- **mettre en cache** l'analyse des classes pour éviter de les analyser plusieurs fois (`getCacheSize()`, `clearCache()`)

Elle utilise la réflexion Java pour analyser les classes à l'exécution, et deux annotations personnalisées :

| Annotation | Rôle |
|---|---|
| `@FieldName(override = "...")` | Renomme un champ dans le JSON (ex. `firstName` devient `prenom`) |
| `@Ignore` | Exclut un champ de la sérialisation |

### Types pris en charge

- types simples : `int`, `String`, `boolean`, `enum`, etc.
- objets imbriqués (DTO dans un DTO)
- collections : `List<CoursDTO>`
- tableaux : `CoursDTO[]`

## Structure du projet

```
src/com/company/
├── jsonlib/                  # La bibliothèque
│   ├── JsonTool.java         # Sérialisation / désérialisation / cache
│   └── annotations/          # @FieldName, @Ignore
└── test/                     # Démonstration et tests
    ├── MainClass.java        # Point d'entrée : exécute les 5 tests
    ├── CoursDTO.java         # Cours (code, nom, description, max d'étudiants)
    ├── StudentDTO.java       # Étudiant avec @FieldName et liste de cours
    └── ProgramDTO.java       # Programme avec @FieldName et tableau de cours
```

## Classes de démonstration (DTO)

- **`CoursDTO`** : objet simple (`code`, `name`, `description`, `maxStudents`).
- **`StudentDTO`** : `firstName` et `lastName` sont renommés en `prenom` et `nom` dans le JSON. Contient une liste `inscriptions` de `CoursDTO`.
- **`ProgramDTO`** : `domain` devient `domaine` et `limited` devient `limite`. Contient un tableau `composition` de `CoursDTO`.

## Tests effectués dans `MainClass`

| Test | Ce qui est vérifié |
|---|---|
| 1. `CoursDTO` | Aller-retour JSON d'un objet simple, comparaison champ par champ |
| 2. `StudentDTO` | Renommage des champs avec `@FieldName` |
| 3. `StudentDTO` + `List<CoursDTO>` | Gestion des collections d'objets |
| 4. `ProgramDTO` | Gestion des tableaux d'objets |
| 5. Cache | La taille du cache n'augmente pas pour un type déjà analysé, puis `clearCache()` le vide |

## Compilation et exécution

Prérequis : **JDK 11 ou plus** (le code utilise `String.repeat`).

Depuis la racine du projet :

```bash
# Compilation
javac -encoding UTF-8 -d out $(find src -name "*.java")

# Exécution
java -cp out com.company.test.MainClass
```

Sous Windows PowerShell :

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out com.company.test.MainClass
```

Le projet peut aussi être ouvert directement dans **IntelliJ IDEA** ou dans **VS Code** (avec l'*Extension Pack for Java*).

## Notions illustrées

Réflexion (reflection), annotations personnalisées, DTO, génériques, récursivité (objets imbriqués), mise en cache.