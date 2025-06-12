package src.ihm;

import javax.swing.*;
import java.awt.*;
import src.Controleur;

public class FrameCritique extends JFrame
{
    private PanelCritique panelCritique;
    private Controleur ctrl;
    
    public FrameCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        setTitle("Chemins Critiques");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        recreerPanelCritique();
    }   

    public void recreerPanelCritique() 
    {
        if (panelCritique != null) {
            remove(panelCritique);
        }
        panelCritique = new PanelCritique(this.ctrl);
        add(panelCritique, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
