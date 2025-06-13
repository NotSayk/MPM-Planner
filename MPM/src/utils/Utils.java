package src.utils;

import src.metier.TacheMPM;

/**
 * Classe utilitaire pour le formatage des données.
 * Fournit des méthodes pour formater les informations des tâches.
 */
public class Utils 
{
    /*--------------*
     * Constructeur *
     *--------------*/
    /**
     * Constructeur privé pour empêcher l'instanciation.
     * Cette classe est une classe utilitaire et ne doit pas être instanciée.
     */
    private Utils() 
    {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /*----------------------------------*
     * Méthodes de formatage            *
     *----------------------------------*/
    /**
     * Formate la liste des tâches précédentes d'une tâche.
     * @param tache Tâche dont on veut formater les précédents
     * @return Chaîne formatée des noms des tâches précédentes séparés par des virgules
     */
    public static String formatVirgulePrecedents(TacheMPM tache) 
    {
        if (!tache.getPrecedents().isEmpty()) 
        {
            String precedents = "";
            for (int i = 0; i < tache.getPrecedents().size(); i++) 
            {
                precedents += tache.getPrecedents().get(i).getNom();
                if (i < tache.getPrecedents().size() - 1) 
                    precedents += ", ";
            }
            return precedents;
        }
        return "";
    }

    /**
     * Formate la liste des tâches suivantes d'une tâche.
     * @param tache Tâche dont on veut formater les suivants
     * @return Chaîne formatée des noms des tâches suivantes séparés par des virgules
     */
    public static String formatVirguleSuivants(TacheMPM tache) 
    {
        if (!tache.getSuivants().isEmpty()) 
        {
            String suivants = "";
            for (int i = 0; i < tache.getSuivants().size(); i++) 
            {
                suivants += tache.getSuivants().get(i).getNom();
                if (i < tache.getSuivants().size() - 1) 
                    suivants += ", ";
            }
            return suivants;
        }
        return "";
    }
}
