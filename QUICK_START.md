# DÉMARRAGE RAPIDE

## Campus Room Management System

Bienvenue! Voici comment lancer l'application en 3 étapes simples.

---

## ⚡ Lancement Rapide

### Étape 1: Compilez le projet
```bash
mvn clean compile
```

### Étape 2: Lancez l'application  
```bash
mvn javafx:run
```

### Étape 3: Connectez-vous
- **Email**: admin@univ.fr
- **Mot de passe**: admin123

---

## 🪟 Sur Windows (Plus Simple)

Créez un raccourci ou double-cliquez:
```batch
run.bat
```

C'est tout! L'application démarre automatiquement.

---

## 🐧 Sur Linux/Mac

```bash
chmod +x run.sh
./run.sh
```

---

## 📌 Prérequis (À Vérifier)

Avant de lancer, vérifiez que vous avez:

### 1. Java 17+
```bash
java -version
```
Doit afficher: `java version "17" ou supérieur`

### 2. Maven 3.6+
```bash
mvn -version
```
Doit afficher la version de Maven

**Si aucun ne fonctionne**, consultez **INSTALLATION.md**

---

## 🎨 Utiliser VS Code

### Lancer et Déboguer
1. Appuyez sur **F5**
2. L'application démarre en mode debug
3. Vous pouvez ajouter des points d'arrêt

### Compiler Uniquement
Appuyez sur **Ctrl+Shift+B** pour compiler sans lancer

### Regarder la Console
- Aucune erreur = succès
- Les données de test se chargent automatiquement

---

## 💡 Conseils

- **La première compilation** peut prendre quelques minutes (télécharge les dépendances)
- **Les données sont perdues** quand vous fermiez l'app (c'est normal en v1.0)
- **Tous les comptes sont actifs** pour tester différents rôles

---

## 🆘 Problèmes Courants

### "mvn is not recognized"
→ Maven n'est pas installé. Voir INSTALLATION.md

### "Cannot find symbol"  
→ Compilation échouée. Vérifiez Java 17+

### "Application doesn't start"
→ Vérifiez la console pour les erreurs

---

## 📚 Fichiers de Référence

- **README.md** - Vue complète
- **USER_GUIDE.md** - Comment utiliser
- **INSTALLATION.md** - Problèmes d'installation
- **PROJECT_STRUCTURE.md** - Architecture

---

## ✅ Checklist

- [ ] Java 17+ installé (`java -version`)
- [ ] Maven installé (`mvn -version`)  
- [ ] Projet téléchargé et extrait
- [ ] Terminal ouvert dans le dossier du projet
- [ ] `mvn javafx:run` a fonctionné
- [ ] L'interface JavaFX s'affiche
- [ ] J'ai pu me connecter avec admin@univ.fr

---

**Enjoy! 🚀**

Pour plus d'aide, consultez les fichiers .md du projet.
