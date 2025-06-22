# Projet MPM (Méthode des Potentiels Métra)


## 🚀 Installation et lancement

### Windows
```batch
# Option 1: Double-cliquer sur start_win.bat
# Option 2: Via ligne de commande
cd MPM
start_win.bat
```

### macOS
```bash
cd MPM
chmod +x start_mac.bat
./start_mac.bat
```

### Linux
```bash
cd MPM
chmod +x start_linux.sh
./start_linux.sh
```

## 💾 Formats de fichiers pris en charge

- **`.MC`** : Format de sauvegarde complexe
- **`.data`** : Format de sauvegarde simple


## 🗂️ Structure du projet

```
MPM/
├── bin/                  # Fichiers compilés (.class)
├── src/                  # Code source
│   ├── Controleur.java   # Point d'entrée et contrôleur principal
│   ├── IHM/              # Interface utilisateur
│   ├── Metier/           # Logique métier et algorithmes
│   └── Utils/            # Classes utilitaires
├── start_win.bat         # Script de lancement Windows
├── start_mac.bat         # Script de lancement macOS
├── start_linux.sh        # Script de lancement Linux
├── compile.list          # Liste des fichiers à compiler
├── listeTache.MC         # Exemple de données format MC
└── listeTache.data       # Exemple de données format alternatif
```

## 👨‍💻 Détails techniques

### Composants principaux

- **GrapheMPM** : Gère la structure et le calcul du graphe MPM
- **TacheMPM** : Représente une tâche individuelle avec ses propriétés
- **CheminCritique** : Calcule et identifie le chemin critique du projet
- **FrameMPM** : Interface utilisateur principale
- **PanelGraphe** : Composant de visualisation du graphe

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