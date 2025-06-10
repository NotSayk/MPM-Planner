package src.utils;

import javax.swing.JOptionPane;

public class ErrorUtils 
{
    
    public static void showWarning(String message) 
    {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Avertissement",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static void showError(String message) 
    {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static void showInfo(String message) 
    {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

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
