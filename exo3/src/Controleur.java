package src;
import java.util.List;
import src.Ihm.FrameMPM;
import src.Ihm.IhmCui;
import src.Metier.Fichier;
import src.Metier.GrapheMPM;
import src.Metier.TacheMPM;
import src.utils.DateUtils;


public class Controleur 
{
    private GrapheMPM graphe;
    private String    dateRef;
    private char      typeDate;

    private FrameMPM  frame;
    private Fichier   fichier;

    public static void main(String[] args) { new Controleur(); }

    public Controleur() 
    {
        this.graphe = new GrapheMPM(this);
        this.frame  = new FrameMPM(this, this.graphe);
    }

    public void initialiserProjet(String dateRef, char typeDate, String nomFichier) 
    {
        System.out.println("Initialisation du projet avec la date de référence : " + dateRef + " et le type de date : " + typeDate);
        this.fichier  = new Fichier(this.graphe, nomFichier); 

        this.dateRef  = dateRef ;
        this.typeDate = typeDate;

        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setTypeDate  (typeDate);

        this.graphe.initSuivants ()        ;
        this.graphe.calculerDates()        ;
        this.graphe.initCheminCritique();
        this.graphe.initNiveauTaches();
        this.frame.changerPanel();
        IhmCui ihm     = new IhmCui   (this);
    }
    
    public List<TacheMPM> getTaches() { return this.fichier.getLstTacheMPMs();                 }
    public String getDateDuJour    () { return DateUtils.getDateDuJour   ();                   }
    public String getGrapheString  () { return this.graphe.toString      ();                   }
    public String getDateReference () { return this.dateRef                ;                   }
    public int    getDureeProjet   () { return this.graphe.getDureeProjet();                   }
    public String getDateProjet    () { return this.graphe.getDateProjet (this.typeDate);      }
    public int    getNiveauTaches (TacheMPM tache)  { return this.graphe.getNiveauTache(tache);}
    public int[]  getNiveauxTaches ()               { return this.graphe.getNiveaux();         }
    public void   resetPositions() { this.frame.resetPositions(); this.frame.repaint(); }
}