package src.Ihm;

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
        this.setLocationRelativeTo(null);

        this.add(new PanelModification(ctrl));

        this.setVisible(true);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
