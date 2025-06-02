import java.util.ArrayList;
import java.util.List;

public class GrapheMPM
{
    private ArrayList <TacheMPM> taches;
    Controleur controleur;

    public GrapheMPM(Controleur controleur)
    {
        this.controleur = controleur;
        this.taches = new ArrayList<TacheMPM>();
    }

    public void ajouterTache(TacheMPM tache)
    {
        if (tache == null) {
            System.out.println("Erreur : Tâche à ajouter est nulle.");
            return;
        }
        this.taches.add(tache);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Graphe MPM:\n");
        for (TacheMPM tache : taches) {
            sb.append(tache.toString()).append("\n");
        }
        return sb.toString();
    }

    public ArrayList<TacheMPM> getTaches() { return taches; }

}
