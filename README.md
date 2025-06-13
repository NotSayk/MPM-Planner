# Projet MPM (Méthode des Potentiels Métra)

![MPM](https://img.shields.io/badge/Project-MPM-blue)
![Language](https://img.shields.io/badge/Language-Java-orange)

## 🚀 Installation et lancement

### Windows
```batch
# Option 1: Double-cliquer sur start_win.bat
# Option 2: Via ligne de commande
cd chemin\vers\MPM
start_win.bat
```

### macOS
```bash
cd chemin/vers/MPM
chmod +x start_mac.bat
./start_mac.bat
```

### Linux
```bash
cd chemin/vers/MPM
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

