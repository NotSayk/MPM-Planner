package src;
import java.util.ArrayList;
import java.util.List;
import src.Ihm.FrameMPM;
import src.Ihm.FrameModification;
import src.Ihm.IhmCui;
import src.Ihm.composants.Entite;
import src.Metier.Fichier;
import src.Metier.GrapheMPM;
import src.Metier.TacheMPM;
import src.utils.DateUtils;


public class Controleur 
{
    private GrapheMPM          graphe;
    private String             dateRef;
    private char               typeDate;

    private FrameMPM           frameMPM;
    private FrameModification  frameModification;

    private Fichier            fichier;

    public static void main(String[] args) { new Controleur(); }

    public Controleur() 
    {
        this.graphe    = new GrapheMPM(this);
        this.frameMPM = new FrameMPM(this, this.graphe);
    }

    public void initialiserProjet(String dateRef, char typeDate, String nomFichier) 
    {
        this.fichier  = new Fichier(this.graphe, nomFichier); 

        this.dateRef  = dateRef ;
        this.typeDate = typeDate;

        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setTypeDate  (typeDate);

        this.graphe.initSuivants      ();
        this.graphe.calculerDates     ();
        this.graphe.initCheminCritique();
        this.graphe.initNiveauTaches  ();

        this.afficherGraphe();
        IhmCui ihm     = new IhmCui   (this);
    }

    public void initComplet(char typeDate, String nomFichier) 
    {
        this.fichier  = new Fichier(this.graphe, nomFichier); 

        this.typeDate = typeDate;

        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setTypeDate  (typeDate);

        this.graphe.initSuivants      ();
        this.graphe.calculerDates     ();
        this.graphe.initCheminCritique();
        this.graphe.initNiveauTaches  ();

        this.afficherGraphe();


        for(Entite e : getEntites())
        {
            int[] pos = fichier.getLocation(e.getTache(), nomFichier);
            System.out.println("x : " + pos[0] + " ; y : " + pos[1]);
            e.setPosition(pos[0], pos[1]);
        }

        IhmCui ihm     = new IhmCui   (this);
    }

    public void afficherGraphe() 
    {
        this.frameMPM.changerPanel();
    }

    public void afficherModification()
    {
        if(this.frameModification == null) 
            this.frameModification = new FrameModification(this, this.graphe);
        this.frameModification.setVisible(true);
    }   

    public void cacherModification() 
    {
        if(this.frameModification != null) 
            this.frameModification.setVisible(false);
    }

    public void changerTheme()
    {
        this.frameMPM.changerTheme();
    }


    public void   resetPositions   () { this.frameMPM.resetPositions(); this.frameMPM.repaint(); }
    
    public void   sauvegarder      () { this.fichier.sauvegarder();                            }

    public void   mettreAJourDureeTache (int index, int duree) 
    {
        List<TacheMPM> taches = this.fichier.getLstTacheMPMs();
        if (index >= 0 && index < taches.size()) 
        {
            TacheMPM tache = taches.get(index);
            tache.setDuree(duree);
            this.fichier.modifierTacheFichier(tache);
            initialiserProjet(dateRef, typeDate, fichier.getNomFichier());
        } 
        else 
        {
            System.err.println("Index de tâche invalide : " + index);
        }
    }

    public List<TacheMPM> getTaches() { return this.fichier.getLstTacheMPMs();                 }
    public String getDateDuJour    () { return DateUtils.getDateDuJour   ();                   }
    public String getGrapheString  () { return this.graphe.toString      ();                   }
    public String getDateReference () { return this.dateRef                ;                   }
    public char   getTypeDate      () { return this.typeDate              ;                   }
    public int    getDureeProjet   () { return this.graphe.getDureeProjet();                   }
    public String getDateProjet    () { return this.graphe.getDateProjet (this.typeDate);      }
    public int    getNiveauTaches (TacheMPM tache)  { return this.graphe.getNiveauTache(tache);}
    public int[]  getNiveauxTaches ()               { return this.graphe.getNiveaux();         }
    public List<Entite> getEntites () { return this.frameMPM.getEntites(); }
}