package src.metier;

import java.util.ArrayList;
import java.util.List;

public class CheminCritique
{
    private ArrayList<TacheMPM> cheminCritique;
    
    public CheminCritique()
    {
        this.cheminCritique = new ArrayList<TacheMPM>();
    }

    public void                ajouterTache(TacheMPM t) { this.cheminCritique.add(t); }   
    public ArrayList<TacheMPM> getListTache()           { return this.cheminCritique; }

    public static boolean estLienCritique(TacheMPM precedent, TacheMPM successeur) 
    {
        return precedent.getMarge  () == 0 && successeur.getMarge() == 0 &&
               precedent.getDateTot() + precedent.getDuree() == successeur.getDateTot();
    }

    public static boolean estCheminCritique(List<TacheMPM> chemin) 
    {
        for (TacheMPM tache : chemin) if (tache.getMarge() != 0)return false;
        return true;
    }
}