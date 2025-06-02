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
        if (tache != null) 
        {
            this.taches.add(tache);
            return;
        }

        System.out.println("Erreur : la tâche à ajouter est nulle.");
    }

    public void initSuivants()
    {
        for (TacheMPM tache : taches) 
        {
            List<TacheMPM> suivants = new ArrayList<>();
            for (TacheMPM autreTache : taches) 
            {
                if (autreTache != tache) 
                {
                    for (TacheMPM precedent : autreTache.getPrecedents()) 
                    {
                        if (precedent.getNom().equals(tache.getNom())) 
                        {
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
        for (TacheMPM tache : taches)
            sb.append(tache.toString()).append("\n");
        
        return sb.toString();
    }

    public ArrayList<TacheMPM> getTaches() { return taches; }

}
