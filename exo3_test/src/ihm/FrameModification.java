package ihm;

import controleur.ControleurMPM;
import metier.TacheMPM;
import javax.swing.*;
import java.awt.*;

public class FrameModification extends JFrame {
    private ControleurMPM controleur;
    private GrilleDonneesModel grilleDonneesModel;
    private JTable tableDonnees;

    public FrameModification(ControleurMPM controleur) {
        this.controleur = controleur;
        
        this.setTitle("Modification des tâches");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        initComponents();
    }

    private void initComponents() {
        // Créer le modèle de données
        this.grilleDonneesModel = new GrilleDonneesModel(controleur);
        
        // Créer la table
        this.tableDonnees = new JTable(grilleDonneesModel);
        JScrollPane scrollPane = new JScrollPane(tableDonnees);
        
        // Ajouter la table à la fenêtre
        this.add(scrollPane);
    }

    public GrilleDonneesModel getGrilleDonneesModel() {
        return grilleDonneesModel;
    }
} 