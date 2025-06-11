package ihm;

import controleur.ControleurMPM;
import metier.CheminCritique;
import javax.swing.*;
import java.awt.*;

public class FrameCritique extends JFrame {
    private ControleurMPM controleur;
    private JTextArea textArea;

    public FrameCritique(ControleurMPM controleur) {
        this.controleur = controleur;
        
        this.setTitle("Chemins critiques");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        initComponents();
    }

    private void initComponents() {
        // Créer la zone de texte
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        // Ajouter la zone de texte à la fenêtre
        this.add(scrollPane);
        
        // Afficher les chemins critiques
        afficherCheminsCritiques();
    }

    private void afficherCheminsCritiques() {
        StringBuilder sb = new StringBuilder();
        for (CheminCritique chemin : controleur.getCheminsCritiques()) {
            sb.append(chemin.toString()).append("\n");
        }
        textArea.setText(sb.toString());
    }
} 