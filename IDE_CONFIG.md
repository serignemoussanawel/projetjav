# Configuration IDE pour VS Code

Vous pouvez configurer VS Code avec les extensions suivantes pour améliorer l'expérience de développement :

## Extensions Recommandées

### Java
- **Extension Pack for Java** (Microsoft)
  - Inclut : Language Support, Debugger, Test Runner
  - ID: vscjava.vscode-java-pack

### Maven
- **Maven for Java** (Microsoft)
  - ID: vscjava.vscode-maven

### Visual Tools
- **GitLens** (Eric Amodio)
  - Meilleure vue des commits et branches
  - ID: eamodio.gitlens

## Configuration launch.json

Créer `.vscode/launch.json` avec :
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "JavaFX Application",
            "type": "java",
            "name": "Launch Main",
            "request": "launch",
            "mainClass": "com.campus.Main",
            "projectName": "campus-room-manager",
            "cwd": "${workspaceFolder}",
            "console": "integratedTerminal",
            "args": ""
        }
    ]
}
```

## Configuration tasks.json

Créer `.vscode/tasks.json` avec :
```json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Compile",
            "type": "shell",
            "command": "mvn",
            "args": ["clean", "compile"],
            "group": {
                "kind": "build",
                "isDefault": true
            },
            "problemMatcher": ["$msCompile"]
        },
        {
            "label": "Run Application",
            "type": "shell",
            "command": "mvn",
            "args": ["javafx:run"],
            "group": {
                "kind": "build"
            },
            "problemMatcher": []
        }
    ]
}
```

## Raccourcis Utiles

- `Ctrl+Shift+B` : Compiler le projet
- `Ctrl+F5` : Démarrer le débogage
- `F5` : Continuer l'exécution
- `Ctrl+Shift+D` : Ouvrir le panneau Debug
- `Ctrl+Shift+X` : Ouvrir les extensions

## Bonnes Pratiques

1. **Organisation du Code**
   - Utiliser des packages logiques
   - Mettre les modèles dans `models/`
   - Mettre les managers dans `managers/`
   - Mettre les UI dans `ui/`

2. **Convention de Nommage**
   - Classes : PascalCase (ex: LoginController)
   - Méthodes : camelCase (ex: handleLogin)
   - Constantes : UPPER_CASE (ex: MAX_SIZE)
   - Variables : camelCase (ex: userName)

3. **Structuration du Code**
   - Commentaires JavaDoc pour les classes publiques
   - Noms de variables explicites
   - Une responsabilité par classe

## Intégration avec IntelliJ IDEA

Si vous utilisez IntelliJ IDEA à la place de VS Code :
1. Ouvrir le projet
2. IntelliJ détectera automatiquement le fichier pom.xml
3. Laisser IntelliJ télécharger les dépendances Maven
4. Run > Run 'Main' pour démarrer l'application

Pour déboguer :
- Run > Debug 'Main'
- Ajouter des points d'arrêt en cliquant sur la marge de ligne
