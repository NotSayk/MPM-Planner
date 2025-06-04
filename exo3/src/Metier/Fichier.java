package src.Metier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Fichier 
{

    private GrapheMPM graphe;
    
    public Fichier(GrapheMPM graphe)
    {
        this.graphe = graphe;
        this.lireFichier();
    }


    public void lireFichier()
    {
        Scanner  scMPM     ;
        String   ligne     ;
        String   nom       ;
        int      duree     ;
        String[] precedents;

        try
        {
            scMPM = new Scanner(new File("listeTache.txt"), "UTF-8");

            while (scMPM.hasNextLine())
            {
                ligne = scMPM.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] parties = ligne.split("\\|", -1);

                nom = parties[0];
                duree = Integer.parseInt(parties[1]);

                if (parties.length > 2 && !parties[2].isEmpty()) precedents = parties[2].split(","); 
                else precedents = new String[0];

                List<TacheMPM> tachesPrecedentes = new ArrayList<TacheMPM>();

                for (String precedent : precedents) {
                    tachesPrecedentes.add(this.graphe.trouverTache(precedent.trim()));
                }
                
                TacheMPM tache = new TacheMPM(nom, duree, tachesPrecedentes);
                this.graphe.ajouterTache(tache);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

}
