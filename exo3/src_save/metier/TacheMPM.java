package src_save.metier;

import java.util.ArrayList;
import java.util.List;

import src_save.utils.DateUtils;

public class TacheMPM 
{
    private String         nom;
    private int            duree;
    private int            dateTot;
    private int            dateTard;
    private int            marge;
    private int            niveau;
    private boolean        estCritique;
    private List<TacheMPM> precedents;
    private List<TacheMPM> suivants;

    public TacheMPM(String nom, int duree, List<TacheMPM> precedents) 
    {
        this.nom         = nom;
        this.duree       = duree;
        this.dateTot     = 0;
        this.dateTard    = 0; 
        this.marge       = 0;
        this.niveau      = 0;
        this.estCritique = false;

        this.precedents  = precedents;
        this.suivants    = new ArrayList<TacheMPM>();
    }

    public void setSuivants(List<TacheMPM> suivants) 
    {
        if (suivants == null) return;
        this.suivants = suivants    ;
    }

    public void setPrecedents(List<TacheMPM> precedents) 
    {
        if (precedents == null) return;
        this.precedents = precedents;
    }

    // Getters
    public String         getNom       () { return this.nom       ; }
    public int            getDuree     () { return this.duree     ; }
    public int            getDateTot   () { return this.dateTot   ; }
    public int            getDateTard  () { return this.dateTard  ; }
    public int            getMarge     () { return this.marge     ; }
    public int            getNiveau    () { return this.niveau    ; }
    public List<TacheMPM> getPrecedents() { return this.precedents; }
    public List<TacheMPM> getSuivants  () { return this.suivants  ; }
    public boolean        estCritique  () { return this.estCritique; }

    // Setters
    public void setDateTot (int dateTot)  { this.dateTot  = dateTot ; }
    public void setDateTard(int dateTard) { this.dateTard = dateTard; }
    public void setMarge   (int marge)    { this.marge    = marge   ; }
    public void setDuree   (int duree)    { this.duree    = duree   ; }
    public void setNiveau  (int niveau)   { this.niveau   = niveau  ; }

    public void setCritique(boolean estCritique) { this.estCritique = estCritique; }

    public String toString(String dateRef)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(nom).append(" : ").append(duree).append(" jour").append(duree > 1 ? "s" : "").append("\n")
        .append("    date au plus tôt  : ").append(DateUtils.ajouterJourDate(dateRef, dateTot)).append("\n")
        .append("    date au plus tard : ").append(DateUtils.ajouterJourDate(dateRef, dateTard)).append("\n")
        .append("    marge             : ").append(marge).append(marge == 0 || marge == 1 ? " jour" : " jours").append("\n")
        .append("    liste des tâches précédentes :\n")
        .append("    ").append(precedents.isEmpty() ? "pas de tâche précédente\n" : "        ");
        
        if (!precedents.isEmpty()) {
            sb.append("        ");
            for (int i = 0; i < precedents.size(); i++) {
                sb.append(precedents.get(i).getNom());
                if (i < precedents.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        
        sb.append("    liste des tâches suivantes :\n");
        
        if (suivants.isEmpty()) {
            sb.append("        pas de tâche suivante\n");
        } else {
            sb.append("        ");
            for (int i = 0; i < suivants.size(); i++) {
                sb.append(suivants.get(i).getNom());
                if (i < suivants.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}