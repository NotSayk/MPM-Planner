package src.Ihm;

import javax.swing.JFrame;
import src.Controleur;
import src.Metier.GrapheMPM;

public class FrameMPM extends JFrame
{
    
    private Controleur ctrl;
    private GrapheMPM graphe;

    private PanelMPM  panelMPM;
    private PanelPara panelPara;

    public FrameMPM(Controleur ctrl, GrapheMPM graphe) 
    {
        this.ctrl   = ctrl;
        this.graphe = graphe;
        this.panelPara = new PanelPara(this.ctrl);

        this.setTitle("MPM - Choix des paramètres");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        this.add(this.panelPara);

        this.setVisible(true);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void changerPanel() 
    {
        this.getContentPane().removeAll();
        this.panelMPM  = new PanelMPM(this.graphe, this.ctrl);
        this.add(this.panelMPM);
        this.revalidate();
        this.repaint();
    }

    public void resetPositions() 
    {
        this.panelMPM.resetPositions();
    }

    public void changerTheme() 
    {
        String currentTheme = this.panelMPM.getTheme();
        
        if (currentTheme.equals("LIGHT")) {
            this.panelMPM.setTheme("DARK");
        } else {
            this.panelMPM.setTheme("LIGHT");
        }
        
        this.repaint();
    }
}
