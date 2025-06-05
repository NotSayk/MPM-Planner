package src.Ihm;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import src.Controleur;
import src.utils.ErrorUtils;

public class panelButton extends JPanel implements ActionListener
{
    private Controleur ctrl;

    private JButton btnPlusTot;
    private JButton btnPlusTard;
    private JButton btnReset;
    private JButton btnTheme;
    private JButton btnCritique;

    private boolean cheminCritique;

    private PanelMPM panelMPM;
    

    public panelButton(Controleur ctrl, PanelMPM panelMPM)
    {
        this.ctrl           = ctrl;
        this.panelMPM       = panelMPM;
        this.cheminCritique = true;

        this.setBackground(new Color(ABORT, 51, 51, 51));
        this.setLayout    (new FlowLayout());

        this.btnPlusTot  = new JButton("+ tôt");
        this.btnPlusTard = new JButton("+ tard");
        this.btnReset    = new JButton("Réinitialiser");
        this.btnTheme    = new JButton("Changer le thème");
        this.btnCritique = new JButton("Chemin critique");

        this.add(this.btnPlusTot);
        this.add(this.btnPlusTard);
        this.add(this.btnReset);
        this.add(this.btnCritique);
        this.add(this.btnTheme);

        this.btnPlusTard.setEnabled(false); 

        this.btnPlusTot .addActionListener(this);
        this.btnPlusTard.addActionListener(this);
        this.btnReset   .addActionListener(this);
        this.btnTheme   .addActionListener(this);
        this.btnCritique.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnPlusTot) 
        {
            panelMPM.afficherDateTot();
            if ( this.panelMPM.estGriseTot() )
            {
                this.btnPlusTard.setEnabled(true); 
                this.btnPlusTot.setEnabled (false);
            }

        } 
        else if (e.getSource() == this.btnPlusTard) 
        {
            this.panelMPM.afficherDateTard();
            if(this.panelMPM.estGriseTard()) this.btnPlusTard.setEnabled(false); 
        }
        else if (e.getSource() == this.btnReset) 
        {
            this.ctrl       .resetPositions(); 
            this.btnPlusTard.setEnabled(false);
            this.btnPlusTot .setEnabled (true);
            this.panelMPM.cacherDates();
            ErrorUtils.showInfo("les tâches ont été réinitialisées");
        }
        else if (e.getSource() == this.btnCritique) 
        {
            this.panelMPM.afficherCheminCritique(this.cheminCritique);
            this.cheminCritique = !this.cheminCritique;
        }
        else if (e.getSource() == this.btnTheme) 
        {
            this.ctrl.changerTheme();
            this.panelMPM.repaint();
        }
    }

    public void setCritiqueButton(boolean critique) { this.cheminCritique = critique ; }

}