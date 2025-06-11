package src.ihm;

import javax.swing.*;
import java.awt.*;
import src.Controleur;

/**
 * Panneau d'affichage des chemins critiques
 * 
 * Ce panneau permet de :
 * - Afficher la liste des chemins critiques dans une zone de texte
 * - Actualiser l'affichage lorsque le graphe est modifié
 * - Naviguer dans les chemins critiques avec une barre de défilement
 */
public class PanelCritique extends JPanel 
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur ctrl;      // Référence au contrôleur
    private JTextArea  textArea;  // Zone de texte pour l'affichage des chemins

    /**
     * Crée un nouveau panneau d'affichage des chemins critiques
     * 
     * @param ctrl Le contrôleur de l'application
     */
    public PanelCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        this.setLayout(new BorderLayout());
        
        // Zone de texte pour afficher les chemins critiques
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        
        this.textArea.setText(this.ctrl.getGraphe().afficherCheminsCritiques());
    }
    
    /**
     * Actualise l'affichage des chemins critiques
     * Cette méthode est appelée lorsque le graphe est modifié
     */
    public void actualiser() 
    {
        this.textArea.setText(this.ctrl.getGraphe().afficherCheminsCritiques());
    }
}