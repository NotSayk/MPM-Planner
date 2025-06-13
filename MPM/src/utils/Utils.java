package src.utils;

import src.metier.TacheMPM;

public class Utils 
{
    
    public Utils() 
    {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String formatVirgulePrecedents(TacheMPM tache) 
    {
        if (!tache.getPrecedents().isEmpty()) 
        {
            String precedents = "";
            for (int i = 0; i < tache.getPrecedents().size(); i++) 
            {
                precedents += tache.getPrecedents().get(i).getNom();
                if (i < tache.getPrecedents().size() - 1) precedents += ", ";
            }
            return precedents;
        }
        return "";
    }

    public static String formatVirguleSuivants(TacheMPM tache) 
    {
        if (!tache.getSuivants().isEmpty()) 
        {
            String suivants = "";
            for (int i = 0; i < tache.getSuivants().size(); i++) 
            {
                suivants += tache.getSuivants().get(i).getNom();
                if (i < tache.getSuivants().size() - 1) suivants += ", ";
            }
            return suivants;
        }
        return ""; // Return an empty string if there are no suivants
    }

}
