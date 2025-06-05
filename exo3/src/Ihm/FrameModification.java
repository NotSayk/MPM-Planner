package src.Ihm;

import java.awt.Frame;
import javax.swing.JFrame;
import src.Controleur;
import src.Metier.GrapheMPM;

public class FrameModification extends JFrame
{
    
    private Controleur ctrl;
    private GrapheMPM graphe;
    private PanelMPM panelMPM;

    public FrameModification(Controleur ctrl, GrapheMPM graphe) 
    {
        this.ctrl = ctrl;
        this.graphe = graphe;

        this.setTitle("MPM - Modification du graphe");
        this.setSize(400, 600);
        this.setResizable(false);

        Frame frameMPM = this.ctrl.getFrameMPM();
        this.setLocation(frameMPM.getX() + frameMPM.getWidth() + 25, frameMPM.getY());

        this.add(new PanelModification(ctrl));

        this.setVisible(false);
        
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

}
