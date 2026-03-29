# FAQ & Troubleshooting

Foire aux questions et solutions aux problèmes courants.

---

## ❓ Questions Fréquentes (FAQ)

### Q1: Comment lancer l'application?

**R**: Trois façons:

1. **Windows** (La plus simple):
   ```bash
   Double-clic sur run.bat
   ```

2. **Tous les OS** (Ligne de commande):
   ```bash
   cd projetjav
   mvn javafx:run
   ```

3. **VS Code** (Clavier):
   - Appuyez sur **F5** pour déboguer

---

### Q2: Quels sont les comptes de test?

**R**: Cinq comptes prédéfinis:

```
ADMIN:
  Email: admin@univ.fr
  Mot de passe: admin123
  
CHEF BATIMENT 1:
  Email: chef1@univ.fr
  Mot de passe: chef123
  
CHEF BATIMENT 2:
  Email: chef2@univ.fr
  Mot de passe: chef123
  
ÉTUDIANT 1:
  Email: jean@univ.fr
  Mot de passe: etud123
  
ÉTUDIANT 2:
  Email: marie@univ.fr
  Mot de passe: etud123
```

---

### Q3: Les données sont-elles sauvegardées?

**R**: **Non** en v1.0. Les données sont en mémoire et perdues à la fermeture. 
C'est normal pour la version prototype. La v1.1 aura la persistance BD.

---

### Q4: Comment modifier le code?

**R**:
1. Ouvrir le dossier `projetjav` dans VS Code/IntelliJ
2. Modifier le code dans `src/main/java/com/campus/`
3. Compiler: `mvn clean compile`
4. Lancer: `mvn javafx:run`

---

### Q5: Puis-je créer des comptes personnalisés?

**R**: Oui! Allez à **Admin → Gestion des Utilisateurs → Ajouter**

---

## 🐛 Troubleshooting

### Problème 1: "mvn is not recognized"

**Cause**: Maven n'est pas installé ou pas dans le PATH.

**Solutions**:
1. Installer Maven depuis https://maven.apache.org/
2. Ajouter Maven au PATH Windows:
   - Propriétés système → Variables d'environnement
   - Ajouter `C:\Program Files\Apache\maven\bin` au PATH
3. Redémarrer le terminal
4. Vérifier: `mvn -version`

---

### Problème 2: "Cannot find symbol" ou erreur de compilation

**Cause**: Java version incompatible ou JDK mal configuré.

**Solutions**:
1. Vérifier Java 17+:
   ```bash
   java -version
   ```
2. Si < Java 17: Installer JDK 17+ depuis https://jdk.java.net/
3. Configurer JAVA_HOME:
   - Windows: Ajouter `JAVA_HOME=C:\Program Files\Java\jdk-17.x.x`
4. Redémarrer terminal et essayer de nouveau

---

### Problème 3: "Application doesn't start" ou fenêtre noire

**Cause**: Erreur JavaFX ou problème de dépendances.

**Solutions**:
1. Vérifier les logs de la console
2. Exécuter: `mvn clean install` (réinstalle dépendances)
3. Attendre le téléchargement complet des dépendances (peut prendre 30s)
4. Vérifier la connexion Internet
5. Redémarrer l'application

---

### Problème 4: "Port already in use"

**Cause**: Un autre processus utilise le port.

**Solutions**:
1. Fermer les autres instances de l'application
2. Attendre 30 secondes avant de relancer
3. Tuer le processus Java en arrière-plan

---

### Problème 5: Erreur de permission (Permission denied)

**Cause**: Permissions insuffisantes sur les fichiers.

**Solutions**:
1. **Windows**: Exécuter VS Code/Terminal en tant qu'administrateur
2. **Linux/Mac**: 
   ```bash
   chmod +x run.sh
   ./run.sh
   ```

---

### Problème 6: "OutOfMemoryError"

**Cause**: Pas assez de mémoire RAM.

**Solutions**:
1. Fermer d'autres applications
2. Augmenter la mémoire JVM (avancé):
   ```bash
   export _JAVA_OPTIONS="-Xmx2g"
   mvn javafx:run
   ```

---

### Problème 7: Interface fenêtre ne s'affiche pas

**Cause**: Problème d'affichage ou de configuration.

**Solutions**:
1. Vérifier la résolution d'écran (au moins 1024x768)
2. Vérifier les logs de la console
3. Essayer un autre IDE (IntelliJ au lieu de VS Code)
4. Lancer depuis la ligne de commande pour voir les erreurs

---

### Problème 8: Données perdues après redémarrage

**Cause**: Normal en v1.0 - données en mémoire uniquement.

**Solutions**:
- C'est par design. V1.1 aura la persistance BD.
- Pour tester: Charger les données de test:
  ```
  Les managers les rechargent au lancement
  ```

---

## ⚙️ Configuration

### Configuration Maven

**Pour compiler uniquement**:
```bash
mvn clean compile
```

**Pour compiler + exécuter**:
```bash
mvn javafx:run
```

**Pour créer un JAR exécutable**:
```bash
mvn clean package
java -jar target/campus-room-manager-1.0.0.jar
```

---

### Configuration IDE

**VS Code**:
- F5: Démarrer debug
- Ctrl+Shift+B: Compiler
- Ctrl+Shift+D: Debug panel

**IntelliJ IDEA**:
- Run > Run 'Main': Exécuter
- Run > Debug 'Main': Déboguer
- Ctrl+F9: Compiler

---

## 🔍 Debogage

### Voir les logs

Les logs s'affichent dans la console intégrée ou le terminal.

Cherchez les messages comme:
```
[ERROR] Compilation failed
[WARN] Missing dependency
[INFO] BUILD SUCCESS
```

### Ajouter des points d'arrêt

1. Cliquer à gauche d'un numéro de ligne dans VS Code
2. Appuyer sur F5 pour démarrer le debug
3. L'exécution s'arrête au point d'arrêt
4. Inspecter les variables

---

## ✅ Checklist de Dépannage

Si l'app ne fonctionne pas:

- [ ] Vérifier Java 17+: `java -version`
- [ ] Vérifier Maven: `mvn -version`
- [ ] Compiler: `mvn clean compile`
- [ ] Voir les erreurs dans la console
- [ ] Réinstaller dépendances: `mvn clean install`
- [ ] Redémarrer le terminal
- [ ] Essayer un autre IDE
- [ ] Vérifier la connexion Internet
- [ ] Consulter INSTALLATION.md

---

## 📱 Vérifier les Prérequis

```bash
# Vérifier Java
java -version
# Doit afficher Java 17 ou supérieur

# Vérifier Maven
mvn -version
# Doit afficher Maven 3.6 ou supérieur

# Vérifier le projet
mvn clean compile
# Doit afficher [INFO] BUILD SUCCESS
```

---

## 📞 Obtenir de l'Aide

1. **Vérifier ce fichier** (FAQ & Troubleshooting)
2. **Lire QUICK_START.md** (démarrage rapide)
3. **Lire INSTALLATION.md** (installation détaillée)
4. **Lire PROJECT_STRUCTURE.md** (comprendre le code)
5. **Vérifier les logs** (erreurs de compilation)

---

## 💡 Astuces Utiles

- **Premier lancement lent**: Normal (télécharge dépendances)
- **Erreurs aléatoires**: Souvent résolues par redémarrage
- **Terminal fermé**: Rouvrir et relancer
- **Données perdues**: C'est normal en v1.0
- **Besoin de persistance**: Attendre v1.1 ou implémenter BD

---

## 🚀 Points de Contrôle

| Étape | Vérification |
|-------|-------------|
| Installation | `java -version` affiche 17+ |
| | `mvn -version` fonctionne |
| Compilation | `mvn clean compile` → BUILD SUCCESS |
| Lancement | Interface JavaFX s'affiche |
| Connexion | Email/Mot de passe fonctionnent |
| Données de test | Bâtiments/Chambres visibles |

---

## 📊 Erreurs Courantes & Solutions

| Erreur | Solution |
|--------|----------|
| mvn not found | Installer Maven |
| Cannot find java | Installer JDK 17+ |
| BUILD FAILURE | Vérifier Java/Maven versions |
| Port in use | Fermer autre instance |
| No space left | Libérer de la RAM |
| Permission denied | Run as admin |

---

## 🎓 Allez Plus Loin

Une fois l'app fonctionnelle:
1. Modifier le code
2. Ajouter des fonctionnalités
3. Implémenter une BD
4. Créer une API REST
5. Déployer en ligne

Consultez **PROJECT_STRUCTURE.md** pour la "Points d'Extension".

---

**Dernière mise à jour**: 2024-03-29  
**Version du Projet**: 1.0.0

Pour commencer: Voir **START_HERE.md** ou **QUICK_START.md**
