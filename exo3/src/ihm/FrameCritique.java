package src.ihm;

import javax.swing.*;
import java.awt.*;
import src.Controleur;

public class FrameCritique extends JFrame
{
    private Controleur ctrl;

    private PanelCritique panelCritique;
    
    public FrameCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setTitle("Chemins Critiques");
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        
        this.panelCritique = new PanelCritique(this.ctrl);
        this.add(panelCritique, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }   
}
