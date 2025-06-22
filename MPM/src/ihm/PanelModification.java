package src.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import src.Controleur;
import src.metier.TacheMPM;
import src.utils.BtnUtils;
import src.utils.ErrorUtils;

/**
 * Classe PanelModification qui gère l'interface de modification des tâches du graphe MPM.
 * Cette classe hérite de JPanel et implémente ActionListener pour gérer les événements des boutons.
 * Elle permet d'ajouter de nouvelles tâches et de modifier la durée des tâches existantes.
 */
public class PanelModification extends JPanel implements ActionListener
{
    private Controleur         ctrl;

	private GrilleDonneesModel grilleDonneesModel;

	private JPanel             panelInfo;

    private JTable             tblGrilleDonnees;
    private JTextField         txtTacheDuree;
    private JTextField         txtTacheNom;
    private JButton            btnAjouter;
    private JButton            btnMaj;

    /**
     * Constructeur du panel de modification
     * @param ctrl Le contrôleur principal de l'application
     */
    public PanelModification(Controleur ctrl)
    {
        this.ctrl = ctrl;
        this.setLayout(new BorderLayout());

        JScrollPane spGrilleDonnees;

        // Création des composants
        this.grilleDonneesModel = new GrilleDonneesModel(ctrl);
        this.tblGrilleDonnees   = new JTable(this.grilleDonneesModel);
        this.panelInfo          = new JPanel(new GridLayout(2, 3));

        this.tblGrilleDonnees.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Gestion de la sélection des lignes dans le tableau
        this.tblGrilleDonnees.getSelectionModel().addListSelectionListener(e -> 
        {
            if (!e.getValueIsAdjusting())
            {
                int[] selectedRows = this.tblGrilleDonnees.getSelectedRows();

                if (selectedRows.length == 1)
                {
                    int selectedRow   = selectedRows[0];
                    Object dureeValue = this.tblGrilleDonnees.getValueAt(selectedRow, 1);

                    if (dureeValue != null) this.txtTacheDuree.setText(dureeValue.toString());
                    else                    this.txtTacheDuree.setText("");
                }
                else
                {
                    this.txtTacheDuree.setText("");
                }
            }
        });

        this.tblGrilleDonnees.setFillsViewportHeight(true);

        this.txtTacheNom   = new JTextField(20);
        this.btnAjouter    = BtnUtils.creerBtn("Ajouter", new Color(0, 123, 255), "Ajouter une nouvelle tâche");
        this.txtTacheDuree = new JTextField(10);
        this.btnMaj        = BtnUtils.creerBtn("Mettre à jour", new Color(255, 221, 51), "Mettre à jour les tâches sélectionnées");

        this.panelInfo.add(new JLabel("Tâche à ajouter :"));
        this.panelInfo.add(this.txtTacheNom);
        this.panelInfo.add(this.btnAjouter);
        this.panelInfo.add(new JLabel("Durée de la tâche :"));
        this.panelInfo.add(this.txtTacheDuree);
        this.panelInfo.add(this.btnMaj);

        spGrilleDonnees = new JScrollPane(this.tblGrilleDonnees);

        this.add(spGrilleDonnees, BorderLayout.NORTH);
        this.add(panelInfo      , BorderLayout.SOUTH);

        this.btnMaj    .addActionListener(this);
        this.btnAjouter.addActionListener(this);
    }

    /**
     * Gère les événements des boutons
     * @param e L'événement déclenché
     */
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == this.btnMaj) 
        {
            int[] lignesSelectionnees = this.tblGrilleDonnees.getSelectedRows();
            int   duree;
            
            // Vérifier si la ligne sélectionnée est la première ou dernière du tableau
            if (lignesSelectionnees.length == 1 && 
                    (lignesSelectionnees[0] == 0 || lignesSelectionnees[0] == this.grilleDonneesModel.getRowCount() - 1)) 
            {
                ErrorUtils.showError("Cette tâche ne peut pas être modifiée.");
                return;
            }
            
            if (lignesSelectionnees.length >= 1) 
            {
                String dureeStr = this.txtTacheDuree.getText().trim();
                
                // Vérifier que la durée n'est pas vide
                if (dureeStr.isEmpty()) 
                {
                    ErrorUtils.showError("Veuillez saisir une durée.");
                    return;
                }
                
                try 
                {
                    duree = Integer.parseInt(dureeStr);
                    
                    // Vérifier que la durée est positive
                    if (duree < 0) 
                    {
                        ErrorUtils.showError("La durée doit être un nombre positif.");
                        return;
                    }
                    
                    // Mettre à jour toutes les tâches sélectionnées
                    for (int i = 0; i < lignesSelectionnees.length; i++) 
                    {
                        int tacheSelectionnee = lignesSelectionnees[i];
                        if (tacheSelectionnee >= 0 && tacheSelectionnee < this.tblGrilleDonnees.getRowCount()) 
                        {
                            this.ctrl.mettreAJourDureeTache(tacheSelectionnee, duree);
                        } 
                        else 
                        {
                            ErrorUtils.showError("Tâche non valide.");
                            return;
                        }
                    }
                    // Rafraîchir l'affichage une seule fois après toutes les modifications
                    this.grilleDonneesModel.refreshTab();
                } 
                catch (NumberFormatException ex) 
                {
                    ErrorUtils.showError("La durée doit être un nombre entier.");
                }
            } 
            else 
            {
                ErrorUtils.showError("Aucune tâche sélectionnée.");
            }
        }
        else if (e.getSource() == this.btnAjouter) 
        {
            String nomTache = this.txtTacheNom.getText().trim();
            int    niveau   = 0;

            if (!nomTache.isEmpty()) 
            {
                TacheMPM temp = new TacheMPM(nomTache, 0, new ArrayList<>());
                int[] lignesSelectionnees = this.tblGrilleDonnees.getSelectedRows();

                if (lignesSelectionnees.length > 0) 
                {
                    // Insérer après la dernière ligne sélectionnée
                    this.ctrl.ajouterTacheAPosition(temp, lignesSelectionnees[lignesSelectionnees.length - 1] + 1);
                } 
                else 
                {
                    ErrorUtils.showError("Aucune tâche sélectionnée pour l'insertion.");
                    return;
                }
            
                if (lignesSelectionnees.length > 0) 
                {
                    int index = lignesSelectionnees[lignesSelectionnees.length - 1];
                    niveau = this.ctrl.getEntites().get(index).getTache().getNiveau() + 1;
                }
                this.ctrl.setNiveauTache(temp, niveau);
                this.grilleDonneesModel.refreshTab();
                this.txtTacheNom.setText("");
            } 
            else 
            {
                ErrorUtils.showError("Le nom de la tâche ne peut pas être vide.");
            }
        }
    }
    
    /**
     * Retourne le modèle de données de la grille
     * @return Le modèle de données utilisé pour afficher les informations dans la grille
     */
    public GrilleDonneesModel getGrilleDonneesModel() { return this.grilleDonneesModel; }
}
