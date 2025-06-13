package src.ihm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import src.Controleur;

public class FrameCalcul extends JFrame 
{
    private Controleur ctrl;
    
    private PanelCalcul panelCalcul;
    private JButton btnFermer;
    private JButton btnReinitialiser;
    
    public FrameCalcul(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        
        // Configuration de la fenêtre
        this.setTitle("Détail des calculs MPM");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setSize(600, 500);
        this.setLocationRelativeTo(null);
        
        // Création du panel principal
        this.panelCalcul = new PanelCalcul(this.ctrl);
        
        // Création des boutons
        this.btnReinitialiser = new JButton("Réinitialiser");
        this.btnReinitialiser.addActionListener(e -> this.panelCalcul.reinitialiser());
        
        this.btnFermer = new JButton("Fermer");
        this.btnFermer.addActionListener(e -> this.setVisible(false));
        
        // Ajout des composants à la fenêtre
        this.setLayout(new BorderLayout());
        this.add(this.panelCalcul, BorderLayout.CENTER);
        
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoutons.add(this.btnReinitialiser);
        panelBoutons.add(this.btnFermer);
        this.add(panelBoutons, BorderLayout.SOUTH);
        
        // Gestion de la fermeture de la fenêtre
        this.addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                setVisible(false);
            }
        });
    }
    
    /**
     * Ajoute les calculs des dates au plus tôt pour un niveau spécifique
     * @param niveau Le niveau des tâches à calculer
     */
    public void ajouterCalculDatesTotNiveau(int niveau) 
    {
        this.panelCalcul.ajouterCalculDatesTotNiveau(niveau);
    }
    
    /**
     * Ajoute les calculs des dates au plus tard pour un niveau spécifique
     * @param niveau Le niveau des tâches à calculer
     */
    public void ajouterCalculDatesTardNiveau(int niveau) 
    {
        this.panelCalcul.ajouterCalculDatesTardNiveau(niveau);
    }
    
    /**
     * Rafraîchit l'affichage des calculs
     */
    public void actualiser() 
    {
        this.panelCalcul.actualiser();
    }
}