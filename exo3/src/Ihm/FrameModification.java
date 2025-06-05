package src.Ihm;

import java.awt.Frame;
import java.util.ResourceBundle.Control;

import javax.swing.JFrame;
import src.Controleur;
import src.Metier.GrapheMPM;

public class FrameModification extends JFrame
{
    
    private Controleur ctrl;
    private PanelModification panelModification;

    public FrameModification(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setTitle("MPM - Modification du graphe");
        this.setSize(400, 600);
        this.setResizable(false);

        Frame frameMPM = this.ctrl.getFrameMPM();
        this.setLocation(frameMPM.getX() + frameMPM.getWidth() + 25, frameMPM.getY());

        this.panelModification = new PanelModification(ctrl);
        this.add(this.panelModification);

        this.setVisible(false);
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public void afficherAjout()
    {
        this.panelModification.afficherAjout();
    }
    
}
