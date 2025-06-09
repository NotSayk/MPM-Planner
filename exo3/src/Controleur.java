package src;
import java.util.List;

import src.ihm.FrameMPM;
import src.ihm.FrameModification;
import src.ihm.composants.Entite;
import src.metier.Fichier;
import src.metier.GrapheMPM;
import src.metier.TacheMPM;
import src.utils.DateUtils;   


public class Controleur 
{
    
    private FrameMPM           frameMPM;
    private FrameModification  frameModification;

    private Fichier            fichier;
    private GrapheMPM          graphe;

    public static void main(String[] args) { new Controleur(); }

    public Controleur() 
    {
        this.graphe   = new GrapheMPM             (this);
        this.frameMPM = new FrameMPM (this, this.graphe);
    }

    // Initialisation du projet avec une date de référence et un type de date
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

    // Initialisation du projet avec un fichier existant
    public void initComplet(char typeDate, String nomFichier) 
    {
        this.initProjet(this.getDateRef(), typeDate, nomFichier);

        this.afficherGraphe();
        this.frameMPM.setTheme   ( this.fichier.getTheme  () );
        this.frameMPM.setCritique( this.fichier.isCritique() );

        this.graphe.chargerEntites( nomFichier );

    }

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
    

    public String  getDateDuJour     () { return DateUtils.getDateDuJour();                       }
    public String  getDateRef        () { return this.graphe.getDateRef ();                       }
    public char    getDateType       () { return this.graphe.getDateType();                       }
    public String  getTheme          () { return this.frameMPM.getTheme ();                       }
    
    public int     getDureeProjet    () { return this.graphe.getDureeProjet();                    }

    public int     getNiveauTache    (TacheMPM tache) { return this.graphe.getNiveauTache(tache); }
    public int[]   getNiveauxTaches  () { return this.graphe.getNiveaux();                        }
    
    public List<TacheMPM> getTaches  () { return this.graphe.getTaches();                         }
    public List<Entite>   getEntites () { return this.frameMPM.getEntites    ();                  }

    public boolean        isCritique () { return this.frameMPM.isCritique    ();                  }

    public FrameMPM       getFrameMPM() { return this.frameMPM;                                   }
    public GrapheMPM      getGraphe  () { return this.graphe;                                     }
    public Fichier        getFichier () { return this.fichier;                                    }

    public String  getGrapheToString () { return this.graphe.toString   ();                       }
}