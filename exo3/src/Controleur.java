package src;

import java.util.List;
import src.ihm.FrameMPM;
import src.ihm.FrameModification;
import src.ihm.GrilleDonneesModel;
import src.ihm.composants.Entite;
import src.metier.Fichier;
import src.metier.GrapheMPM;
import src.metier.TacheMPM;
import src.utils.DateUtils;   


public class Controleur 
{
    /*-------------------------*
     * Attributs du contrôleur *
     *-------------------------*/

    private FrameMPM           frameMPM;
    private FrameModification  frameModification;

    private Fichier            fichier;
    private GrapheMPM          graphe;

    // Gerer le format de date - texte
    private boolean formatDateTexte = false; 

    /*-----------------------------*
     * Point d’entrée du programme *
     *-----------------------------*/

    public static void main(String[] args) { new Controleur(); }

    /*-------------------------*
     * Constructeurs principal *
     *-------------------------*/

    public Controleur() 
    {
        this.graphe   = new GrapheMPM             (this);
        this.frameMPM = new FrameMPM (this, this.graphe);
    }

    /*--------------------------*
     * Initialisation du projet *
     *--------------------------*/

    // Initialisation avec une date de référence
    public void initProjet(String dateRef, char typeDate, String nomFichier) 
    {
        this.fichier  = new Fichier(this, nomFichier); 

        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setDateType  (typeDate);

        this.graphe.calculerDates     ();
        if (typeDate == 'F') this.graphe.setDateFin (dateRef);
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

    public void ajouterTacheAPosition(TacheMPM tache, int position) 
    {
        this.graphe.ajouterTacheAPosition(tache, position);
    }

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

    public int     getNiveauTache    (TacheMPM tache)             { return this.graphe.getNiveauTache(tache) ; }
    public void    setNiveauTache    (TacheMPM tache, int niveau) { this.graphe.setNiveauTache(tache, niveau); }
    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        if (this.frameModification != null) 
        {
            return this.frameModification.getGrilleDonneesModel();
        }
        
        this.frameModification = new FrameModification(this);

        return this.frameModification.getGrilleDonneesModel();
    }

    public void modifierNom(TacheMPM tacheModifier, String nouvelleValeur) 
    {
        this.graphe.modifierNom(tacheModifier, nouvelleValeur);
    }

    public void copierTache() { this.graphe.copierTache(); }
    public void collerTache() { this.graphe.collerTache(); }

    public boolean chercherTache(String nomTache) { return this.graphe.chercherTache(nomTache); }

    /*--------------------------------------------
     * Accesseurs - Données du graphe et de l’IHM
     *--------------------------------------------*/

    public List<TacheMPM> getTaches  () { return this.graphe.getTaches   (); }
    public List<Entite>   getEntites () { return this.frameMPM.getEntites(); }

    /*---------------------------
     * Accesseurs - État général
     *---------------------------*/

    public TacheMPM       getTacheSelectionnee() { return this.frameMPM.getTacheSelectionnee(); }
    public boolean        isCritique () { return this.frameMPM.isCritique(); }
    public FrameMPM       getFrameMPM() { return this.frameMPM;              }
    public GrapheMPM      getGraphe  () { return this.graphe;                }
    public Fichier        getFichier () { return this.fichier;               }
    public void           setTheme   (String theme) { this.frameMPM.setTheme(theme); }
    public boolean        getAfficher()  { return this.frameMPM.getPanelMPM().isCritique(); }
    public void           afficherCheminCritique(boolean afficher) { this.frameMPM.getPanelMPM().afficherCheminCritique(afficher); }

    public String         getGrapheToString ()               { return this.graphe.toString(); }
    public void           setFormatDateTexte(boolean format) { this.formatDateTexte = format; }
    public boolean        isFormatDateTexte()                { return this.formatDateTexte;   }
}

