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
    private List<TacheMPM> lstTacheMPMs;
    String             nomFichier;
    
    public Fichier(GrapheMPM graphe, String nomFichier)
    {
        this.graphe       = graphe;
        this.lstTacheMPMs = new ArrayList<TacheMPM>();
        this.initTache(nomFichier);
        this.nomFichier   = nomFichier;
    }


    public void initTache(String nomFichier)
    {
        Scanner  scMPM     ;
        String   ligne     ;
        String   nom       ;
        int      duree     ;
        String[] precedents;

        this.lstTacheMPMs.clear();

        try
        {
            scMPM = new Scanner(new File(nomFichier), "UTF-8");

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
                    tachesPrecedentes.add(this.trouverTache(precedent.trim()));
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
        this.sauvegarder();
    }

    public void modifierTacheFichier(TacheMPM tacheModif) 
    {
        for (int i = 0; i < this.lstTacheMPMs.size(); i++) {
            if (this.lstTacheMPMs.get(i).getNom().equals(tacheModif.getNom())) {
                this.lstTacheMPMs.set(i, tacheModif);
                break;
            }
        }
        this.sauvegarder();
    }
    

    public void sauvegarder()
	{

		try
		{
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("listeTache.txt"), "UTF8" ));

			for (TacheMPM tache:this.lstTacheMPMs )
			{
				pw.println ( tache.getNom()   + "|" + 
                             tache.getDuree() + "|" + 
                             (tache.getPrecedents().isEmpty() ? "" : String.join(",",
                             tache.getPrecedents().stream().map(TacheMPM::getNom).toArray(String[]::new))) );

			}
			pw.close();
		}
		catch (Exception e){ e.printStackTrace(); }
	}

    public List<TacheMPM> getLstTacheMPMs() { return this.lstTacheMPMs; }

    private TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.lstTacheMPMs) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    public String getNomFichier() { return this.nomFichier;  }


    public int[] getLocation(TacheMPM tache, String fichier)
    {
        Scanner  scMPM     ;
        String   ligne;
        int[] pos = new int[2];

        try
        {
            scMPM = new Scanner(new File(fichier), "UTF-8");

            while (scMPM.hasNextLine())
            {
                ligne = scMPM.nextLine().trim();
                if (ligne.isEmpty()) continue;

                String[] parties = ligne.split("\\|", -1);

                if(tache.getNom().equals(parties[0]))
                {
                    pos[0] = Integer.parseInt(parties[3]);
                    pos[1] = Integer.parseInt(parties[4]);

                    return pos;
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return null;
    }

}
