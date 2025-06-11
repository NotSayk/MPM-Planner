package src.ihm;

import javax.swing.*;
import java.awt.*;
import src.Controleur;

/**
 * Fenêtre d'affichage des chemins critiques
 * 
 * Cette fenêtre permet de :
 * - Visualiser tous les chemins critiques du graphe
 * - Afficher les tâches composant chaque chemin
 * - Voir la durée totale de chaque chemin
 */
public class FrameCritique extends JFrame
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private PanelCritique panelCritique; // Panneau d'affichage des chemins critiques
    private Controleur    ctrl;          // Référence au contrôleur
    
    /**
     * Crée une nouvelle fenêtre d'affichage des chemins critiques
     * 
     * @param ctrl Le contrôleur de l'application
     */
    public FrameCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        setTitle("Chemins Critiques");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        panelCritique = new PanelCritique(this.ctrl);
        add(panelCritique, BorderLayout.CENTER);
    }   
}
