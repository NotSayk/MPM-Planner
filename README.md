# Exercice 3 - Gestion de Projet MPM (Méthode des Potentiels Métra)

## Description
Ce projet est une application Java permettant de gérer des projets en utilisant la méthode MPM (Méthode des Potentiels Métra). Il permet de créer, visualiser et gérer des graphes de tâches avec leurs dépendances, calculer les dates au plus tôt et au plus tard, et identifier les chemins critiques.

## Architecture du Projet

### Structure des Dossiers
```
exo3/
├── src/
│   ├── metier/         # Classes métier (logique de l'application)
│   ├── ihm/            # Interface Homme-Machine
│   ├── utils/          # Utilitaires
│   └── Controleur.java # Point d'entrée de l'application
├── bin/                # Fichiers compilés
└── listeTache.MC      # Fichier de sauvegarde par défaut
```

### Composants Principaux

#### 1. Couche Métier (`src/metier/`)
- `GrapheMPM.java` : Classe principale gérant la logique du graphe MPM
  - Gestion des tâches et leurs relations
  - Calcul des dates au plus tôt et au plus tard
  - Identification des chemins critiques
  - Sauvegarde et chargement des données
- `TacheMPM.java` : Représente une tâche dans le graphe
- `CheminCritique.java` : Gère les chemins critiques du projet

#### 2. Interface Utilisateur (`src/ihm/`)
- `FrameMPM.java` : Fenêtre principale de l'application
- `PanelMPM.java` : Panneau d'affichage du graphe
- `PanelPara.java` : Panneau de paramètres
- `FrameModification.java` : Fenêtre de modification des tâches
- `FrameCritique.java` : Affichage des chemins critiques
- `BarreMenu.java` : Barre de menu de l'application

#### 3. Utilitaires (`src/utils/`)
- `DateUtils.java` : Gestion des dates
- `ErrorUtils.java` : Gestion des erreurs
- `Utils.java` : Fonctions utilitaires diverses

## Fonctionnalités Principales

### 1. Gestion des Tâches
- Création de nouvelles tâches
- Modification des tâches existantes
- Suppression de tâches
- Définition des relations de précédence
- Copier/Coller de tâches

### 2. Calculs MPM
- Calcul automatique des dates au plus tôt
- Calcul automatique des dates au plus tard
- Identification des chemins critiques
- Calcul des marges

### 3. Visualisation
- Affichage graphique du graphe MPM
- Zoom et déplacement dans le graphe
- Thèmes d'affichage (clair/sombre)
- Mise en évidence des chemins critiques

### 4. Persistance des Données
- Sauvegarde au format `.MC` (format complet)
- Sauvegarde au format `.data` (format simple)
- Chargement de projets existants

## Utilisation

### Démarrage
1. Exécuter le script approprié selon votre système d'exploitation :
   - Windows : `start_win.bat`
   - Linux : `start_linux.bat`
   - MacOS : `start_mac.bat`

### Création d'un Nouveau Projet
1. Cliquer sur "Fichier" > "Nouveau Projet"
2. Entrer le nom du projet
3. Le système crée automatiquement les tâches DEBUT et FIN

### Ajout d'une Tâche
1. Cliquer sur "Edition" > "Ajouter une tâche"
2. Remplir les informations de la tâche :
   - Nom
   - Durée
   - Tâches précédentes

### Modification d'une Tâche
1. Clic droit sur une tâche
2. Sélectionner l'action souhaitée :
   - Modifier la durée
   - Modifier les précédences
   - Supprimer la tâche

### Visualisation des Chemins Critiques
1. Cliquer sur "Informations" > "Chemins critiques"
2. Les chemins critiques sont mis en évidence en rouge

## Format des Fichiers

### Format Simple (.data)
```
nomTache|duree|precedents
```

### Format Complet (.MC)
```
nomTache|duree|precedents|positionX|positionY|dateTot|dateTard
theme
critique
```

## Dépendances
- Java 8 ou supérieur
- Swing (inclus dans le JDK)

## Compilation
Le projet utilise un fichier `compile.list` pour la compilation. Pour compiler :
```bash
javac @compile.list
``` 