# Index de Ressources

Guide complet de tous les fichiers du projet Campus Room Management System.

---

## 📁 Organisation des Fichiers

### 🔧 Configuration & Build

| Fichier | Description | Usage |
|---------|-------------|-------|
| **pom.xml** | Configuration Maven | Gère les dépendances et build |
| **run.bat** | Script Windows | `run.bat` pour compiler et lancer |
| **run.sh** | Script Linux/Mac | `./run.sh` pour compiler et lancer |
| **.gitignore** | Exclusions Git | Ignore les fichiers temporaires |

---

### 📖 Documentation

| Fichier | Description | Lecteurs |
|---------|-------------|----------|
| **README.md** | Vue d'ensemble complète | Tous |
| **QUICK_START.md** | Démarrage en 3 étapes | Débutants |
| **INSTALLATION.md** | Installation détaillée | Dev ayant des problèmes |
| **USER_GUIDE.md** | Comment utiliser l'app | Utilisateurs finaux |
| **PROJECT_STRUCTURE.md** | Architecture & design | Développeurs |
| **IDE_CONFIG.md** | Config VS Code/IntelliJ | Développeurs |
| **CHANGELOG.md** | Historique des versions | Tous |
| **PROJECT_SUMMARY.md** | Résumé & statistiques | Chefs de projet |
| **Ce fichier** | Index de ressources | Tous |

---

### 💻 Code Source Java

#### Modèles (src/main/java/com/campus/models/)

| Classe | Description | Responsabilités |
|--------|-------------|-----------------|
| **Utilisateur.java** | Classe utilisateur | Données d'authentification |
| **UserRole.java** | Énumération des rôles | ADMIN, CHEF_BATIMENT, ETUDIANT |
| **Batiment.java** | Classe bâtiment | Données du bâtiment |
| **Chambre.java** | Classe chambre | Données chambre + état |
| **Etudiant.java** | Classe étudiant | Données étudiant + affectation |

#### Managers (src/main/java/com/campus/managers/)

| Classe | Description | Responsabilités |
|--------|-------------|-----------------|
| **GestionUtilisateur.java** | Gestion des utilisateurs | CRUD + Authentification |
| **GestionBatiment.java** | Gestion des bâtiments | CRUD des bâtiments |
| **GestionChambre.java** | Gestion des chambres | CRUD + Disponibilité |
| **GestionEtudiant.java** | Gestion des étudiants | CRUD + Affectations |

#### Contrôleurs UI (src/main/java/com/campus/ui/controllers/)

| Classe | Description | Type |
|--------|-------------|------|
| **Main.java** | Point d'entrée | Application JavaFX |
| **LoginController.java** | Authentification | Dialog |
| **AdminDashboardController.java** | Dashboard Admin | Stage |
| **ChefBatimentDashboardController.java** | Dashboard Chef | Stage |
| **EtudiantDashboardController.java** | Espace Étudiant | Stage |
| **BatimentViewController.java** | CRUD Bâtiments | Dialog |
| **ChambreViewController.java** | CRUD Chambres | Dialog |
| **EtudiantViewController.java** | CRUD Étudiants | Dialog |
| **UtilisateurViewController.java** | CRUD Utilisateurs | Dialog |
| **StatisticsViewController.java** | Statistiques & Graphes | Dialog |

---

### ⚙️ Configuration IDE (./vscode/)

| Fichier | Description |
|---------|-------------|
| **launch.json** | Configuration de lancement debug |
| **tasks.json** | Tâches Maven (compile, run, package) |
| **settings.json** | Paramètres de l'éditeur |

---

### 🔧 Fichiers de Configuration GitHub

| Fichier | Description |
|---------|-------------|
| **.github/copilot-instructions.md** | Instructions Copilot GitHub |

---

## 📊 Résumé des Fichiers

```
projetjav/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── campus/
│                   ├── Main.java (1)
│                   ├── models/ (5 fichiers)
│                   ├── managers/ (4 fichiers)
│                   └── ui/controllers/ (10 fichiers)
├── .vscode/ (3 fichiers)
├── .github/ (1 fichier)
├── Documentation (9 fichiers)
├── Configuration (5 fichiers)
└── Total: ~43 fichiers
```

---

## 🎓 Guide de Lecture Recommandé

### Pour les Débutants
1. **QUICK_START.md** - Comment lancer
2. **USER_GUIDE.md** - Comment utiliser
3. **README.md** - Fonctionnalités

### Pour les Développeurs
1. **README.md** - Vue d'ensemble
2. **INSTALLATION.md** - Setup environnement
3. **PROJECT_STRUCTURE.md** - Architecture complète
4. **IDE_CONFIG.md** - Configuration IDE
5. **Code source** - En commençant par Main.java

### Pour les Chefs de Projet
1. **PROJECT_SUMMARY.md** - Résumé exécutif
2. **CHANGELOG.md** - Version & roadmap
3. **README.md** - Fonctionnalités

---

## 🔗 Relations entre Fichiers

```
Main.java
    ↓
LoginController.java (FXML)
    ↓
[Admin|Chef|Etudiant]DashboardController.java
    ↓
[Batiment|Chambre|Etudiant|Utilisateur|Statistics]ViewController.java
    ↓
Managers* (Gestion*.java)
    ↓
Models (*.java)
```

---

## 📈 Lignes de Code par Catégorie

| Catégorie | Fichiers | Lignes (approx) |
|-----------|----------|-----------------|
| Modèles | 5 | ~350 |
| Managers | 4 | ~600 |
| Contrôleurs UI | 10 | ~2500 |
| Configuration | 8 | < 500 |
| **Total** | **27** | **~3950** |

---

## 🧪 Fichiers de Test

Pour tester les fonctionnalités, utiliser les comptes de test dans:
- **src/main/java/com/campus/managers/GestionUtilisateur.java** (méthode `initialiserDonnees`)

---

## 📦 Dépendances Maven

Définies dans **pom.xml**:
- **javafx-controls** (v21.0.3)
- **javafx-fxml** (v21.0.3)
- **javafx-graphics** (v21.0.3)

---

## 🔄 Flux de Développement

1. **Modifier le code** → Fichiers Java
2. **Compiler** → `mvn clean compile`
3. **Lancer** → `mvn javafx:run`
4. **Déboguer** → VS Code F5 ou IDE debugger
5. **Commiter** → Git (exclut fichiers dans .gitignore)

---

## ✅ Checklist de Déploiement

- [x] Code compilé sans erreur
- [x] Données de test chargées
- [x] Tous les comptes fonctionnent
- [x] Documentation complète
- [x] Scripts de lancement
- [x] Configuration IDE
- [x] .gitignore approprié
- [x] pom.xml configuré

---

## 🚀 Prochaines Étapes

1. **Lancer l'application** → Un de run.bat/run.sh/mvn
2. **Tester les fonctionnalités** → USER_GUIDE.md
3. **Explorer le code** → PROJECT_STRUCTURE.md
4. **Modifier/Étendre** → Consulter les commentaires de code
5. **Faire un commit** → Git

---

## 💬 Comment Trouver l'Information

| Besoin | Consulter |
|--------|----------|
| Lancer l'app | QUICK_START.md |
| Installer | INSTALLATION.md |
| Utiliser | USER_GUIDE.md |
| Comprendre l'archi | PROJECT_STRUCTURE.md |
| Configuration | IDE_CONFIG.md |
| Comptes de test | README.md ou USER_GUIDE.md |
| Modifier le code | PROJECT_STRUCTURE.md + code source |
| Ajouter une feature | PROJECT_STRUCTURE.md + "Points d'extension" |

---

## 📞 Support

Tous les fichiers .md contiennent une section "Support" ou "Troubleshooting".

---

**Index mis à jour**: 2024-03-29  
**Version du Projet**: 1.0.0

Pour commencer: Lire **QUICK_START.md** maintenant! 🚀
