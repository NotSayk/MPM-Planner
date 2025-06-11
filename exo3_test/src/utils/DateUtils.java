package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static String getDateDuJour() {
        return DATE_FORMAT.format(new Date());
    }

    public static String ajouterJourDate(String date, int nbJours) {
        try {
            Date dateObj = DATE_FORMAT.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateObj);
            cal.add(Calendar.DAY_OF_MONTH, nbJours);
            return DATE_FORMAT.format(cal.getTime());
        } catch (Exception e) {
            System.err.println("Erreur lors de la manipulation de la date : " + e.getMessage());
            return date;
        }
    }

    public static int calculerDuree(String dateDebut, String dateFin) {
        try {
            Date debut = DATE_FORMAT.parse(dateDebut);
            Date fin = DATE_FORMAT.parse(dateFin);
            
            long diff = fin.getTime() - debut.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de la durée : " + e.getMessage());
            return 0;
        }
    }
} 