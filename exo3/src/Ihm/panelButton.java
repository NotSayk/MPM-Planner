package src.Ihm;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import src.Controleur;

public class panelButton extends JPanel implements ActionListener
{

    private JButton btnPlusTot;
    private JButton btnPlusTard;
    private JButton btnReset;
    private JButton btnTheme;
    private Controleur ctrl;

    private PanelMPM panelMPM;
    

    public panelButton(Controleur ctrl, PanelMPM panelMPM)
    {
        this.ctrl = ctrl;
        this.panelMPM = panelMPM;

        this.setBackground(new Color(ABORT, 51, 51, 51));
        this.setLayout(new FlowLayout());

        this.btnPlusTot  = new JButton("+ tôt");
        this.btnPlusTard = new JButton("+ tard");
        this.btnReset    = new JButton("Réinitialiser");
        this.btnTheme    = new JButton("Changer le thème");

        this.add(this.btnPlusTot);
        this.add(this.btnPlusTard);
        this.add(this.btnReset);
        this.add(this.btnTheme);

        this.btnPlusTard.setEnabled(false); 

        this.btnPlusTot .addActionListener(this);
        this.btnPlusTard.addActionListener(this);
        this.btnReset   .addActionListener(this);
        this.btnTheme   .addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnPlusTot) 
        {
            // Logique pour le bouton "+ tôt"
            panelMPM.afficherDateTot();
            if ( this.panelMPM.estGrise() )
            {
                this.btnPlusTard.setEnabled(true); 
                this.btnPlusTot.setEnabled(false);
            }

        } 
        else if (e.getSource() == this.btnPlusTard) 
        {
            // Logique pour le bouton "+ tard"
            panelMPM.afficherDateTard();
        } 
        else if (e.getSource() == this.btnReset) 
        {
            this.ctrl.resetPositions(); 
            this.btnPlusTard.setEnabled(false);
            this.btnPlusTot.setEnabled(true);
            panelMPM.cacherDates();
        } 
        else if (e.getSource() == this.btnTheme) 
        {
            // Logique pour le bouton "Changer le thème"
        }
    }

}