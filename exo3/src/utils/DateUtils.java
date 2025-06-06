package src.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtils 
{
    
    public static String getDateDuJour() 
    {
        GregorianCalendar calendar = new GregorianCalendar()            ;
        int               jour     = calendar.get(Calendar.DAY_OF_MONTH);
        int               mois     = calendar.get(Calendar.MONTH) + 1   ;
        int               annee    = calendar.get(Calendar.YEAR)        ;
        
        return String.format("%02d/%02d/%04d", jour, mois, annee);
    }

    public static String ajouterJourDate(String date, int jours) 
    {
        String[] parties = date.split("/")             ;
        int      jour    = Integer.parseInt(parties[0]);
        int      mois    = Integer.parseInt(parties[1]);
        int      annee   = Integer.parseInt(parties[2]);

        GregorianCalendar calendar = new GregorianCalendar(annee, mois - 1, jour);
        calendar.add(Calendar.DAY_OF_MONTH, jours)                               ;

        return String.format("%02d/%02d/%04d", calendar.get(Calendar.DAY_OF_MONTH), 
                                                calendar.get(Calendar.MONTH) + 1  , 
                                                calendar.get(Calendar.YEAR))      ;
    }


}
