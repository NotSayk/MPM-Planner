package src.Metier;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Scanner;

import java.util.GregorianCalendar;
import java.util.Calendar;
import src.Controleur;

public class GrapheMPM
{
    private Controleur           ctrl;

    private String               dateRef ; 
    private char                 typeDate;
    private int[]                niveaux ;
    private List<CheminCritique> lstChemins;

    public GrapheMPM(Controleur ctrl)
    {
        this.ctrl       = ctrl;
        this.niveaux    = new int[100];
        this.lstChemins = new ArrayList<CheminCritique>();
    }

    public void calculerDates() 
    {
        this.initDateTot ();
        this.initDateTard();
        this.initMarge   ();
    }

    private void initDateTot() 
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
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

        for (int i = this.ctrl.getTaches().size() - 1; i >= 0; i--) 
        {
            TacheMPM tache = this.ctrl.getTaches().get(i);

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
        for (TacheMPM tache : this.ctrl.getTaches()) 
        {
            int dateTot  = tache.getDateTot ();
            int dateTard = tache.getDateTard();
            int marge    = dateTard - dateTot ;
            tache.setMarge(marge);
        }
    }

    public void initSuivants()
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
        {
            List<TacheMPM> suivants = new ArrayList<>();
            for (TacheMPM autreTache : this.ctrl.getTaches()) 
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

    public TacheMPM trouverTache(String nom) 
    {
        for (TacheMPM tache : this.ctrl.getTaches()) 
            if (tache.getNom().equals(nom)) 
                return tache;
        return null;
    }

    // Getters et setters

    public int getDureeProjet() 
    {
        int dureeMax = 0;
        for (TacheMPM tache : this.ctrl.getTaches()) 
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

    
    public void calculNiveauTaches() 
    {
        for (TacheMPM tache : ctrl.getTaches()) 
            tache.setNiveau(0);
        
        for (TacheMPM tache : ctrl.getTaches()) 
        {
            for (TacheMPM predecesseur : tache.getPrecedents()) 
            {
                if (predecesseur.getNiveau() + 1 > tache.getNiveau()) 
                    tache.setNiveau(predecesseur.getNiveau() + 1);
            }
            this.niveaux[tache.getNiveau()] += 1;
        }

    }

    public int   getNiveauTache (TacheMPM tache) { return tache.getNiveau(); }
    public int[] getNiveaux     ()               { return niveaux;           }

    public void initCheminCritique() 
    {
        TacheMPM fin   = this.ctrl.getTaches().get(this.ctrl.getTaches().size() - 1);
        TacheMPM debut = this.ctrl.getTaches().get(0);

        List<List<TacheMPM>> tousChemin   = new ArrayList<>();
        List<TacheMPM>       cheminActuel = new ArrayList<>();
        
        // Recherche récursive de tous les chemins critiques
        this.trouverTousCheminsCritiques(debut, fin, cheminActuel, tousChemin);
        
        // Afficher tous les chemins trouvés
        for (int i = 0; i < tousChemin.size(); i++) 
        {
            System.out.println("=== Chemin critique " + (i + 1) + " ===");
            afficherChemin(tousChemin.get(i));
            System.out.println();
        }
    }
  
    private void trouverTousCheminsCritiques(TacheMPM actuelle, TacheMPM fin, 
                                            List<TacheMPM> cheminActuel, 
                                            List<List<TacheMPM>> tousChemin) 
    {
        // Ajouter la tâche actuelle au chemin
        cheminActuel.add(actuelle);
        
        // Si on atteint la fin, vérifier si le chemin est critique
        if (actuelle.equals(fin)) 
        {
            if (estCheminCritique(cheminActuel))
                tousChemin.add(new ArrayList<>(cheminActuel));
        } 
        else 
        {

            // Continuer avec les successeurs critiques
            for (TacheMPM successeur : getSuccesseurs(actuelle))
                if (estLienCritique(actuelle, successeur))
                    trouverTousCheminsCritiques(successeur, fin, cheminActuel, tousChemin);
        }
        
        cheminActuel.remove(cheminActuel.size() - 1);
    }

    private boolean estLienCritique(TacheMPM precedent, TacheMPM successeur) 
    {
        // Un lien est critique si les deux tâches ont une marge de 0
        // et si les dates sont cohérentes
        return precedent.getMarge  () == 0 && successeur.getMarge() == 0 &&
               precedent.getDateTot() + precedent.getDuree() == successeur.getDateTot();
    }

    private boolean estCheminCritique(List<TacheMPM> chemin) 
    {
        // Un chemin est critique si toutes ses tâches ont une marge de 0
        for (TacheMPM tache : chemin) if (tache.getMarge() != 0)return false;
        return true;
    }

    private void afficherChemin(List<TacheMPM> chemin) 
    {
        CheminCritique cheminCritique = new CheminCritique();
        
        for (TacheMPM tache : chemin) 
        {
            cheminCritique.ajouterTache(tache);
            System.out.print(tache.getNom() + " -> ");
        }
        
        this.lstChemins.add(cheminCritique);
    }

    private List<TacheMPM> getSuccesseurs(TacheMPM tache) 
    {
        List<TacheMPM> successeurs = new ArrayList<>();
        
        for (TacheMPM autreTache : this.ctrl.getTaches())
            if (autreTache.getPrecedents().contains(tache)) successeurs.add(autreTache);
        
        return successeurs;
    }

    public String              getDateRef () { return dateRef ; }
    public char                getTypeDate() { return typeDate; }
    
    public void setDateRef(String dateRef) { this.dateRef = dateRef  ; }
    public void setTypeDate(char typeDate) { this.typeDate = typeDate; }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Graphe MPM:\n");
        for (TacheMPM tache : this.ctrl.getTaches()) 
            sb.append(tache.toString(this.dateRef)).append("\n");
        
        return sb.toString();
    }
}