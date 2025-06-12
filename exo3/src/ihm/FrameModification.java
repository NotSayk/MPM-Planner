package src.ihm;

import javax.swing.JFrame;
import src.Controleur;

public class FrameModification extends JFrame
{  
    private Controleur        ctrl;
    private PanelModification panelModification;


    public FrameModification(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setTitle    ("MPM - Modification du graphe");
        this.setSize     (300 ,550                      );


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
