# Campus Room Management System

Un système complet de gestion des chambres du campus universitaire développé en Java avec JavaFX.

## Fonctionnalités

### Pour l'Administrateur
- **Gestion des Bâtiments**: Ajouter, modifier, supprimer et consulter tous les bâtiments
- **Gestion des Chambres**: Gérer les chambres avec codes uniques (ex: A-101)
- **Gestion des Étudiants**: Ajouter des étudiants et affecter des chambres
- **Gestion des Utilisateurs**: Créer et gérer les comptes utilisateur
- **Statistiques**: Consulter les statistiques d'occupation des chambres

### Pour le Chef de Bâtiment
- Consulter les chambres de son bâtiment
- Voir les étudiants affectés
- Vérifier la disponibilité des chambres

### Pour l'Étudiant
- Consulter sa chambre affectée
- Vérifier son affectation
- Voir ses informations personnelles

## Structure du Projet

```
src/main/java/com/campus/
├── Main.java
├── models/
│   ├── Utilisateur.java
│   ├── UserRole.java
│   ├── Batiment.java
│   ├── Chambre.java
│   └── Etudiant.java
├── managers/
│   ├── GestionUtilisateur.java
│   ├── GestionBatiment.java
│   ├── GestionChambre.java
│   └── GestionEtudiant.java
└── ui/
    └── controllers/
        ├── LoginController.java
        ├── AdminDashboardController.java
        ├── ChefBatimentDashboardController.java
        ├── EtudiantDashboardController.java
        ├── BatimentViewController.java
        ├── ChambreViewController.java
        ├── EtudiantViewController.java
        ├── UtilisateurViewController.java
        └── StatisticsViewController.java
```

## Compilation et Exécution

### Prérequis
- Java 17 ou supérieur
- Maven 3.6+

### Compiler le projet
```bash
mvn clean compile
```

### Exécuter l'application
```bash
mvn javafx:run
```

## Comptes de Test

### Admin
- Email: admin@univ.fr
- Mot de passe: admin123

### Chef de Bâtiment
- Email: chef1@univ.fr
- Mot de passe: chef123

### Étudiant
- Email: jean@univ.fr
- Mot de passe: etud123

## Architecture

### Couche Modèle
Les classes du modèle représentent les entités métier :
- `Utilisateur` : Gère les utilisateurs avec différents rôles
- `Batiment` : Représente un bâtiment du campus
- `Chambre` : Une chambre avec code unique
- `Etudiant` : Un étudiant pouvant être affecté à une chambre

### Couche Métier
Les managers gèrent la logique métier :
- `GestionUtilisateur` : Authentification et gestion des utilisateurs
- `GestionBatiment` : CRUD des bâtiments
- `GestionChambre` : Gestion des chambres et leur disponibilité
- `GestionEtudiant` : Gestion des étudiants et affectations

### Couche Présentation
Les contrôleurs JavaFX gèrent l'interface utilisateur :
- Chaque entité a une vue dédiée (CRUD)
- Dashboards spécifiques par rôle
- Écran d'authentification

## Règles Métier

- Un bâtiment peut contenir plusieurs chambres
- Une chambre appartient à un seul bâtiment
- Une chambre ne peut accueillir qu'un seul étudiant
- Un étudiant ne peut avoir qu'une seule chambre
- Les codes de chambre sont uniques (format: Bâtiment-Numéro)

## Technologies Utilisées

- **Java 17** : Langage de programmation
- **JavaFX 21** : Framework pour l'interface graphique
- **Maven** : Gestionnaire de dépendances

## Auteur

Système de gestion des chambres du campus - Version 1.0.0
