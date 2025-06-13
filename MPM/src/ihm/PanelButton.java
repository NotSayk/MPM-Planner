package src.ihm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import src.Controleur;
import src.utils.BtnUtils;
import src.utils.ErrorUtils;

/**
 * Classe PanelButton qui gère la barre de boutons de l'interface MPM.
 * Cette classe hérite de JPanel et implémente ActionListener pour gérer les événements des boutons.
 * Elle contient les boutons pour afficher les dates au plus tôt/tard, réinitialiser, afficher les chemins critiques
 * et changer le thème de l'application.
 */
public class PanelButton extends JPanel implements ActionListener 
{

    private Controleur ctrl;
    
    private boolean    cheminCritique;

    private JButton    btnPlusTot;
    private JButton    btnPlusTard;
    private JButton    btnReset;
    private JButton    btnTheme;
    private JButton    btnCritique;

    /**
     * Constructeur du panel de boutons
     * @param ctrl Le contrôleur principal de l'application
     */
    public PanelButton(Controleur ctrl) 
    {
        this.ctrl           = ctrl;
        this.cheminCritique = false;
        
        this.setBackground(new Color(45, 45, 55));
        this.setLayout    (new FlowLayout(FlowLayout.CENTER, 15, 10));
        this.setBorder    (BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.btnPlusTot  = BtnUtils.creerBtn("Plus tôt"       , new Color(0, 183, 14)  , "Afficher les dates au plus tôt"     );
        this.btnPlusTard = BtnUtils.creerBtn("Plus tard"      , new Color(255, 27, 14) , "Afficher les dates au plus tard"    );
        this.btnReset    = BtnUtils.creerBtn("Réinitialiser"  , new Color(255, 193, 7) , "Remettre à zéro les positions"      );
        this.btnCritique = BtnUtils.creerBtn("Chemin critique", new Color(220, 53, 69) , "Afficher/masquer le chemin critique");
        this.btnTheme    = BtnUtils.creerBtn("Changer thème"  , new Color(23, 162, 184), "Basculer entre les thèmes"          );

        this.btnPlusTard.setEnabled(false);
        this.btnCritique.setEnabled(false);

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
    
    /**
     * Gère les événements des boutons
     * @param e L'événement déclenché
     */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        PanelMPM panelMPM = this.ctrl.getFrameMPM().getPanelMPM();

        JButton sourceButton = (JButton) e.getSource();
        this.animationBouttonClique(sourceButton);
        
        if (e.getSource() == this.btnPlusTot) 
        {
            int niveau = panelMPM.afficherDateTot();
            // Notifier le contrôleur de l'affichage des dates au plus tôt pour ce niveau
            this.ctrl.notifierAffichageDateTot(niveau);
            
            if (panelMPM.estGriseTot()) 
            {
                this.btnPlusTard.setEnabled(true);
                this.btnPlusTot.setEnabled(false);
                this.majBouttonEtat();
            }
        }
        else if (e.getSource() == this.btnPlusTard) 
        {
            int niveau = panelMPM.afficherDateTard();
            // Notifier le contrôleur de l'affichage des dates au plus tard pour ce niveau
            this.ctrl.notifierAffichageDateTard(niveau);
            
            if (panelMPM.estGriseTard()) 
            {
                this.btnPlusTard.setEnabled(false);
                this.majBouttonEtat();
            }
        }
        else if (e.getSource() == this.btnReset) 
        {
            this.ctrl       .resetPositions();
            this.btnPlusTard.setEnabled(false);
            this.ctrl       .afficherCheminCritique(false);

            this.cheminCritique = false;

            this.btnCritique.setText(this.cheminCritique ? "Masquer critique" : "Chemin critique");
            this.btnCritique.setEnabled(false);
            this.btnPlusTot .setEnabled(true);
            panelMPM        .cacherDates();
            panelMPM        .resetScale();
            this.majBouttonEtat();
            ErrorUtils.showSucces("Les tâches ont été réinitialisées !");
        
        }
        else if (e.getSource() == this.btnCritique) 
        {
            this.cheminCritique = !this.cheminCritique;
            panelMPM.afficherCheminCritique(this.cheminCritique);
            this.btnCritique.setText(this.cheminCritique ? "Masquer critique" : "Chemin critique");
        }
        else if (e.getSource() == this.btnTheme) 
        {
            this.ctrl.changerTheme();
            panelMPM.repaint();
            ErrorUtils.showSucces("Le thème a été modifié !");
        }
    }

    /**
     * Retourne le bouton de chemin critique
     * @return Le bouton de chemin critique
     */
    public JButton getBtnCritique() 
    {
        return this.btnCritique;
    }
    
    /**
     * Anime le bouton lors du clic
     * @param button Le bouton à animer
     */
    private void animationBouttonClique(JButton button) 
    {
        Timer timer = new Timer(100, e -> button.repaint());
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Met à jour l'état des boutons
     */
    private void majBouttonEtat() 
    {
        SwingUtilities.invokeLater(() -> 
        {
            this.btnPlusTot.repaint();
            this.btnPlusTard.repaint();
        });
    }
    
    /**
     * Met à jour l'état du bouton de chemin critique
     * @param critique true pour afficher le chemin critique, false pour le masquer
     */
    public void setCritiqueButton(boolean critique) 
    { 
        this.cheminCritique = critique;
        this.btnCritique.setText(critique ? "Masquer critique" : "Chemin critique");
    }
    
    /**
     * Met à jour le thème du panel
     * @param darkMode true pour le thème sombre, false pour le thème clair
     */
    public void updateTheme(boolean darkMode) 
    {
        Color newBackground = darkMode ? new Color(45, 45, 55) : new Color(240, 240, 245);
        this.setBackground(newBackground);
        this.repaint();
    }
}