package src.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Classe utilitaire pour la manipulation des dates.
 * Fournit des méthodes pour formater et manipuler les dates dans l'application.
 */
public class DateUtils 
{
    /*--------------*
     * Constructeur *
     *--------------*/
    /**
     * Constructeur privé pour empêcher l'instanciation.
     * Cette classe est une classe utilitaire et ne doit pas être instanciée.
     */
    private DateUtils() 
    {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /*----------------------------------*
     * Méthodes de gestion des dates    *
     *----------------------------------*/
    /**
     * Récupère la date du jour au format JJ/MM/AAAA.
     * @return Date du jour formatée
     */
    public static String getDateDuJour() 
    {
        GregorianCalendar calendar = new GregorianCalendar();
        int               jour     = calendar.get(Calendar.DAY_OF_MONTH);
        int               mois     = calendar.get(Calendar.MONTH) + 1;
        int               annee    = calendar.get(Calendar.YEAR);
        
        return String.format("%02d/%02d/%04d", jour, mois, annee);
    }

    /**
     * Ajoute un nombre de jours à une date donnée.
     * @param date Date initiale au format JJ/MM/AAAA
     * @param jours Nombre de jours à ajouter
     * @return Nouvelle date au format JJ/MM/AAAA
     */
    public static String ajouterJourDate(String date, int jours) 
    {
        String[] parties = date.split("/");
        int      jour    = Integer.parseInt(parties[0]);
        int      mois    = Integer.parseInt(parties[1]);
        int      annee   = Integer.parseInt(parties[2]);

        GregorianCalendar calendar = new GregorianCalendar(annee, mois - 1, jour);
        calendar.add(Calendar.DAY_OF_MONTH, jours);

        return String.format("%02d/%02d/%04d", 
            calendar.get(Calendar.DAY_OF_MONTH), 
            calendar.get(Calendar.MONTH) + 1, 
            calendar.get(Calendar.YEAR));
    }
}