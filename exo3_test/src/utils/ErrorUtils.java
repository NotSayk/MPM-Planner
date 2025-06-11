package utils;

import javax.swing.JOptionPane;

public class ErrorUtils {
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(String message) {
        JOptionPane.showMessageDialog(null,
            message,
            "Attention",
            JOptionPane.WARNING_MESSAGE);
    }

    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(null,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(String message) {
        return JOptionPane.showConfirmDialog(null,
            message,
            "Confirmation",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
} 