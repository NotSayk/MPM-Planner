import java.io.File;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Controleur 
{
    private GrapheMPM grapheMPM;
    public static void main(String[] args) 
    {
        System.out.println("Bienvenue dans l'application de gestion de projet MPM");
        new Controleur();
    }


    public Controleur() 
    {
        this.grapheMPM = new GrapheMPM(this);
        lireFichier();


        this.grapheMPM.defSuivants();

        System.out.println("\n"+this.grapheMPM.toString());


    }

    public void lireFichier()
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

                nom = parties[0];
                duree = Integer.parseInt(parties[1]);

                if (parties.length > 2 && !parties[2].isEmpty()) 
                    precedents = parties[2].split(","); 
                else 
                    precedents = new String[0];


                // Traiter les données lues
                List<TacheMPM> tachesPrecedentes = new ArrayList<TacheMPM>();
                for (int i = 0; i < precedents.length; i++) 
                {
                    tachesPrecedentes.add(new TacheMPM(precedents[i].trim(), 0, new ArrayList<TacheMPM>()));
                }
                TacheMPM tache = new TacheMPM(nom, duree, tachesPrecedentes);
                this.grapheMPM.ajouterTache(tache);
                }
        }
        catch ( Exception e ) { e.printStackTrace(); }
    }
    
}
