package src_save.ihm;

import java.awt.Frame;
import javax.swing.JFrame;

import src_save.Controleur;

public class FrameModification extends JFrame
{  
    private Controleur        ctrl;
    private PanelModification panelModification;


    public FrameModification(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        Frame frameMPM = this.ctrl.getFrameMPM();

        this.setTitle    ("MPM - Modification du graphe");
        this.setSize     (600 ,600       );
        this.setResizable(false);


        this.setLocation(frameMPM.getX() + frameMPM.getWidth() + 25, frameMPM.getY());

        this.panelModification = new PanelModification(ctrl);
        this.add(this.panelModification);

        
        this.setVisible(false);
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}
