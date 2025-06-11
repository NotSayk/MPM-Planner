package src;

import java.util.List;
import src.ihm.FrameCritique;
import src.ihm.FrameMPM;
import src.ihm.FrameModification;
import src.ihm.GrilleDonneesModel;
import src.ihm.composants.Entite;
import src.metier.CheminCritique;
import src.metier.Fichier;
import src.metier.GrapheMPM;
import src.metier.TacheMPM;
import src.utils.DateUtils;   

/**
 * Classe principale du contrôleur de l'application MPM
 * 
 * Cette classe est le cœur de l'application. Elle :
 * - Coordonne les interactions entre l'interface utilisateur et le modèle
 * - Gère les actions utilisateur (ajout, modification, suppression de tâches)
 * - Contrôle l'affichage des différentes fenêtres
 * - Assure la persistance des données via la classe Fichier
 */
public class Controleur 
{
    /*-------------------------*
     * Attributs du contrôleur *
     *-------------------------*/
    private FrameMPM          frameMPM;          // Fenêtre principale de l'application
    private FrameModification frameModification; // Fenêtre de modification des tâches
    private FrameCritique     frameCritique;     // Fenêtre d'affichage des chemins critiques

    private GrapheMPM         grapheMPM;         // Modèle du graphe MPM
    private Fichier           fichier;           // Gestionnaire de fichiers
    private boolean           formatDateTexte = false; // Format d'affichage des dates

    /*-----------------------------*
     * Point d'entrée du programme *
     *-----------------------------*/
    public static void main(String[] args) { new Controleur(); }

    /*-------------------------*
     * Constructeurs principal *
     *-------------------------*/
    /**
     * Initialise le contrôleur et crée la fenêtre principale
     */
    public Controleur() 
    {
        this.grapheMPM = new GrapheMPM();
        this.frameMPM  = new FrameMPM(this);
    }

    /*--------------------------*
     * Initialisation du projet *
     *--------------------------*/
    /**
     * Initialise un nouveau projet avec les paramètres spécifiés
     * 
     * @param dateRef Date de référence du projet
     * @param typeDate Type de date (Début/Fin)
     * @param nomFichier Nom du fichier de sauvegarde
     */
    public void initProjet(String dateRef, char typeDate, String nomFichier) 
    {
        this.fichier = new Fichier(this, nomFichier); 

        this.grapheMPM.setDateRef(dateRef);
        this.grapheMPM.setDateType(typeDate);
        this.grapheMPM.calculerDates();
        
        if (typeDate == 'F') this.grapheMPM.setDateFin(dateRef);
        
        this.grapheMPM.initCheminCritique();
        this.grapheMPM.initNiveauTaches();
        this.afficherGraphe();
    }

    /**
     * Initialise un projet complet avec tous les paramètres
     * 
     * @param typeDate Type de date (Début/Fin)
     * @param nomFichier Nom du fichier de sauvegarde
     */
    public void initComplet(char typeDate, String nomFichier) 
    {
        this.initProjet(this.getDateRef(), typeDate, nomFichier);
        this.afficherGraphe();
        
        this.frameMPM .setTheme(this.fichier.getTheme());
        this.frameMPM.getPanelMPM().setCritique(this.fichier.isCritique());
    }

    /*--------------------------*
     * Affichage des interfaces *
     *--------------------------*/
    /**
     * Rafraîchit l'affichage du graphe
     */
    public void afficherGraphe() { this.frameMPM.changerPanel(); }

    /**
     * Affiche la fenêtre de modification des tâches
     */
    public void afficherModification()
    {
        if (this.frameModification == null) 
            this.frameModification = new FrameModification(this);
        this.frameModification.setVisible(true);
    }   

    /**
     * Cache la fenêtre de modification des tâches
     */
    public void cacherModification()
    {
        if (this.frameModification != null) 
            this.frameModification.setVisible(false);
    }

    /*---------------------------*
     * Actions utilisateur - IHM *
     *---------------------------*/
    /**
     * Ajoute une tâche à une position spécifique dans le graphe
     * 
     * @param tache La tâche à ajouter
     * @param position Position où insérer la tâche
     */
    public void ajouterTacheAPosition(TacheMPM tache, int position) 
    {
        this.grapheMPM.ajouterTacheAPosition(tache, position);
        this.fichier.ajouterTacheFichier(tache);
        this.frameMPM.getPanelMPM().initEntitesTest();
        this.afficherGraphe();
    }

    /**
     * Affiche la fenêtre des chemins critiques
     */
    public void afficherCritiques()
    {
        if (this.frameCritique == null)
            this.frameCritique = new FrameCritique(this);
        
        this.frameCritique.setVisible(true);
    }

    /**
     * Change le thème de l'interface
     */
    public void changerTheme() { this.frameMPM.changerTheme(); }

    /**
     * Réinitialise les positions des tâches dans le graphe
     */
    public void resetPositions() 
    { 
        this.frameMPM.resetPositions(); 
        this.frameMPM.repaint(); 
    }

    /**
     * Sauvegarde l'état actuel du projet
     */
    public void sauvegarder() { this.fichier.sauvegarder(); }

    /**
     * Ouvre un dialogue pour charger un fichier
     */
    public void chargerFichier() { this.fichier.chargerFichierB(); }

    /**
     * Copie la tâche sélectionnée pour la coller plus tard
     */
    public void copierTache() 
    { 
        this.grapheMPM.copierTache();
        this.getGrilleDonneesModel().refreshTab();
    }

    /**
     * Colle la tâche précédemment copiée
     */
    public void collerTache() 
    { 
        this.grapheMPM.collerTache();
        this.afficherGraphe();
    }

    /**
     * Ouvre un dialogue pour rechercher une tâche
     */
    public void chercherTache() 
    { 
        this.grapheMPM.chercherTache();
        this.frameMPM.setTacheSelectionnee(this.grapheMPM.getTacheSelectionnee());
    }

    /**
     * Sauvegarde le projet dans un fichier
     */
    public void sauvegarderFichier() 
    { 
        this.fichier.sauvegarderFichier(this.getTheme(), 
                                        this.isCritique(),
                                        this.getDateRef(),  
                                        this.frameMPM.getPanelMPM()); 
    }

    /**
     * Met à jour la durée d'une tâche
     * 
     * @param index Index de la tâche à modifier
     * @param duree Nouvelle durée
     */
    public void mettreAJourDureeTache(int index, int duree) 
    { 
        this.grapheMPM.mettreAJourDureeTache(index, duree);
        this.fichier.modifierTacheFichier(this.grapheMPM.getTaches().get(index));
        this.afficherGraphe();
    }

    /**
     * Modifie les prédécesseurs d'une tâche
     * 
     * @param tacheModifier Tâche à modifier
     * @param nouvelleValeur Nouvelle liste de prédécesseurs (noms séparés par des virgules)
     */
    public void modifierPrecedents(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.grapheMPM.modifierPrecedents(tacheModifier, nouvelleValeur);
        this.fichier.modifierTacheFichier(tacheModifier);
        this.initProjet(this.getDateRef(), this.getDateType(), this.fichier.getNomFichier());
    }

    /**
     * Modifie les successeurs d'une tâche
     * 
     * @param tacheModifier Tâche à modifier
     * @param nouvelleValeur Nouvelle liste de successeurs (noms séparés par des virgules)
     */
    public void modifierSuivants(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.grapheMPM.modifierSuivants(tacheModifier, nouvelleValeur);
        this.fichier.modifierTacheFichier(tacheModifier);
        this.initProjet(this.getDateRef(), this.getDateType(), this.fichier.getNomFichier());
    }

    /**
     * Modifie le nom d'une tâche
     * 
     * @param tacheModifier Tâche à modifier
     * @param nouvelleValeur Nouveau nom
     */
    public void modifierNom(TacheMPM tacheModifier, String nouvelleValeur) 
    {
        this.grapheMPM.modifierNom(tacheModifier, nouvelleValeur);
        this.fichier.modifierTacheFichier(tacheModifier);
        this.initProjet(this.getDateRef(), this.getDateType(), this.fichier.getNomFichier());
    }

    /**
     * Change le format d'affichage des dates (texte/nombre)
     */
    public void changerAffichage()
    {
        this.setFormatDateTexte(!this.isFormatDateTexte());
        this.getFrameMPM().repaint();
    } 

    /*-----------------------------------------*
     * Accesseurs - Informations sur le projet *
     *-----------------------------------------*/
    public void   setZoom(double zoom)                       { this.frameMPM.getPanelMPM().setScale(zoom);  }
    public void   setTheme(String theme)                     { this.frameMPM.setTheme(theme);               }
    public void   setFormatDateTexte(boolean format)         { this.formatDateTexte = format;               }
    public void   setNiveauTache(TacheMPM tache, int niveau) { this.grapheMPM.setNiveauTache(tache, niveau);   }

    public String getDateDuJour()     { return DateUtils.getDateDuJour();     }
    public String getTheme()          { return this.frameMPM.getTheme();      }
    public String getDateRef()        { return this.grapheMPM.getDateRef();      }
    public String getGrapheToString() { return this.grapheMPM.toString();        }
    
    public char   getDateType()       { return this.grapheMPM.getDateType();     }
    
    public int    getDureeProjet()    { return this.grapheMPM.getDureeProjet();  }
    public int    getNiveauTache(TacheMPM tache) { return this.grapheMPM.getNiveauTache(tache); }
    public int[]  getNiveauxTaches()  { return this.grapheMPM.getNiveaux();      }
    
    public boolean isFormatDateTexte() { return this.formatDateTexte;         }
    public boolean isCritique()        { return this.frameMPM.getPanelMPM().isCritique();   }
    public boolean getAfficher()       { return this.frameMPM.getPanelMPM().isCritique(); }

    public List<CheminCritique> getCheminsCritiques() { return this.grapheMPM.getCheminsCritiques(); }

    public void afficherCheminCritique(boolean afficher) 
    { 
        this.frameMPM.getPanelMPM().afficherCheminCritique(afficher); 
    }

    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        if (this.frameModification == null) 
            this.frameModification = new FrameModification(this);
        
        return this.frameModification.getGrilleDonneesModel();
    }

    public List<TacheMPM> getTaches()  { return this.grapheMPM.getTaches();    }
    public List<Entite>   getEntites() { return this.frameMPM.getPanelMPM().getEntites(); }
    
    public TacheMPM       getTacheSelectionnee() { return this.frameMPM.getPanelMPM().getTacheSelectionnee();  }
    public FrameMPM       getFrameMPM()          { return this.frameMPM;                         }
    public GrapheMPM      getGraphe()            { return this.grapheMPM;                           }
    public Fichier        getFichier()           { return this.fichier;                          }
}