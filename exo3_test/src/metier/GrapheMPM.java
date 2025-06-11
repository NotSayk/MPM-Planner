package metier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class GrapheMPM {
    private List<TacheMPM> taches;
    private List<CheminCritique> cheminsCritiques;
    private String dateRef;
    private char dateType;
    private int[] niveaux;
    private int numNiveauxTot;
    private int numNiveauxTard;
    private boolean critique;

    public GrapheMPM() {
        this.taches = new ArrayList<>();
        this.cheminsCritiques = new ArrayList<>();
        this.niveaux = new int[1000];
        this.numNiveauxTot = -1;
        this.numNiveauxTard = 0;
    }

    public void calculerDates() {
        calculerDatesTot();
        calculerDatesTard();
    }

    private void calculerDatesTot() {
        for (TacheMPM tache : taches) {
            if (!tache.getPrecedents().isEmpty()) {
                int maxFinPrecedent = 0;
                for (TacheMPM precedent : tache.getPrecedents()) {
                    int finPrecedent = precedent.getDateTot() + precedent.getDuree();
                    if (finPrecedent > maxFinPrecedent)
                        maxFinPrecedent = finPrecedent;
                }
                tache.setDateTot(maxFinPrecedent);
            }
        }
    }

    private void calculerDatesTard() {
        for (int i = taches.size() - 1; i >= 0; i--) {
            TacheMPM tache = taches.get(i);
            if (!tache.getSuivants().isEmpty()) {
                int minDateTard = Integer.MAX_VALUE;
                for (TacheMPM tacheSuivante : tache.getSuivants()) {
                    if (tacheSuivante.getDateTard() < minDateTard)
                        minDateTard = tacheSuivante.getDateTard();
                }
                tache.setDateTard(minDateTard - tache.getDuree());
            } else {
                tache.setDateTard(tache.getDateTot());
            }
        }
    }

    public void initNiveauTaches() {
        for (TacheMPM tache : taches) {
            tache.setNiveau(0);
        }
        
        for (TacheMPM tache : taches) {
            for (TacheMPM predecesseur : tache.getPrecedents()) {
                if (predecesseur.getNiveau() + 1 > tache.getNiveau()) {
                    tache.setNiveau(predecesseur.getNiveau() + 1);
                }
            }
            this.niveaux[tache.getNiveau()]++;
        }
    }

    public void initCheminCritique() {
        if (taches.isEmpty()) return;
        
        TacheMPM fin = taches.get(taches.size() - 1);
        TacheMPM debut = taches.get(0);

        List<List<TacheMPM>> tousChemins = new ArrayList<>();
        List<TacheMPM> cheminActuel = new ArrayList<>();
        
        trouverTousCheminsCritiques(debut, fin, cheminActuel, tousChemins);
        
        for (List<TacheMPM> chemin : tousChemins) {
            definirCritique(chemin);
        }
    }

    private void trouverTousCheminsCritiques(TacheMPM actuelle, TacheMPM fin, 
                                           List<TacheMPM> cheminActuel, 
                                           List<List<TacheMPM>> tousChemins) {
        cheminActuel.add(actuelle);
        
        if (actuelle.equals(fin)) {
            if (CheminCritique.estCheminCritique(cheminActuel)) {
                tousChemins.add(new ArrayList<>(cheminActuel));
            }
        } else {
            for (TacheMPM successeur : getSuccesseurs(actuelle)) {
                if (CheminCritique.estLienCritique(actuelle, successeur)) {
                    trouverTousCheminsCritiques(successeur, fin, cheminActuel, tousChemins);
                }
            }
        }
        
        cheminActuel.remove(cheminActuel.size() - 1);
    }

    private void definirCritique(List<TacheMPM> chemin) {
        CheminCritique cheminCritique = new CheminCritique();
        for (TacheMPM tache : chemin) {
            cheminCritique.ajouterTache(tache);
            tache.setCritique(true);
        }
        this.cheminsCritiques.add(cheminCritique);
    }

    private List<TacheMPM> getSuccesseurs(TacheMPM tache) {
        List<TacheMPM> successeurs = new ArrayList<>();
        for (TacheMPM autreTache : taches) {
            if (autreTache.getPrecedents().contains(tache)) {
                successeurs.add(autreTache);
            }
        }
        return successeurs;
    }

    public int getDureeProjet() {
        if (taches.isEmpty()) return 0;
        
        int maxFin = 0;
        for (TacheMPM tache : taches) {
            int fin = tache.getDateTot() + tache.getDuree();
            if (fin > maxFin) {
                maxFin = fin;
            }
        }
        return maxFin;
    }

    // Getters et Setters
    public List<TacheMPM> getTaches() { return taches; }
    public List<CheminCritique> getCheminsCritiques() { return cheminsCritiques; }
    public String getDateRef() { return dateRef; }
    public void setDateRef(String dateRef) { this.dateRef = dateRef; }
    public char getDateType() { return dateType; }
    public void setDateType(char dateType) { this.dateType = dateType; }
    public int[] getNiveaux() { return niveaux; }
    public int getNumNiveauxTot() { return numNiveauxTot; }
    public void setNumNiveauxTot(int numNiveauxTot) { this.numNiveauxTot = numNiveauxTot; }
    public int getNumNiveauxTard() { return numNiveauxTard; }
    public void setNumNiveauxTard(int numNiveauxTard) { this.numNiveauxTard = numNiveauxTard; }
    public boolean isCritique() { return critique; }
    public void setCritique(boolean critique) { this.critique = critique; }
} 