package src.ihm;

import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JFrame;
import src.Controleur;

/**
 * Fenêtre de modification du graphe MPM
 * 
 * Cette fenêtre permet de :
 * - Modifier les propriétés des tâches (nom, durée, prédécesseurs, successeurs)
 * - Afficher les tâches dans une grille de données
 * - Gérer les modifications via un panneau dédié
 */
public class FrameModification extends JFrame
{  
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur        ctrl;              // Référence au contrôleur
    private PanelModification panelModification; // Panneau de modification

    /**
     * Crée une nouvelle fenêtre de modification
     * 
     * @param ctrl Le contrôleur de l'application
     */
    public FrameModification(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        Frame frameMPM = this.ctrl.getFrameMPM();

        this.setTitle    ("MPM - Modification du graphe");
        this.setSize     (500 ,600       );
        this.setResizable(false);

        // Place la fenêtre à droite de la frame principale si elle ne dépasse pas 80% de la largeur de l'écran
        if(frameMPM.getX() + frameMPM.getWidth() < 0.8 * Toolkit.getDefaultToolkit().getScreenSize().width)
            this.setLocation(frameMPM.getX() + frameMPM.getWidth() + 25, frameMPM.getY());

        this.panelModification = new PanelModification(ctrl);
        this.add(this.panelModification);
        
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Récupère le panneau de modification
     * 
     * @return Le panneau de modification
     */
    public PanelModification getPanelModification() 
    {
        return this.panelModification;
    }

    /**
     * Récupère le modèle de la grille de données
     * 
     * @return Le modèle de la grille
     */
    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        return this.panelModification.getGrilleDonneesModel();
    }
}
