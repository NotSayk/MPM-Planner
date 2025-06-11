package src.ihm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import src.Controleur;
import src.utils.BtnUtils;
import src.utils.ErrorUtils;

public class PanelButton extends JPanel implements ActionListener 
{

    private Controleur ctrl;

    private JButton    btnPlusTot;
    private JButton    btnPlusTard;
    private JButton    btnReset;
    private JButton    btnTheme;
    private JButton    btnCritique;

    public PanelButton(Controleur ctrl) 
    {
        this.ctrl           = ctrl;
        
        this.setBackground(new Color(45, 45, 55));
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.btnPlusTot  = BtnUtils.creerBtn("Plus tôt"       , new Color(0, 183, 14)  , "Afficher les dates au plus tôt");
        this.btnPlusTard = BtnUtils.creerBtn("Plus tard"      , new Color(255, 27, 14) , "Afficher les dates au plus tard");
        this.btnReset    = BtnUtils.creerBtn("Réinitialiser"  , new Color(255, 193, 7) , "Remettre à zéro les positions");
        this.btnCritique = BtnUtils.creerBtn("Chemin critique", new Color(220, 53, 69) , "Afficher/masquer le chemin critique");
        this.btnTheme    = BtnUtils.creerBtn("Changer thème"  , new Color(23, 162, 184), "Basculer entre les thèmes");
        
        this.btnPlusTard.setEnabled(false);

        this.add(btnPlusTot);
        this.add(btnPlusTard);
        this.add(btnReset);
        this.add(btnCritique);
        this.add(btnTheme);

        this.btnPlusTot. addActionListener(this);
        this.btnPlusTard.addActionListener(this);
        this.btnReset   .addActionListener(this);
        this.btnTheme   .addActionListener(this);
        this.btnCritique.addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        PanelMPM panelMPM = this.ctrl.getFrameMPM().getPanelMPM();

        JButton sourceButton = (JButton) e.getSource();
        this.animationBouttonClique(sourceButton);
        
        if (e.getSource() == this.btnPlusTot) 
        {
            panelMPM.afficherDateTot();
            if (this.ctrl.isGriseTot())
            {
                this.btnPlusTard.setEnabled(true);
                this.btnPlusTot.setEnabled(false);
                this.majBouttonEtat();
            }
        }
        else if (e.getSource() == this.btnPlusTard) 
        {
            panelMPM.afficherDateTard();
            if (this.ctrl.isGriseTard())
            {
                this.btnPlusTard.setEnabled(false);
                this.majBouttonEtat();
            }
        }
        else if (e.getSource() == this.btnReset) 
        {
            this.ctrl       .resetPositions();
            this.btnPlusTard.setEnabled(false);
            this.btnPlusTot .setEnabled(true);
            panelMPM   .cacherDates();
            panelMPM   .resetScale();
            this.majBouttonEtat();
            ErrorUtils.showSucces("Les tâches ont été réinitialisées !");
        
        }
        else if (e.getSource() == this.btnCritique) 
        {
            boolean critique = !this.ctrl.isCritique();

            this.ctrl.setCritique(critique);
            panelMPM.afficherCheminCritique(critique);
            this.btnCritique.setText(critique ? "Masquer critique" : "Chemin critique");
        }
        else if (e.getSource() == this.btnTheme) 
        {
            this.ctrl.changerTheme();
            panelMPM.repaint();
            ErrorUtils.showSucces("Le thème a été modifié !");
        }
    }
    
    private void animationBouttonClique(JButton button) 
    {
        Timer timer = new Timer(100, e -> button.repaint());
        timer.setRepeats(false);
        timer.start();
    }
    
    private void majBouttonEtat() 
    {
        SwingUtilities.invokeLater(() -> 
        {
            this.btnPlusTot.repaint();
            this.btnPlusTard.repaint();
        });
    }
    
    public void setCritiqueButton(boolean critique) 
    { 
        this.ctrl.setCritique(critique);
        this.btnCritique.setText(critique ? "Masquer critique" : "Chemin critique");
    }
    
    public void updateTheme(boolean darkMode) 
    {
        Color newBackground = darkMode ? new Color(45, 45, 55) : new Color(240, 240, 245);
        this.setBackground(newBackground);
        this.repaint();
    }
}