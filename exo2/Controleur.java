import src.metier.GrapheMPM;


public class Controleur 
{
    private GrapheMPM graphe;
    private String    dateRef;
    private char      typeDate;

    public static void main(String[] args) { new Controleur(); }

    public Controleur() 
    {
        this.graphe = new GrapheMPM()    ;
        IhmCui ihm  = new IhmCui   (this);
    }

    public void initialiserProjet(String dateRef, char typeDate) 
    {
        this.dateRef  = dateRef ;
        this.typeDate = typeDate;
        
        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setTypeDate  (typeDate);
        this.graphe.lireFichier  ()        ;
        this.graphe.calculerDates()        ;
    }

    public String getDateDuJour   () { return GrapheMPM.getDateDuJour   ();              }
    public String getGrapheString () { return this.graphe.toString      ();              }
    public String getDateReference() { return this.dateRef                ;              }
    public int    getDureeProjet  () { return this.graphe.getDureeProjet();              }
    public String getDateProjet   () { return this.graphe.getDateProjet (this.typeDate); }
}