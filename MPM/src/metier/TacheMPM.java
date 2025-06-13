package src.metier;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente une tâche dans un graphe MPM.
 * Une tâche est caractérisée par son nom, sa durée, ses dates au plus tôt et au plus tard,
 * et ses relations avec les autres tâches (prédécesseurs et successeurs).
 */
public class TacheMPM 
{

    private String         nom;
    private int            duree;
    private int            dateTot;
    private int            dateTard;
    private int            niveau;
    private boolean        estCritique;
    private List<TacheMPM> precedents;
    private List<TacheMPM> suivants;

    /**
     * Crée une nouvelle tâche avec les paramètres spécifiés.
     * @param nom Nom de la tâche
     * @param duree Durée de la tâche en jours
     * @param precedents Liste des tâches prédécesseurs
     */
    public TacheMPM(String nom, int duree, List<TacheMPM> precedents) 
    {
        this.nom         = nom;
        this.duree       = duree;
        this.dateTot     = 0;
        this.dateTard    = 0; 
        this.niveau      = 0;
        this.estCritique = false;

        this.precedents  = precedents;
        this.suivants    = new ArrayList<TacheMPM>();
    }

    /**
     * Définit la liste des tâches successeurs.
     * @param suivants Liste des tâches successeurs
     */
    public void setSuivants(List<TacheMPM> suivants) 
    {
        if (suivants == null) return;
        this.suivants = suivants;
    }

    /**
     * Définit la liste des tâches prédécesseurs.
     * @param precedents Liste des tâches prédécesseurs
     */
    public void setPrecedents(List<TacheMPM> precedents) 
    {
        if (precedents == null) return;
        this.precedents = precedents;
    }

    public String         getNom       () { return this.nom       ; }
    public int            getDuree     () { return this.duree     ; }
    public int            getDateTot   () { return this.dateTot   ; }
    public int            getDateTard  () { return this.dateTard  ; }
    public int            getMarge     () { return this.dateTard - this.dateTot; }
    public int            getNiveau    () { return this.niveau    ; }
    public List<TacheMPM> getPrecedents() { return this.precedents; }
    public List<TacheMPM> getSuivants  () { return this.suivants  ; }
    public boolean        estCritique  () { return this.estCritique; }

    public void setDateTot (int dateTot)  { this.dateTot  = dateTot ; }
    public void setDateTard(int dateTard) { this.dateTard = dateTard; }
    public void setDuree   (int duree)    { this.duree    = duree   ; }
    public void setNiveau  (int niveau)   { this.niveau   = niveau  ; }
    public void setNom     (String nom)   { this.nom      = nom     ; }
    public void setCritique(boolean estCritique) { this.estCritique = estCritique; }

    @Override
    public String toString() 
    {
        return String.format(this.nom);
    }
}