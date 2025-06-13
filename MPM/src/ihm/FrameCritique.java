package src.ihm;

import java.awt.*;
import javax.swing.*;
import src.Controleur;

/**
 * Classe FrameCritique qui gère la fenêtre d'affichage des chemins critiques du projet.
 * Cette classe hérite de JFrame pour créer une fenêtre modale qui affiche les chemins critiques
 * du graphe MPM. Elle permet de visualiser tous les chemins critiques dans une fenêtre séparée.
 */
public class FrameCritique extends JFrame
{
    /** Contrôleur principal de l'application */
    private Controleur ctrl;
    
    /** Panel contenant l'affichage des chemins critiques */
    private PanelCritique panelCritique;
    
    /**
     * Constructeur de la fenêtre des chemins critiques
     * @param ctrl Le contrôleur principal de l'application
     */
    public FrameCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setTitle("Chemins Critiques");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.recreerPanelCritique();
    }   

    /**
     * Recrée le panel d'affichage des chemins critiques.
     * Cette méthode est appelée pour mettre à jour l'affichage des chemins critiques
     * lorsque le graphe est modifié ou lorsque la fenêtre est ouverte.
     */
    public void recreerPanelCritique() 
    {
        if (this.panelCritique != null) this.remove(this.panelCritique);

        this.panelCritique = new PanelCritique(this.ctrl);
        this.add(panelCritique, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}
