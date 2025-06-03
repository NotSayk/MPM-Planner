package src.Metier;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

import java.util.GregorianCalendar;
import java.util.Calendar;

public class GrapheMPM
{
    private ArrayList<TacheMPM> taches  ;
    private String              dateRef ; 
    private char                typeDate;

    public GrapheMPM()
    {
        this.taches = new ArrayList<TacheMPM>();
    }

    public static String getDateDuJour() 
    {
        GregorianCalendar calendar = new GregorianCalendar()            ;
        int               jour     = calendar.get(Calendar.DAY_OF_MONTH);
        int               mois     = calendar.get(Calendar.MONTH) + 1   ;
        int               annee    = calendar.get(Calendar.YEAR)        ;
        
        return String.format("%02d/%02d/%04d", jour, mois, annee);
    }

    public static String ajouterJourDate(String date, int jours) 
    {
        String[] parties = date.split("/")             ;
        int      jour    = Integer.parseInt(parties[0]);
        int      mois    = Integer.parseInt(parties[1]);
        int      annee   = Integer.parseInt(parties[2]);

        GregorianCalendar calendar = new GregorianCalendar(annee, mois - 1, jour);
        calendar.add(Calendar.DAY_OF_MONTH, jours)                               ;

        return String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), 
                                                calendar.get(Calendar.MONTH) + 1  , 
                                                calendar.get(Calendar.YEAR))      ;
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
                    tachesPrecedentes.add(this.trouverTache(precedent.trim()));
                }
                
                TacheMPM tache = new TacheMPM(nom, duree, tachesPrecedentes);
                this.ajouterTache(tache);
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        this.initSuivants();
    }

    public void calculerDates() 
    {
        this.initDateTot ();
        this.initDateTard();
        this.initMarge   ();
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
                    if (tacheSuivantes.getDateTard() < minDateTard) 
                        minDateTard = tacheSuivantes.getDateTard();
                }
                tache.setDateTard(minDateTard - tache.getDuree());
                continue;
            }
            tache.setDateTard(tache.getDateTot());
        }
    }

    private void initMarge()
    {
        for (TacheMPM tache : this.taches) 
        {
            int dateTot  = tache.getDateTot ();
            int dateTard = tache.getDateTard();
            int marge    = dateTard - dateTot ;
            tache.setMarge(marge);
        }
    }

    private void ajouterTache(TacheMPM tache)
    {
        if (tache != null) 
        {
            this.taches.add(tache);
            return;
        }

        System.out.println("Erreur : la tâche à ajouter est nulle.");
    }

    private void initSuivants()
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
                            suivants.add(autreTache);
                            break;
                        }
                    }
                }
            }
            tache.setSuivants(suivants);
        }
    }

    private TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.taches) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    // Getters et setters

    public int getDureeProjet() 
    {
        int dureeMax = 0;
        for (TacheMPM tache : this.taches) 
        {
            if (tache.getSuivants().isEmpty()) 
            {
                int finTache = tache.getDateTot() + tache.getDuree();
                if (finTache > dureeMax) dureeMax = finTache;
            }
        }
        return dureeMax;
    }

    public String getDateProjet(char typeDemande) 
    {
        int dureeProjet = getDureeProjet();
        if (typeDemande == 'F') 
            return "Date de fin du projet : "   + ajouterJourDate(this.dateRef, dureeProjet) ;
        else                    
            return "Date de début du projet : " + ajouterJourDate(this.dateRef, -dureeProjet);
    }

    public void initCheminCritique() {
        TacheMPM fin = this.taches.getLast();
        List<TacheMPM> precedentsCritiques = new ArrayList<>();
        
        // Trouver tous les prédécesseurs critiques de la tâche finale
        for (TacheMPM precedent : fin.getPrecedents()) {
            if (precedent.getMarge() == 0 && 
                precedent.getDateTot() + precedent.getDuree() == fin.getDateTot()) {
                precedentsCritiques.add(precedent);
            }
        }
        
        // Afficher un chemin pour chaque prédécesseur critique
        for (int i = 0; i < precedentsCritiques.size(); i++) {
            System.out.println("=== Chemin critique " + (i + 1) + " ===");
            afficherCheminDepuis(precedentsCritiques.get(i), fin);
            System.out.println();
        }
    }

    private TacheMPM trouverPrecedentCritique(TacheMPM tache) {
        for (TacheMPM precedent : tache.getPrecedents()) 
        {
            if (precedent.getMarge() == 0 && 
                precedent.getDateTot() + precedent.getDuree() == tache.getDateTot()) 
                {
                return precedent;
            }
        }
        return null;
    }

    private void afficherCheminDepuis(TacheMPM debut, TacheMPM fin) {
        List<TacheMPM> chemin = new ArrayList<>();
        TacheMPM actuelle = debut;
        
        while (actuelle != null) {
            chemin.add(actuelle);
            actuelle = trouverPrecedentCritique(actuelle);
        }
        
        // Afficher en ordre inverse + ajouter la tâche finale
        for (int i = chemin.size() - 1; i >= 0; i--) {
            System.out.println(chemin.get(i).getNom() + " (Critique)");
        }
        System.out.println(fin.getNom() + " (Critique)");
    }

    public ArrayList<TacheMPM> getTaches  () { return taches  ; }
    public String              getDateRef () { return dateRef ; }
    public char                getTypeDate() { return typeDate; }
    
    public void setDateRef(String dateRef) { this.dateRef = dateRef  ; }
    public void setTypeDate(char typeDate) { this.typeDate = typeDate; }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Graphe MPM:\n");
        for (TacheMPM tache : taches) 
            sb.append(tache.toString(this.dateRef)).append("\n");
        
        return sb.toString();
    }
}