package src.Ihm;

import java.util.List;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import src.Controleur;
import src.Metier.TacheMPM;


public class PanelAjout extends JPanel
{
    private Controleur ctrl;

    public PanelAjout(Controleur ctrl)
    {
        this.ctrl = ctrl;

        this.setLayout(null);
        this.setOpaque(false); 
        // Initialisation des composants

        List<TacheMPM> lstTache = this.ctrl.getTaches();
        
        TacheMPM       tache    = null;
        JRadioButton   rb       = null;

        for (int i = 0; i < lstTache.size(); i++) 
        {
            tache = lstTache.get(i);
            rb    = new JRadioButton(tache.getNom());

            rb.setBounds(10, 10 + i * 30, 200, 20);
            this.add(rb);
        }
    }
}
