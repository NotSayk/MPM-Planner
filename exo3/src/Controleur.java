package src;

import java.util.List;
import src.ihm.FrameCritique;
import src.ihm.FrameMPM;
import src.ihm.FrameModification;
import src.ihm.GrilleDonneesModel;
import src.ihm.composants.Entite;
import src.metier.CheminCritique;
import src.metier.GrapheMPM;
import src.metier.TacheMPM;
import src.utils.DateUtils;   

public class Controleur 
{
    /*-------------------------*
     * Attributs du contrôleur *
     *-------------------------*/
    private GrapheMPM         graphe;

    private FrameMPM          frameMPM;
    private FrameModification frameModification;
    private FrameCritique     frameCritique;

    /*-----------------------------*
     * Point d'entrée du programme *
     *-----------------------------*/
    public static void main(String[] args) { new Controleur(); }

    /*-------------------------*
     * Constructeurs principal *
     *-------------------------*/
    public Controleur() 
    {
        this.graphe   = new GrapheMPM();
        this.frameMPM = new FrameMPM (this);
    }

    /*--------------------------*
     * Initialisation du projet *
     *--------------------------*/
    public void initProjet(String dateRef, char typeDate, String nomFichier) 
    {
        this.graphe.initTache(nomFichier);

        this.graphe.setDateRef(dateRef);
        this.graphe.setDateType(typeDate);
        this.graphe.calculerDates();
        
        if (typeDate == 'F') this.graphe.setDateFin(dateRef);
        
        this.graphe.initCheminCritique();
        this.graphe.initNiveauTaches();
        this.afficherGraphe();
    }

    public void initComplet(char typeDate, String nomFichier) 
    {
        this.initProjet(this.getDateRef(), typeDate, nomFichier);
        this.afficherGraphe();
        
        this.frameMPM.setTheme(this.getTheme());
        this.frameMPM.setCritique(this.isCritique());
        this.graphe.chargerEntites(nomFichier, this.getEntites());
    }

    /*--------------------------*
     * Affichage des interfaces *
     *--------------------------*/
    public void afficherGraphe() { this.frameMPM.changerPanel(); }

    public void afficherModification()
    {
        if (this.frameModification == null) 
            this.frameModification = new FrameModification(this);
        this.frameModification.setVisible(true);
    }   

    public void cacherModification()
    {
        if (this.frameModification != null) 
            this.frameModification.setVisible(false);
    }

    /*---------------------------*
     * Actions utilisateur - IHM *
     *---------------------------*/
    public void ajouterTacheAPosition(TacheMPM tache, int position) 
    {
        this.graphe.ajouterTacheAPosition(tache, position);
        this.afficherGraphe(); 
        this.afficherCheminCritique(this.isCritique());
    }

    public void afficherCritiques()
    {
        if (this.frameCritique == null)
            this.frameCritique = new FrameCritique(this);
        
        this.frameCritique.setVisible(true);
    }

    public void changerTheme()       { this.frameMPM.changerTheme();                     }
    public void resetPositions()     { this.frameMPM.resetPositions(); 
                                       this.frameMPM.repaint();                          }
    public void sauvegarder()        { this.graphe.sauvegarder();                       }
    public void chargerFichier()     { this.graphe.chargerFichierB(this);                   }
    public void copierTache()        
    { 
        this.graphe.copierTache(this.getFrameMPM().getTacheSelectionnee());   
        this.getGrilleDonneesModel().refreshTab();
    }
    public void collerTache()        { this.graphe.collerTache(); this.afficherGraphe(); }
    public TacheMPM trouverTache(String nomTache)      { return this.graphe.trouverTache(nomTache);                      }
    public void modifierTacheFichier(TacheMPM tache) 
    { 
        this.graphe.modifierTacheFichier(tache); 
    }

    public void sauvegarderFichier() 
    { 
        this.graphe.sauvegarderFichier(this.getTheme(), 
                                        this.isCritique(),
                                        this.getDateRef(),  
                                        this.frameMPM.getPanelMPM()); 
    }

    public void supprimerTacheFichier(TacheMPM tache) 
    { 
        this.graphe.supprimerTacheFichier(tache); 
    }

    public void mettreAJourDureeTache(int index, int duree) 
    { 
        if(this.graphe.mettreAJourDureeTache(index, duree))
        {        
            this.afficherGraphe();
            this.setTheme(this.getTheme());
            this.afficherCheminCritique(this.isCritique());
            this.getFrameMPM().getPanelMPM().setScale(this.getFrameMPM().getScale());
        }
    }

    public void modifierPrecedents(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.graphe.modifierPrecedents(tacheModifier, nouvelleValeur); 
    }

    public void modifierSuivants(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.graphe.modifierSuivants(tacheModifier, nouvelleValeur); 
        this.initProjet(this.getDateRef(), this.getDateType(), this.getNomFichier());
    }

    public void modifierNom(TacheMPM tacheModifier, String nouvelleValeur) 
    {
        this.graphe.modifierNom(tacheModifier, nouvelleValeur);
        this.initProjet(this.getDateRef(), this.getDateType(), this.getNomFichier());
    }

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
    public void   setFormatDateTexte(boolean format)         { this.graphe.setFormatDateTexte(format);     }
    public void   setNiveauTache(TacheMPM tache, int niveau) { this.graphe.setNiveauTache(tache, niveau);   }

    public String getDateDuJour()     { return DateUtils.getDateDuJour();     }
    public String getDateRef()        { return this.graphe.getDateRef();      }
    public String getGrapheToString() { return this.graphe.toString();        }
    
    public char   getDateType()       { return this.graphe.getDateType();     }
    
    public int    getDureeProjet()    { return this.graphe.getDureeProjet();  }
    public int    getNiveauTache(TacheMPM tache) { return this.graphe.getNiveauTache(tache); }
    public int[]  getNiveauxTaches()  { return this.graphe.getNiveaux();      }
    
    public boolean isFormatDateTexte() { return this.graphe.isFormatDateTexte(); }
    public boolean getAfficher()       { return this.frameMPM.getPanelMPM().isCritique(); }

    public List<CheminCritique> getCheminsCritiques() { return this.graphe.getCheminsCritiques(); }

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

    /*--------------------------------------------*
     * Accesseurs - Données du graphe et de l'IHM *
     *--------------------------------------------*/
    public List<TacheMPM> getTaches()  { return this.graphe.getTaches();    }
    public List<Entite>   getEntites() { return this.frameMPM.getEntites(); }

    /*---------------------------*
     * Accesseurs - État général *
     *---------------------------*/
    public TacheMPM       getTacheSelectionnee() { return this.frameMPM.getTacheSelectionnee();  }
    public FrameMPM       getFrameMPM()          { return this.frameMPM;                         }
    public GrapheMPM      getGraphe()            { return this.graphe;                           }

    public String   getTheme() { return this.graphe.getTheme(); }
    public boolean  isCritique() { return this.graphe.isCritique(); }
    public String getNomFichier() { return this.graphe.getNomFichier(); }
}