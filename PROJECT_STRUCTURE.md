# Architecture du Projet

Documentation détaillée de l'architecture et structure du système de gestion des chambres du campus.

## Vue d'Ensemble des Couches

```
┌─────────────────────────────────────────────────────┐
│                  COUCHE PRÉSENTATION                │
│         UI Controllers (JavaFX - Stage/Scene)       │
│  LoginController, AdminDashboard, ChefBatimentDash  │
│  BatimentViewController, ChambreViewController, etc │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                  COUCHE MÉTIER                      │
│          Managers (Business Logic)                  │
│  GestionUtilisateur, GestionBatiment,               │
│  GestionChambre, GestionEtudiant                    │
└─────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────┐
│                  COUCHE MODÈLE                      │
│          Entités Métier (Data Classes)              │
│  Utilisateur, Batiment, Chambre, Etudiant           │
│         UserRole (Énumération)                      │
└─────────────────────────────────────────────────────┘
```

## Structure des Packages

### `com.campus.models`
Contient les classes de données (POJO - Plain Old Java Objects).

```
models/
├── Utilisateur.java        # Classe utilisateur avec authentification
├── UserRole.java           # Énumération des rôles (ADMIN, CHEF_BATIMENT, ETUDIANT)
├── Batiment.java           # Classe bâtiment du campus
├── Chambre.java            # Classe chambre avec gestion d'état
└── Etudiant.java           # Classe étudiant avec affectation
```

**Responsabilités** :
- Représenter les entités métier
- Fournir getters/setters
- Logique simple (toString, equals)

### `com.campus.managers`
Contient les managers qui gèrent la logique métier.

```
managers/
├── GestionUtilisateur.java  # Authentification et gestion des utilisateurs
├── GestionBatiment.java     # CRUD des bâtiments
├── GestionChambre.java      # CRUD des chambres + disponibilité
└── GestionEtudiant.java     # CRUD des étudiants + affectations
```

**Responsabilités** :
- CRUD (Create, Read, Update, Delete)
- Validation des données
- Logique métier
- Gestion des collections

### `com.campus.ui.controllers`
Contient les contrôleurs JavaFX pour l'interface utilisateur.

```
ui/controllers/
├── Main.java                           # Point d'entrée de l'application
├── LoginController.java                # Gestion de l'authentification
├── AdminDashboardController.java       # Dashboard administrateur
├── ChefBatimentDashboardController.java # Dashboard chef de bâtiment
├── EtudiantDashboardController.java    # Espace étudiant
├── BatimentViewController.java         # Vue CRUD bâtiments
├── ChambreViewController.java          # Vue CRUD chambres
├── EtudiantViewController.java         # Vue CRUD étudiants
├── UtilisateurViewController.java      # Vue CRUD utilisateurs
└── StatisticsViewController.java       # Statistiques et graphiques
```

**Responsabilités** :
- Créer les interfaces utilisateur
- Gérer les événements (button clicks, etc.)
- Afficher les données
- Valider les entrées utilisateur

## Flux de Données

### 1. Authentification

```
┌─────────────────────────────────────┐
│   LoginController.handleLogin()     │
│  - Récupère email et mot de passe   │
└────────────┬────────────────────────┘
             ↓
┌─────────────────────────────────────┐
│ GestionUtilisateur.authentifier()   │
│  - Cherche l'utilisateur             │
│  - Vérifie les identifiants          │
│  - Retourne l'utilisateur ou null    │
└────────────┬────────────────────────┘
             ↓
┌─────────────────────────────────────┐
│ Afficher le dashboard approprié      │
│ Admin / Chef de Bâtiment / Étudiant  │
└─────────────────────────────────────┘
```

### 2. Affectation de Chambre

```
┌──────────────────────────────────────┐
│ EtudiantViewController.showAffecterDialog() │
│  - Affiche les chambres libres            │
└────────────┬─────────────────────────┘
             ↓
┌──────────────────────────────────────┐
│ GestionEtudiant.affecterChambre()    │
│  - Mises à jour étudiant              │
│  - Configure chambreId et date        │
└────────────┬─────────────────────────┘
             ↓
┌──────────────────────────────────────┐
│ GestionChambre.affecterChambre()     │
│  - Occupe la chambre                 │
│  - Affecter l'étudiant               │
│  - Change l'état à "Occupée"         │
└──────────────────────────────────────┘
```

### 3. Gestion des Bâtiments

```
┌─────────────────────────────────┐
│ BatimentViewController.show()    │
│ - Crée la table des bâtiments   │
└────────────┬────────────────────┘
             ↓
┌─────────────────────────────────┐
│ GesteatBatiment.getAllBatiments()│
│ - Récupère tous les bâtiments   │
└────────────┬────────────────────┘
             ↓
┌─────────────────────────────────┐
│ Afficher dans la TableView       │
└─────────────────────────────────┘
```

## Modèle de Données

### Entités et Relationships

```
Utilisateur (1)
├── email (PK)
├── nom
├── prenom
├── motDePasse
├── role (FK → UserRole)
└── batimentId (FK → Batiment) [Optionnel - pour chefs]

Batiment (1)
├── id (PK)
├── nom
├── adresse
└── etages

Chambre (*)
├── id (PK)
├── code (Unique)
├── batimentId (FK → Batiment)
├── etage
├── type
├── capacite
├── etat (Libre/Occupée/Maintenance)
└── etudiantId (FK → Etudiant) [Optionnel]

Etudiant (1)
├── id (PK)
├── nom
├── prenom
├── email
├── numeroMatricule (Unique)
├── specialite
├── chambreId (FK → Chambre) [Optionnel]
└── dateAffectation
```

### Relations

- **1 Bâtiment : N Chambres**
  - Un bâtiment contient plusieurs chambres
  - Une chambre appartient à un seul bâtiment

- **1 Chambre : 0..1 Étudiant**
  - Une chambre peut accueillir 0 ou 1 étudiant
  - Un étudiant ne peut avoir qu'une seule chambre

- **1 Utilisateur (Chef) : 1 Bâtiment**
  - Un chef de bâtiment gère un seul bâtiment
  - Un bâtiment peut avoir 1 chef assigné

## Énumérations

### UserRole

Définit les trois rôles du système :

```java
enum UserRole {
    ADMIN("Administrateur"),           // Accès complet
    CHEF_BATIMENT("Chef de bâtiment"), // Accès limé à son bâtiment
    ETUDIANT("Étudiant")               // Accès limité à sa chambre
}
```

## Flux d'Exécution Principal

```
1. Démarrage
   └─> Main.start()

2. Affichage du Login
   └─> LoginController.createLoginView()

3. Authentification
   └─> GestionUtilisateur.authentifier()

4. Redirection par rôle
   ├─> Admin → AdminDashboardController
   ├─> Chef → ChefBatimentDashboardController
   └─> Étudiant → EtudiantDashboardController

5. Gestion des entités
   └─> ViewControllers (CRUD operations)
       └─> Manager (Logique métier)
           └─> Models (Données)
```

## Gestion des Données

### Collection de Données

Toutes les collections utilisent `LinkedHashMap` pour :
- Maintenir l'ordre d'insertion
- Accès rapide par clé (ID)
- Itération simple

```java
private Map<String, Utilisateur> utilisateurs = new LinkedHashMap<>();
```

### Initialisation des Données

Chaque manager initialise des données d'exemple dans le constructeur :

```java
public GestionBatiment() {
    this.batiments = new LinkedHashMap<>();
    initialiserDonnees();  // Pré-charge les données de test
}
```

## Sécurité

### Authentification
- Comparaison simple email/mot de passe
- ⚠️ Pas de hashage (À améliorer en production)
- Session utilisateur maintenue dans `GestionUtilisateur`

### Autorisations
- Vérification du rôle dans chaque dashboard
- Chef de bâtiment limité à son bâtiment
- Étudiant limité à sa chambre personnelle

## Points d'Extension

### Pour Ajouter une Nouvelle Entité

1. **Créer la classe modèle** dans `models/`
2. **Créer le manager** dans `managers/`
3. **Créer le contrôleur** dans `ui/controllers/`
4. **Ajouter au manager parent** (ex: AdminDashboardController)

### Pour Ajouter une Nouvelle Vue

1. Créer une classe `[Entité]ViewController`
2. Implémenter `show()` pour créer Stage
3. Créer `setupTable()` pour les colonnes
4. Implémenter les dialogues CRUD

## Performance

### Optimisations Actuelles
- Collections HashMap (O(1) lookup)
- Mise en cache des données en mémoire
- Pas de requête réseau

### Limitations
- Données limitées à la RAM disponible
- Pas de pagination (toutes les données chargées)
- À améliorer pour production

## Scalabilité

Pour adapter à une plus grande échelle :

1. **Base de Données**
   - Remplacer LinkedHashMap par une vraie BD
   - JPA/Hibernate pour ORM

2. **API REST**
   - Spring Boot REST API
   - Séparation client/serveur

3. **Caching**
   - Redis pour cache distribué

4. **Architecture Microservices**
   - Service d'authentification
   - Service de gestion de chambres
   - Service de statistiques
