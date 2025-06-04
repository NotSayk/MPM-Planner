package src;
import src.Ihm.FrameMPM;
import src.Ihm.IhmCui;
import src.Metier.Fichier;
import src.Metier.GrapheMPM;
import src.Metier.TacheMPM;


public class Controleur 
{
    private GrapheMPM graphe;
    private String    dateRef;
    private char      typeDate;

    private FrameMPM frame;
    private Fichier fichier;

    public static void main(String[] args) { new Controleur(); }

    public Controleur() 
    {
        this.graphe = new GrapheMPM()    ;
        this.frame  = new FrameMPM(this, this.graphe);
    }

    public void initialiserProjet(String dateRef, char typeDate) 
    {
        System.out.println("Initialisation du projet avec la date de référence : " + dateRef + " et le type de date : " + typeDate);
        this.dateRef  = dateRef ;
        this.typeDate = typeDate;
        this.fichier  = new Fichier(this.graphe); 

        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setTypeDate  (typeDate);

        this.graphe.initSuivants ()        ;
        this.graphe.calculerDates()        ;
        this.graphe.initCheminCritique();
        this.graphe.calculNiveauTaches();
        this.frame.changerPanel();
        IhmCui ihm     = new IhmCui   (this);
    }

    public String getDateDuJour   () { return GrapheMPM.getDateDuJour   ();              }
    public String getGrapheString () { return this.graphe.toString      ();              }
    public String getDateReference() { return this.dateRef                ;              }
    public int    getDureeProjet  () { return this.graphe.getDureeProjet();              }
    public String getDateProjet   () { return this.graphe.getDateProjet (this.typeDate);     }
    public int    getNiveauTaches(TacheMPM tache)  { return this.graphe.getNiveauTache(tache);}
    public int[]  getNiveauxTaches()               { return this.graphe.getNiveaux(); }
    public void   resetPositions() { this.frame.resetPositions(); this.frame.repaint(); }
}