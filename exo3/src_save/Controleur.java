package src_save;

import java.util.List;

import src_save.ihm.FrameMPM;
import src_save.ihm.FrameModification;
import src_save.ihm.composants.Entite;
import src_save.metier.Fichier;
import src_save.metier.GrapheMPM;
import src_save.metier.TacheMPM;
import src_save.utils.DateUtils;   


public class Controleur 
{
    /*-------------------------
     * Attributs du contrôleur
     *-------------------------*/

    private FrameMPM           frameMPM;
    private FrameModification  frameModification;

    private Fichier            fichier;
    private GrapheMPM          graphe;

    /*-----------------------------
     * Point d’entrée du programme
     *-----------------------------*/

    public static void main(String[] args) { new Controleur(); }

    /*-------------------------
     * Constructeurs principal
     *-------------------------*/

    public Controleur() 
    {
        this.graphe   = new GrapheMPM             (this);
        this.frameMPM = new FrameMPM (this, this.graphe);
    }

    /*--------------------------
     * Initialisation du projet
     *--------------------------*/

    // Initialisation avec une date de référence
    public void initProjet(String dateRef, char typeDate, String nomFichier) 
    {
        this.fichier  = new Fichier(this, nomFichier); 

        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setDateType  (typeDate);

        this.graphe.calculerDates     ();
        this.graphe.initCheminCritique();
        this.graphe.initNiveauTaches  ();

        this.afficherGraphe();
    }

    // Initialisation complète avec chargement des entités
    public void initComplet(char typeDate, String nomFichier) 
    {
        this.initProjet(this.getDateRef(), typeDate, nomFichier);

        this.afficherGraphe();
        this.frameMPM.setTheme   ( this.fichier.getTheme  () );
        this.frameMPM.setCritique( this.fichier.isCritique() );

        this.graphe.chargerEntites( nomFichier );
    }

    /*--------------------------
     * Affichage des interfaces
     *--------------------------*/

    public void afficherGraphe() { this.frameMPM.changerPanel(); }

    public void afficherModification()
    {
        if(this.frameModification == null) this.frameModification = new FrameModification(this);
        this.frameModification.setVisible(true);
    }   

    public void cacherModification() 
    {
        if(this.frameModification != null) this.frameModification.setVisible(false);
    }

    /*---------------------------
     * Actions utilisateur - IHM
     *---------------------------*/

    public void changerTheme       () { this.frameMPM.changerTheme  ();                               }
    public void resetPositions     () { this.frameMPM.resetPositions(); this.frameMPM.repaint();      }
    public void sauvegarder        () { this.fichier.sauvegarder    ();                               }

    public void sauvegarderFichier () { this.fichier.sauvegarderFichier(this.getTheme  (),
                                                                        this.isCritique(),
                                                                        this.getDateRef(),  
                                                                        this.frameMPM.getPanelMPM()); }

    public void mettreAJourDureeTache (int index, int duree) { this.graphe.mettreAJourDureeTache(index, duree); }

    public void modifierPrecedents(TacheMPM tacheModifier, String nouvelleValeur) { this.graphe.modifierPrecedents(tacheModifier, nouvelleValeur); }

    public void modifierSuivants  (TacheMPM tacheModifier, String nouvelleValeur) { this.graphe.modifierSuivants(tacheModifier, nouvelleValeur); }

    /*-----------------------------------------
     * Accesseurs - Informations sur le projet
     *-----------------------------------------*/

    public String  getDateDuJour     () { return DateUtils.getDateDuJour   ();                    }

    public String  getTheme          () { return this.frameMPM.getTheme    ();                    }
    public String  getDateRef        () { return this.graphe.getDateRef    ();                    }
    public char    getDateType       () { return this.graphe.getDateType   ();                    }
    public int[]   getNiveauxTaches  () { return this.graphe.getNiveaux    ();                    }
    public int     getDureeProjet    () { return this.graphe.getDureeProjet();                    }

    public int     getNiveauTache    (TacheMPM tache) { return this.graphe.getNiveauTache(tache); }

    /*--------------------------------------------
     * Accesseurs - Données du graphe et de l’IHM
     *--------------------------------------------*/

    public List<TacheMPM> getTaches  () { return this.graphe.getTaches   (); }
    public List<Entite>   getEntites () { return this.frameMPM.getEntites(); }

    /*---------------------------
     * Accesseurs - État général
     *---------------------------*/

    public boolean        isCritique () { return this.frameMPM.isCritique(); }
    public FrameMPM       getFrameMPM() { return this.frameMPM;              }
    public GrapheMPM      getGraphe  () { return this.graphe;                }
    public Fichier        getFichier () { return this.fichier;               }

    public String  getGrapheToString () { return this.graphe.toString    (); }
}
