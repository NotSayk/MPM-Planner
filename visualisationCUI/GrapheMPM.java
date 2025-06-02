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

    public void defSuivants()
    {
        for (TacheMPM tache : taches) {
            List<TacheMPM> suivants = new ArrayList<>();
            for (TacheMPM autreTache : taches) {
                if (autreTache != tache) {
                    for (TacheMPM precedent : autreTache.getPrecedents()) {
                        if (precedent.getNom().equals(tache.getNom())) {
                            suivants.add(autreTache);
                            break;
                        }
                    }
                }
            }
            tache.setSuivants(suivants);
        }
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
