package metier;

import java.util.ArrayList;
import java.util.List;

public class TacheMPM {
    private String nom;
    private int duree;
    private int dateTot;
    private int dateTard;
    private int niveau;
    private boolean critique;
    private List<TacheMPM> precedents;
    private List<TacheMPM> suivants;

    public TacheMPM(String nom, int duree) {
        this.nom = nom;
        this.duree = duree;
        this.dateTot = 0;
        this.dateTard = 0;
        this.niveau = 0;
        this.critique = false;
        this.precedents = new ArrayList<>();
        this.suivants = new ArrayList<>();
    }

    public void setPrecedents(String precedentsStr) {
        this.precedents.clear();
        if (precedentsStr == null || precedentsStr.trim().isEmpty()) {
            return;
        }
        
        String[] nomsPrecedents = precedentsStr.split(",");
        for (String nom : nomsPrecedents) {
            nom = nom.trim();
            if (!nom.isEmpty()) {
                // La tâche sera ajoutée plus tard quand toutes les tâches seront créées
                // On utilise juste le nom pour l'instant
            }
        }
    }

    public void setSuivants(String suivantsStr) {
        this.suivants.clear();
        if (suivantsStr == null || suivantsStr.trim().isEmpty()) {
            return;
        }
        
        String[] nomsSuivants = suivantsStr.split(",");
        for (String nom : nomsSuivants) {
            nom = nom.trim();
            if (!nom.isEmpty()) {
                // La tâche sera ajoutée plus tard quand toutes les tâches seront créées
                // On utilise juste le nom pour l'instant
            }
        }
    }

    // Getters et Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }
    
    public int getDateTot() { return dateTot; }
    public void setDateTot(int dateTot) { this.dateTot = dateTot; }
    
    public int getDateTard() { return dateTard; }
    public void setDateTard(int dateTard) { this.dateTard = dateTard; }
    
    public int getNiveau() { return niveau; }
    public void setNiveau(int niveau) { this.niveau = niveau; }
    
    public boolean isCritique() { return critique; }
    public void setCritique(boolean critique) { this.critique = critique; }
    
    public List<TacheMPM> getPrecedents() { return precedents; }
    public List<TacheMPM> getSuivants() { return suivants; }

    public void ajouterPrecedent(TacheMPM precedent) {
        this.precedents.add(precedent);
        precedent.suivants.add(this);
    }

    @Override
    public String toString() {
        return nom;
    }
} 