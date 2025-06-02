import java.util.ArrayList;
import java.util.List;
import java.io.File        ;
import java.util.Scanner   ;

public class GrapheMPM
{
    private ArrayList <TacheMPM> taches     ;

    public static void main(String[] args) 
    {
        System.out.println("Bienvenue dans l'application de gestion de projet MPM");
        new GrapheMPM();                                                        ;
    }

    public GrapheMPM()
    {
        this.taches     = new ArrayList<TacheMPM>() ;
        this.lireFichier();
        this.setDateTot ();
        this.setDateTard();
        System.out.println(this.toString());
    }

    private void setDateTot() 
    {
        for (TacheMPM tache : this.taches) 
        {
            if (!tache.getPrecedents().isEmpty()) 
            {
                int maxFinPrecedent = 0;
                for (TacheMPM precedent : tache.getPrecedents()) 
                {
                    int finPrecedent = Integer.parseInt(precedent.getDateTot()) + precedent.getDuree();
                    if (finPrecedent > maxFinPrecedent)
                        maxFinPrecedent = finPrecedent;
                }
                tache.setDateTot(String.valueOf(maxFinPrecedent));
            }
        }
    }

    private void setDateTard() 
    {
        for (TacheMPM tache : this.taches) 
        {
            if (tache.getSuivants().isEmpty()) 
            {
                tache.setDateTard(tache.getDateTot());
            } 
            else 
            {
                int minDebutSuivant = 0;
                for (TacheMPM suivant : tache.getSuivants()) 
                {
                    int debutSuivant = Integer.parseInt(suivant.getDateTot());
                    if (debutSuivant < minDebutSuivant)
                        minDebutSuivant = debutSuivant;
                }
                tache.setDateTard(String.valueOf(minDebutSuivant - tache.getDuree()));
            }
        }
    }

    public void ajouterTache(TacheMPM tache)
    {
        if (tache != null) 
        {
            this.taches.add(tache) ;
            return                 ;
        }

        System.out.println("Erreur : la tâche à ajouter est nulle.");
    }

    public void initSuivants()
    {
        for (TacheMPM tache : this.taches) 
        {
            List<TacheMPM> suivants = new ArrayList<>();
            for (TacheMPM autreTache : this.taches) 
            {
                if (autreTache != tache) 
                {
                    for (TacheMPM precedent : autreTache.getPrecedents()) 
                    {
                        if (precedent.getNom().equals(tache.getNom())) 
                        {
                            suivants.add(autreTache) ;
                            break                    ;
                        }
                    }
                }
            }
            tache.setSuivants(suivants);
        }
    }

    private void lireFichier()
    {
        Scanner  scMPM      ;
        String   ligne      ;
        String   nom        ;
        int      duree      ;
        String[] precedents ;

        try
        {
            scMPM = new Scanner(new File("listeTache.txt"), "UTF-8");

            while (scMPM.hasNextLine())
            {
                ligne = scMPM.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] parties = ligne.split("\\|", -1); // Utiliser -1 pour conserver les champs vides

                nom   = parties[0]                  ;
                duree = Integer.parseInt(parties[1]);

                if (parties.length > 2 && !parties[2].isEmpty()) precedents = parties[2].split(",") ; 
                else                                             precedents = new String[0]         ;

                // Création et ajout des tâches
                List<TacheMPM> tachesPrecedentes = new ArrayList<TacheMPM>();

                for (int i = 0; i < precedents.length; i++) 
                    tachesPrecedentes.add(this.trouverTache(precedents[i].trim()));
                
                TacheMPM tache = new TacheMPM(nom, duree, tachesPrecedentes);
                this.ajouterTache(tache)                          ;
            }
        }
        catch ( Exception e ) { e.printStackTrace(); }

        this.initSuivants();
    }

    private TacheMPM trouverTache(String nom) {
        for (TacheMPM tache : this.taches) {
            if (tache.getNom().equals(nom)) {
                return tache;
            }
        }
        return null;
    }

    public ArrayList<TacheMPM> getTaches() { return taches; }

    public String toString()
    {
        StringBuilder sb = new StringBuilder() ;
        sb.append("Graphe MPM:\n")             ;
        for (TacheMPM tache : taches) 
            sb.append(tache.toString()).append("\n");
        
        return sb.toString();
    }


}
