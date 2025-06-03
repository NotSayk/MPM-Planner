package src.Ihm;

import javax.swing.JFrame;
import src.Controleur;

public class FrameMPM extends JFrame
{
    
    private Controleur ctrl;

    public FrameMPM(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        setTitle("MPM - Choix des paramètres");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        this.add(new PanelPara(this.ctrl));

        this.setVisible(true);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
