import java.util.ArrayList;

public class CheminCritique
{
    private ArrayList<TacheMPM> CheminCritique;
    
    public CheminCritique()
    {
        this.CheminCritique = new ArrayList<TacheMPM>();
    }

    public void ajouterTache(TacheMPM t)
    {
        this.CheminCritique.add(t);
    }   
    
    public ArrayList<TacheMPM> getListTache()
    {
        return this.CheminCritique;
    }
}