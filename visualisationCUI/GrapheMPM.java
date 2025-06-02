import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class GrapheMPM
{
    private ArrayList <TacheMPM> taches;

    public static void main(String[] args) 
    {
        System.out.println("Bienvenue dans l'application de gestion de projet MPM");
        new GrapheMPM()                                                            ;
    }

    public GrapheMPM()
    {
        this.taches     = new ArrayList<TacheMPM>() ;
        this.lireFichier ();
        this.initDateTot ();
        this.initDateTard();
        this.initMarge   ();
        System.out.println(this.toString());
    }

    public static String getDateDuJour() 
    {
        GregorianCalendar calendar = new GregorianCalendar()   ;
        int jour  = calendar.get( Calendar.DAY_OF_MONTH )      ;
        int mois  = calendar.get( Calendar.MONTH        ) + 1  ;
        int annee = calendar.get( Calendar.YEAR         )      ;
        
        return String.format( "%02d/%02d/%04d", jour, mois, annee );
    }

    public static String ajouterJourDate ( int jours ) 
    {
        String   date    = getDateDuJour()              ;
        String[] parties = date.split("/")              ;
        int      jour    = Integer.parseInt(parties[0]) ;
        int      mois    = Integer.parseInt(parties[1]) ;
        int      annee   = Integer.parseInt(parties[2]) ;

        GregorianCalendar calendar = new GregorianCalendar(annee, mois - 1, jour);
        calendar.add(Calendar.DAY_OF_MONTH, jours);

        return String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), 
                                               calendar.get(Calendar.MONTH) + 1, 
                                               calendar.get(Calendar.YEAR));
    }

    private void initDateTot() 
    {
        for (TacheMPM tache : this.taches) 
        {
            if (!tache.getPrecedents().isEmpty()) 
            {
                int maxFinPrecedent = 0;
                for (TacheMPM precedent : tache.getPrecedents()) 
                {
                    int finPrecedent = precedent.getDateTot() + precedent.getDuree();
                    if (finPrecedent > maxFinPrecedent)
                        maxFinPrecedent = finPrecedent;
                }
                tache.setDateTot(maxFinPrecedent);
            }
        }
    }


    private void initDateTard() 
    {
        for (int i = this.taches.size() - 1; i >= 0; i--)
        {
            TacheMPM tache = this.taches.get(i);

            if (!tache.getSuivants().isEmpty()) 
            {
                int minDateTard = Integer.MAX_VALUE;
                for(TacheMPM tacheSuivantes : tache.getSuivants())
                {
                    if(tacheSuivantes.getDateTard() < minDateTard)
                    {
                        minDateTard = tacheSuivantes.getDateTard();
                    }
                }
                tache.setDateTard(minDateTard - tache.getDuree());
                continue;
            }
            tache.setDateTard(tache.getDateTot());
        }
    }


    public void initMarge()
    {
        for (TacheMPM tache : this.taches) 
        {
            int dateTot  = tache.getDateTot ();
            int dateTard = tache.getDateTard();
            int marge    = dateTard - dateTot;
            tache.setMarge(marge);
        }
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

                String[] parties = ligne.split("\\|", -1);

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

    private TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.taches) 
            if (tache.getNom().equals(nom)) 
                return tache;
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
