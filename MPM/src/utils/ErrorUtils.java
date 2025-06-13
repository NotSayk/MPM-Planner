package src.utils;

import javax.swing.JOptionPane;

/**
 * Classe utilitaire pour la gestion des messages d'erreur et d'information.
 * Fournit des méthodes pour afficher différents types de messages à l'utilisateur.
 */
public class ErrorUtils 
{

    /**
     * Constructeur privé pour empêcher l'instanciation.
     * Cette classe est une classe utilitaire et ne doit pas être instanciée.
     */
    private ErrorUtils() 
    {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Affiche un message d'avertissement.
     * @param message Message à afficher
     */
    public static void showWarning(String message) 
    {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Avertissement",
            JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Affiche un message d'erreur.
     * @param message Message à afficher
     */
    public static void showError(String message) 
    {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Affiche un message d'information.
     * @param message Message à afficher
     */
    public static void showInfo(String message) 
    {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Affiche un message de succès.
     * @param message Message à afficher
     */
    public static void showSucces(String message) 
    {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Succès",
            JOptionPane.QUESTION_MESSAGE
        );
    }
}
