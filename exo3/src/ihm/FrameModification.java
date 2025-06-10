package src.ihm;

import java.awt.Frame;
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
