package ihm;

import controleur.ControleurMPM;
import ihm.composants.Entite;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FrameMPM extends JFrame {
    private ControleurMPM controleur;
    private JPanel panelMPM;
    private String theme;

    public FrameMPM(ControleurMPM controleur) {
        this.controleur = controleur;
        this.theme = "clair";
        
        this.setTitle("Graphe MPM");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {
        // Créer le menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuFichier = new JMenu("Fichier");
        JMenuItem itemCharger = new JMenuItem("Charger");
        JMenuItem itemSauvegarder = new JMenuItem("Sauvegarder");
        menuFichier.add(itemCharger);
        menuFichier.add(itemSauvegarder);
        
        JMenu menuEdition = new JMenu("Édition");
        JMenuItem itemModifier = new JMenuItem("Modifier");
        menuEdition.add(itemModifier);
        
        JMenu menuAffichage = new JMenu("Affichage");
        JMenuItem itemTheme = new JMenuItem("Changer thème");
        JMenuItem itemReset = new JMenuItem("Réinitialiser positions");
        menuAffichage.add(itemTheme);
        menuAffichage.add(itemReset);
        
        menuBar.add(menuFichier);
        menuBar.add(menuEdition);
        menuBar.add(menuAffichage);
        
        this.setJMenuBar(menuBar);
        
        // Créer le panel principal
        this.panelMPM = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerGraphe(g);
            }
        };
        this.panelMPM.setPreferredSize(new Dimension(800, 600));
        
        // Ajouter les listeners
        itemCharger.addActionListener(e -> controleur.chargerFichier());
        itemSauvegarder.addActionListener(e -> controleur.sauvegarder());
        itemModifier.addActionListener(e -> controleur.afficherModification());
        itemTheme.addActionListener(e -> controleur.changerTheme());
        itemReset.addActionListener(e -> controleur.resetPositions());
        
        // Ajouter le panel à la fenêtre
        this.add(panelMPM);
    }

    private void dessinerGraphe(Graphics g) {
        // TODO: Implémenter le dessin du graphe
    }

    public void changerPanel() {
        this.panelMPM.repaint();
    }

    public void changerTheme() {
        this.theme = this.theme.equals("clair") ? "sombre" : "clair";
        this.panelMPM.repaint();
    }

    public void resetPositions() {
        // TODO: Implémenter la réinitialisation des positions
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setCritique(boolean critique) {
        // TODO: Implémenter l'affichage des chemins critiques
    }

    public JPanel getPanelMPM() {
        return panelMPM;
    }

    public String getTheme() {
        return theme;
    }
} 