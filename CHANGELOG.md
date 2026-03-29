# Changelog

Historique des modifications et versions du projet Campus Room Management System.

## [1.0.0] - 2024-03-29

### Ajouté
- ✅ Structure Maven complète avec JavaFX 21
- ✅ Authentification des utilisateurs avec 3 rôles (Admin, Chef de bâtiment, Étudiant)
- ✅ Gestion complète des bâtiments (CRUD)
- ✅ Gestion complète des chambres avec codes uniques
- ✅ Gestion des étudiants avec affectation de chambres
- ✅ Dashboard administrateur avec accès à toutes les fonctionnalités
- ✅ Dashboard chef de bâtiment limitée au bâtiment assigné
- ✅ Espace étudiant pour consulter sa chambre
- ✅ Statistiques d'occupation avec graphiques
- ✅ Système d'alertes et notifications
- ✅ Données de test pré-chargées
- ✅ Documentation complète (README, INSTALLATION, IDE_CONFIG)

### Fonctionnalités Implémentées

#### Modèles de Données
- `Utilisateur` : Gestion des comptes avec rôles
- `Batiment` : Structure des bâtiments du campus
- `Chambre` : Chambres avec codes uniques (A-101, B-202, etc.)
- `Etudiant` : Dossiers étudiants
- `UserRole` : Énumération des rôles

#### Couche Métier
- `GestionUtilisateur` : Authentification et gestion des utilisateurs
- `GestionBatiment` : CRUD des bâtiments
- `GestionChambre` : CRUD des chambres et gestion de disponibilité
- `GestionEtudiant` : CRUD des étudiants et affectations

#### Interface Utilisateur
- Écran de connexion
- Dashboard Admin avec menus pour :
  - Gestion des Bâtiments
  - Gestion des Chambres
  - Gestion des Étudiants
  - Gestion des Utilisateurs
  - Statistiques
- Dashboard Chef de Bâtiment
- Espace Étudiant
- Fenêtres modales pour CRUD

#### Données de Test
- 3 bâtiments pré-créés
- 10 chambres distribuées
- 5 étudiants avec affectations partielles
- 3 chefs de bâtiment
- 1 administrateur

### Comptes de Test
```
Admin: admin@univ.fr / admin123
Chef 1: chef1@univ.fr / chef123
Chef 2: chef2@univ.fr / chef123
Étudiant 1: jean@univ.fr / etud123
Étudiant 2: marie@univ.fr / etud123
```

### Configuration Technique
- Base de données : En mémoire (LinkedHashMap)
- Framework UI : JavaFX 21
- Langage : Java 17
- Gestionnaire de dépendances : Maven 3.6+
- Version du JDK : 17+

## Prochaines Versions Planifiées

### [1.1.0] - En Planification
- [ ] Persistance des données avec base de données
- [ ] Recherche et filtrage avancés
- [ ] Export de rapports (PDF/Excel)
- [ ] Logs d'audit
- [ ] Notifications par email
- [ ] Interface responsive
- [ ] Recherche par critères multiples

### [1.2.0]
- [ ] API REST
- [ ] Export des données
- [ ] Historique des modifications
- [ ] Sauvegarde/Restauration

### [2.0.0]
- [ ] Interface mobile
- [ ] Authentification LDAP/Active Directory
- [ ] Système de réservation de chambres
- [ ] Géolocalisation des bâtiments
- [ ] Notifications en temps réel

## Notes d'Implémentation

### Architecture
- **Pattern MVC** : Séparation Modèle/Vue/Contrôleur
- **Couches** : Modèles → Managers → Contrôleurs → UI
- **Données** : Structure en mémoire pour simplicité initiale

### Décisions Technologiques
- JavaFX choisi pour interface riche et multiplateforme
- Maven pour gestion simple des dépendances
- Pas de framework web pour prototype simple
- Données en mémoire pour faciliter le démarrage

### Limitations Actuelles
- Données perdues à l'arrêt de l'application
- Pas de sauvegarde en base de données
- Pas de recherche avancée
- Interface desktop uniquement
- Pas de support du multilingue

## Bugs Connus
- Aucun bug majeur signalé pour la version 1.0.0

## Contributions
Pour contribuer au projet :
1. Fork le dépôt
2. Créer une branche pour votre fonctionnalité
3. Commiter vos modifications
4. Pousser vers la branche
5. Ouvrir une Pull Request

## Licence
[À Définir]

## Auteur
Campus Room Management System - 2024
