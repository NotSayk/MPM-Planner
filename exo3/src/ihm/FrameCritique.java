package src.ihm;

import java.awt.*;
import javax.swing.*;
import src.Controleur;

public class FrameCritique extends JFrame
{
    private Controleur ctrl;
    private PanelCritique panelCritique;
    
    public FrameCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setTitle("Chemins Critiques");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.recreerPanelCritique();
    }   

    public void recreerPanelCritique() 
    {
        if (this.panelCritique != null) this.remove(this.panelCritique);

        this.panelCritique = new PanelCritique(this.ctrl);
        this.add(panelCritique, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}
