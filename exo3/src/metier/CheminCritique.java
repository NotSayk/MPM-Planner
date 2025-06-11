package src.metier;

import java.util.ArrayList;
import java.util.List;

public class CheminCritique
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private ArrayList<TacheMPM> cheminCritique;
    
    /*--------------*
     * Constructeur *
     *--------------*/
    public CheminCritique()
    {
        this.cheminCritique = new ArrayList<TacheMPM>();
    }

    /*----------------------------------*
     * Méthodes de gestion du chemin    *
     *----------------------------------*/
    public void ajouterTache(TacheMPM tache) 
    { 
        this.cheminCritique.add(tache); 
    }

    /*---------------------------------*
     * Méthodes statiques de validation*
     *---------------------------------*/
    public static boolean estLienCritique(TacheMPM precedent, TacheMPM successeur) 
    {
        return precedent.getMarge() == 0 && 
               successeur.getMarge() == 0 &&
               precedent.getDateTot() + precedent.getDuree() == successeur.getDateTot();
    }

    public static boolean estCheminCritique(List<TacheMPM> chemin) 
    {
        for (TacheMPM tache : chemin) 
            if (tache.getMarge() != 0) 
                return false;
        return true;
    }

    /*---------------------------------*
     * Accesseurs - Getters            *
     *---------------------------------*/
    public ArrayList<TacheMPM> getListTache() { return this.cheminCritique; }

    /*---------------------------------*
     * Méthodes utilitaires            *
     *---------------------------------*/
    public boolean isEmpty() 
    {
        return this.cheminCritique.isEmpty();
    }

    public int size() 
    {
        return this.cheminCritique.size();
    }

    public void clear() 
    {
        this.cheminCritique.clear();
    }
}