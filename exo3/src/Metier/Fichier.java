package src.Metier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Fichier 
{

    private GrapheMPM      graphe;
    private List<TacheMPM> lstTacheMPMs = new ArrayList<TacheMPM>();
    
    public Fichier(GrapheMPM graphe)
    {
        this.graphe = graphe;
        this.initTache();
        /*this.sauvegarder();

        System.out.println("Fichier initialisé avec " + this.lstTacheMPMs.size() + " tâches.");
        this.lstTacheMPMs.get(3).setMarge(1000);
        this.modifierTacheFichier(this.lstTacheMPMs.get(3));
        this.sauvegarder();
        */
    }


    public void initTache()
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
                this.lstTacheMPMs.add(tache);
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public void ajouterTacheFichier(TacheMPM tacheAjout) 
    {
        this.lstTacheMPMs.add(tacheAjout);
    }

    public void modifierTacheFichier(TacheMPM tacheModif) 
    {
        for (int i = 0; i < this.lstTacheMPMs.size(); i++) {
            if (this.lstTacheMPMs.get(i).getNom().equals(tacheModif.getNom())) {
                this.lstTacheMPMs.set(i, tacheModif);
                break;
            }
        }
    }

    public void sauvegarder()
	{

		try
		{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("listeTache.txt"), "UTF8" ));

			for (TacheMPM tache:this.lstTacheMPMs )
			{
				pw.println ( tache.getNom() + "|" + 
                            tache.getDuree() + "|" + 
                            (tache.getPrecedents().isEmpty() ? "" : String.join(",",
                            tache.getPrecedents().stream().map(TacheMPM::getNom).toArray(String[]::new))) );

			}
			pw.close();
		}
		catch (Exception e){ e.printStackTrace(); }
	}

    public List<TacheMPM> getLstTacheMPMs() { return this.lstTacheMPMs; }

    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        for (TacheMPM tache : this.lstTacheMPMs) 
        {
            sb.append(tache.toString()).append("\n");
        }
        return sb.toString();
    }


}
