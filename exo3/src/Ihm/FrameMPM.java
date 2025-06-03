package src.Ihm;

import javax.swing.JFrame;
import src.Controleur;
import src.Metier.GrapheMPM;

public class FrameMPM extends JFrame
{
    
    private Controleur ctrl;
    private GrapheMPM graphe;

    public FrameMPM(Controleur ctrl, GrapheMPM graphe) 
    {
        this.ctrl = ctrl;
        this.graphe = graphe;

        setTitle("MPM - Choix des paramètres");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.add(new PanelPara(ctrl));

        this.setVisible(true);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void changerPanel() 
    {
        this.getContentPane().removeAll();
        this.add(new PanelMPM(this.graphe, this.ctrl));
        this.revalidate();
        this.repaint();
    }

}
