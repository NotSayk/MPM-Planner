package src.ihm;

import java.awt.Frame;
import java.awt.Toolkit;
import javax.swing.JFrame;
import src.Controleur;

public class FrameModification extends JFrame
{  
    private Controleur        ctrl;
    private PanelModification panelModification;


    public FrameModification(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        Frame frameMPM = this.ctrl.getFrameMPM();

        this.setTitle    ("MPM - Modification du graphe");
        this.setSize     (500 ,600       );
        this.setResizable(false);

        //place la fenêtre à droite de la frame principale si elle ne dépace pas 80% de la largeur de l'écran
        if(frameMPM.getX() + frameMPM.getWidth() < 0.8 * Toolkit.getDefaultToolkit().getScreenSize().width)
            this.setLocation(frameMPM.getX() + frameMPM.getWidth() + 25, frameMPM.getY());

        this.panelModification = new PanelModification(ctrl);
        this.add(this.panelModification);

        
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public PanelModification getPanelModification() 
    {
        return this.panelModification;
    }

    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        return this.panelModification.getGrilleDonneesModel();
    }
    
}
