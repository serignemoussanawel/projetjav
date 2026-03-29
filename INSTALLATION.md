# Guide d'Installation

## Prérequis

### Java Development Kit (JDK) 17+

1. Télécharger JDK 17 ou supérieur depuis :
   - https://www.oracle.com/java/technologies/downloads/
   - ou OpenJDK: https://jdk.java.net/

2. Installer JDK et ajouter au PATH :
   - Windows: Ajouter `C:\Program Files\Java\jdk-17.x.x\bin` au PATH
   - Linux/Mac: Généralement automatique

3. Vérifier l'installation :
   ```bash
   java -version
   javac -version
   ```

### Apache Maven 3.6+

1. Télécharger Maven depuis : https://maven.apache.org/download.cgi

2. Extraire l'archive :
   ```bash
   # Windows - décompresser dans C:\maven ou C:\Program Files\Apache\maven
   # Linux/Mac - décompresser dans /opt/maven ou ~/maven
   ```

3. Ajouter Maven au PATH :
   - **Windows** : 
     - Variables d'environnement > PATH
     - Ajouter `C:\Program Files\Apache\maven\bin` (ou votre chemin)
   - **Linux/Mac** : 
     ```bash
     export PATH=$PATH:/opt/maven/bin
     ```

4. Vérifier l'installation :
   ```bash
   mvn -version
   ```

## Installation du Projet

### 1. Cloner ou télécharger le projet
```bash
# Si vous avez Git
git clone <repository-url>
cd projetjav

# Sinon, télécharger et extraire le ZIP
cd projetjav
```

### 2. Compiler le projet
```bash
# Compiler seulement
mvn clean compile

# Ou utiliser les scripts fournis
# Windows
run.bat

# Linux/Mac
./run.sh
```

### 3. Exécuter l'application
```bash
# Exécuter directement
mvn javafx:run

# Ou utiliser les scripts
run.bat (Windows)
./run.sh (Linux/Mac)
```

## Dépannage

### Erreur : "mvn is not recognized"
- **Cause** : Maven n'est pas installé ou pas dans le PATH
- **Solution** : 
  1. Vérifier que Maven est installé
  2. Ajouter Maven au PATH des variables d'environnement
  3. Redémarrer le terminal

### Erreur : "Cannot find symbol"
- **Cause** : JDK ne correspond pas à la version requise
- **Solution** : 
  1. Installer JDK 17 ou supérieur
  2. Vérifier que `JAVA_HOME` pointe vers le bon JDK

### Erreur : JavaFX libraries not found
- **Cause** : Les dépendances JavaFX ne se sont pas téléchargées
- **Solution** : 
  1. Vérifier la connexion Internet
  2. Exécuter `mvn clean install`
  3. Effacer le cache Maven : `rmdir %USERPROFILE%\.m2\repository` (Windows)

### Application ne démarre pas
- **Cause** : Problème d'affichage ou de configuration
- **Solution** : 
  1. Vérifier les logs de la console
  2. S'assurer que le port n'est pas occupé
  3. Essayer de relancer l'application

## Améliorations Futures

- [ ] Intégration avec une base de données (MySQL/PostgreSQL)
- [ ] Export de statistiques (PDF/Excel)
- [ ] Système de notifications par email
- [ ] API REST pour accès externe
- [ ] Interface mobile
- [ ] Authentification Active Directory/LDAP
- [ ] Logs d'audit complets

## Support

Pour toute question ou problème d'installation, vérifier que :
1. Vous avez une connexion Internet stable
2. Vous avez les permissions nécessaires sur le dossier du projet
3. JDK et Maven sont correctement installés et configurés
