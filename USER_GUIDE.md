# Guide d'Utilisation

Guide complet pour utiliser l'application Campus Room Management System.

## Démarrage de l'Application

### Méthode 1 : Scripts Automatisés (Recommandé)

**Windows** :
```bash
Double-clic sur run.bat
```

**Linux/Mac** :
```bash
./run.sh
```

### Méthode 2 : Maven

```bash
mvn javafx:run
```

### Méthode 3 : IDE

1. Ouvrir le projet dans VS Code ou IntelliJ
2. Run > Run Main
3. L'application démarre

## Écran de Connexion

Au démarrage, vous verrez l'écran de connexion.

### Comptes de Test

| Rôle | Email | Mot de passe | Accès |
|------|-------|--------------|--------|
| Admin | admin@univ.fr | admin123 | Complet |
| Chef Bâtiment | chef1@univ.fr | chef123 | Bâtiment A |
| Chef Bâtiment | chef2@univ.fr | chef123 | Bâtiment B |
| Étudiant | jean@univ.fr | etud123 | Chambre personnelle |
| Étudiant | marie@univ.fr | etud123 | Chambre personnelle |

### Connexion

1. Entrer l'email
2. Entrer le mot de passe
3. Cliquer "Connexion"
4. Si les identifiants sont corrects, accès au dashboard

## Dashboard Administrateur

L'administrateur a accès complet à toutes les fonctionnalités.

### Menu Principal

#### 1. Gestion des Bâtiments

**Vue** : Table avec tous les bâtiments du campus

**Colonnes** :
- ID : Identifiant unique
- Nom : Nom du bâtiment
- Adresse : Localisation
- Étages : Nombre de niveaux

**Actions** :
- **Ajouter** : Nouveau bâtiment
- **Modifier** : Éditer un bâtiment
- **Supprimer** : Supprimer un bâtiment

**Test** :
1. Cliquer "Ajouter"
2. Entrer : ID=B4, Nom=Bâtiment D, Adresse=126 Rue..., Étages=3
3. Cliquer "Enregistrer"
4. Le nouveau bâtiment apparait dans la table

#### 2. Gestion des Chambres

**Vue** : Table avec filtrage par bâtiment

**Colonnes** :
- Code : Identifiant unique (ex: A-101)
- Numéro : Numéro de chambre
- Bâtiment : Bâtiment auquel appartient
- Étage : Niveau
- Type : Simple/Double/Suite
- État : Libre/Occupée/Maintenance

**Actions** :
- **Filtrer par Bâtiment** : Afficher uniquement les chambres d'un bâtiment
- **Ajouter** : Création d'une chambre
- **Modifier** : Éditer les paramètres
- **Supprimer** : Supprimer une chambre

**Test d'Ajout** :
1. Sélectionner un bâtiment
2. Cliquer "Ajouter"
3. Entrer le code (ex: A-502)
4. Configurer : Numéro, Étage, Type, Capacité
5. Cliquer "Enregistrer"

#### 3. Gestion des Étudiants

**Vue** : Table avec tous les étudiants

**Colonnes** :
- Nom : Nom complet de l'étudiant
- Matricule : Numéro unique d'immatriculation
- Spécialité : Domaine d'étude
- Chambre : Chambre affectée ou "Non affectée"

**Actions** :
- **Ajouter** : Créer un nouvel étudiant
- **Modifier** : Éditer les informations
- **Affecter une Chambre** : Assigner une chambre disponible
- **Libérer la Chambre** : Retirer l'affectation
- **Supprimer** : Supprimer un étudiant

**Test d'Affectation** :
1. Sélectionner un étudiant sans chambre
2. Cliquer "Affecter une Chambre"
3. Choisir une chambre disponible
4. Cliquer "Affecter"
5. La chambre devient "Occupée" et l'étudiant a son affectation

#### 4. Gestion des Utilisateurs

**Vue** : Table avec tous les utilisateurs

**Colonnes** :
- Nom : Nom complet
- Email : Adresse email
- Rôle : Admin/Chef de bâtiment/Étudiant
- Statut : Actif/Inactif

**Actions** :
- **Ajouter** : Créer un nouvel utilisateur
- **Modifier** : Changer les informations
- **Activer/Désactiver** : Changer le statut
- **Supprimer** : Supprimer l'utilisateur

#### 5. Statistiques

**Vue** : Statistiques et graphiques d'occupation

**Informations Affichées** :
- Nombre total de bâtiments
- Nombre total de chambres
- Chambres libres / Chambres occupées
- Nombre d'étudiants
- Étudiants logés
- Taux d'occupation (en %)
- Graphique comparatif

**Utilité** :
- Vue d'ensemble rapide
- Identification des goulots d'étranglement
- Planification des affectations

## Dashboard Chef de Bâtiment

Accès limité au bâtiment assigné.

### Menu

#### 1. Mes Chambres
Voir les chambres du bâtiment assigné.

#### 2. Étudiants du Bâtiment
Voir les étudiants logés dans le bâtiment.

#### 3. Disponibilité
Vérifier les chambres libres dans le bâtiment.

## Espace Étudiant

Accès personnel pour consulter ses informations.

### Informations Affichées

**Personnelles** :
- Nom complet
- Numéro matricule
- Spécialité

**Chambre** (si affectée) :
- Code de la chambre (ex: A-101)
- Type de chambre
- Capacité
- Bâtiment
- Étage
- Date d'affectation

**Si Non Affecté** :
Message indiquant l'absence d'affectation.

## Opérations Courantes

### Scénario 1 : Ajouter un Nouvel Étudiant et l'Affecter

1. **Admin → Gestion des Étudiants**
2. **Cliquer "Ajouter"**
   - Nom: Dupre
   - Prénom: Lucas
   - Email: lucas@univ.fr
   - Matricule: MAT006
   - Spécialité: Biologie
3. **Sélectionner l'étudiant → "Affecter une Chambre"**
   - Choisir une chambre libre (ex: A-301)
4. **Cliquer "Affecter"**
5. **Vérifier** : La chambre devient "Occupée"

### Scénario 2 : Créer un Nouveau Bâtiment

1. **Admin → Gestion des Bâtiments**
2. **Cliquer "Ajouter"**
   - ID: B4
   - Nom: Bâtiment Scientifique
   - Adresse: 127 Avenue du Campus
   - Étages: 5
3. **Cliquer "Enregistrer"**

### Scénario 3 : Ajouter des Chambres

1. **Admin → Gestion des Chambres**
2. **Filter le bâtiment** (ex: Bâtiment Scientifique)
3. **Cliquer "Ajouter"**
   - Code: BS-101
   - Numéro: 101
   - Bâtiment: Bâtiment Scientifique
   - Étage: 1
   - Type: Double
   - Capacité: 2
4. **Répéter pour d'autres chambres**

### Scénario 4 : Consulter les Statistiques

1. **Admin → Statistiques**
2. **Observer** :
   - Le taux d'occupation
   - Les chambres libres/occupées
   - Le graphique comparatif

### Scénario 5 : Libérer une Chambre

1. **Admin → Gestion des Étudiants**
2. **Sélectionner un étudiant ayant une chambre**
3. **Cliquer "Libérer la Chambre"**
4. **Confirmer** la suppression de l'affectation
5. **Vérifier** : La chambre redevient "Libre"

## Raccourcis et Astuces

### Tableau (TableView)

- **Double-clic** : Sélectionne la ligne
- **Ctrl+A** : Sélectionner tous
- **Scroll** : Défiler les lignes

### Boîtes de Dialogue

- **Tab** : Passer au champ suivant
- **Enter** : Valider (équivalent à cliquer confirmer)
- **Escape** : Fermer la boîte

### Filtrage

- **Sélectionner un filtre** puis affichage automatique
- **Décocher le filtre** : Afficher tous les éléments

## Messages d'Erreur Courants

### "Veuillez remplir tous les champs"
- **Cause** : Un champ obligatoire est vide
- **Solution** : Remplir tous les champs marqués d'un label

### "Veuillez sélectionner un [élément]"
- **Cause** : Aucune ligne sélectionnée dans la table
- **Solution** : Cliquer sur une ligne avant d'effectuer l'action

### "Email invalide"
- **Cause** : Format non valide
- **Solution** : Utiliser le format email@domaine.com

### "Identifiants invalides"
- **Cause** : Email ou mot de passe incorrect
- **Solution** : Vérifier le compte, utiliser les comptes de test

## Déconnexion

Pour quitter l'application sécurisée :

1. Cliquer le bouton **"Déconnexion"** (en haut à droite)
2. Retour à l'écran de connexion
3. Vous pouvez vous reconnecter avec un autre compte

## Questions Fréquemment Posées

### Q : Comment savoir si une chambre est libre?
**R** : Voir la colonne "État" qui affiche "Libre", "Occupée", ou "Maintenance"

### Q : Puis-je modifier un étudiant déjà affecté?
**R** : Oui, sélectionner l'étudiant → Modifier → Changer les informations

### Q : Un étudiant peut-il avoir plusieurs chambres?
**R** : Non, un étudiant ne peut avoir qu'une seule chambre. Si affecté ailleurs, l'affectation précédente est libérée.

### Q : Puis-je créer des comptes chef de bâtiment?
**R** : Oui via "Gestion des Utilisateurs" → Rôle: "Chef de bâtiment" → Assigner un bâtiment

### Q : Les données sont-elles sauvegardées?
**R** : Non pour cette version 1.0. Les données sont en mémoire. Version 1.1 aura la BD.

## Support

Pour aide supplémentaire :
- Consulter README.md
- Lire INSTALLATION.md
- Vérifier les logs de la console

## Mise à Jour

Pour mettra la fin d'une session :
1. Cliquer "Déconnexion"
2. Vous êtes déconnecté
3. Les données en mémoire persistes jusqu'à l'arrêt de l'app
