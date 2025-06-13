package src.ihm;

import javax.swing.JFrame;
import src.Controleur;

/**
 * Classe FrameModification qui gère la fenêtre de modification du graphe MPM.
 * Cette classe hérite de JFrame pour créer une fenêtre modale permettant de modifier
 * les propriétés des tâches et leurs relations dans le graphe.
 */
public class FrameModification extends JFrame
{  
    /** Contrôleur principal de l'application */
    private Controleur        ctrl;

    /** Panel contenant les contrôles de modification du graphe */
    private PanelModification panelModification;

    /**
     * Constructeur de la fenêtre de modification
     * @param ctrl Le contrôleur principal de l'application
     */
    public FrameModification(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setTitle("MPM - Modification du graphe");
        this.setSize (300 ,550               );

        this.panelModification = new PanelModification(this.ctrl);
        this.add(this.panelModification);
        
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Retourne le panel de modification
     * @return Le panel contenant les contrôles de modification
     */
    public PanelModification getPanelModification() 
    {
        return this.panelModification;
    }

    /**
     * Retourne le modèle de données de la grille
     * @return Le modèle de données utilisé pour afficher les informations dans la grille
     */
    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        return this.panelModification.getGrilleDonneesModel();
    }
}
