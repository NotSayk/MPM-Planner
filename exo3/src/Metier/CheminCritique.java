package src.Metier;

import java.util.ArrayList;

public class CheminCritique
{
    private ArrayList<TacheMPM> cheminCritique;
    
    public CheminCritique()
    {
        this.cheminCritique = new ArrayList<TacheMPM>();
    }

    public void ajouterTache(TacheMPM t)
    {
        this.cheminCritique.add(t);
    }   
    
    public ArrayList<TacheMPM> getListTache()
    {
        return this.cheminCritique;
    }
}