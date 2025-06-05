package src.Ihm;

import javax.swing.JPanel;
import src.Controleur;
import src.Metier.TacheMPM;
import javax.swing.JRadioButton;
import java.util.List;


public class PanelAjout extends JPanel
{
    private Controleur ctrl;

    public PanelAjout(Controleur ctrl)
    {
        this.ctrl = ctrl;
        this.setLayout(null);
        this.setOpaque(false); 
        // Initialisation des composants

        List<TacheMPM> lstTache =   this.ctrl.getTaches();

        for (int i = 0; i < lstTache.size(); i++) 
        {
            TacheMPM tache = lstTache.get(i);
            tache.getNom();
            JRadioButton rb = new JRadioButton(tache.getNom());
            rb.setBounds(10, 10 + i * 30, 200, 20);
            this.add(rb);
        }
    }
}
