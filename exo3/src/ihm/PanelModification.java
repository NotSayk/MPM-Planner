package src.ihm;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import src.Controleur;
import src.metier.TacheMPM;
import src.utils.ErrorUtils;

/**
 * Panneau de modification des tâches du graphe MPM
 * 
 * Ce panneau permet de :
 * - Afficher les tâches dans une grille de données
 * - Ajouter de nouvelles tâches
 * - Modifier la durée des tâches existantes
 * - Gérer les sélections multiples
 */
public class PanelModification extends JPanel implements ActionListener
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur         ctrl;              // Référence au contrôleur
    private GrilleDonneesModel grilleDonneesModel; // Modèle de la grille de données
    private JPanel             panelInfo;         // Panneau d'informations et contrôles
    private JTable             tblGrilleDonnees;  // Tableau des tâches
    private JTextField         txtTacheDuree;     // Champ de saisie de la durée
    private JTextField         txtTacheNom;       // Champ de saisie du nom
    private JButton            btnAjouter;        // Bouton d'ajout de tâche
    private JButton            btnMaj;           // Bouton de mise à jour

    /**
     * Crée un nouveau panneau de modification
     * 
     * @param ctrl Le contrôleur de l'application
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

        // Configuration de la sélection multiple
        this.tblGrilleDonnees.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Gestion de la sélection des tâches
        this.tblGrilleDonnees.getSelectionModel().addListSelectionListener(e -> 
        {
            if (!e.getValueIsAdjusting())
            {
                int[] selectedRows = this.tblGrilleDonnees.getSelectedRows();

                if (selectedRows.length == 1)
                {
                    int selectedRow   = selectedRows[0];
                    Object dureeValue = this.tblGrilleDonnees.getValueAt(selectedRow, 1);

                    if (dureeValue != null) 
                        this.txtTacheDuree.setText(dureeValue.toString());
                    else                    
                        this.txtTacheDuree.setText("");
                }
                else
                {
                    this.txtTacheDuree.setText("");
                }
            }
        });

        this.tblGrilleDonnees.setFillsViewportHeight(true);

        // Création des composants de saisie
        this.txtTacheNom   = new JTextField(20);
        this.btnAjouter    = new JButton("Ajouter");
        this.txtTacheDuree = new JTextField(10);
        this.btnMaj        = new JButton("Mettre à jour");

        // Organisation des composants
        this.panelInfo.add(new JLabel("Tâche à ajouter :"));
        this.panelInfo.add(this.txtTacheNom);
        this.panelInfo.add(this.btnAjouter);
        this.panelInfo.add(new JLabel("Durée de la tâche :"));
        this.panelInfo.add(this.txtTacheDuree);
        this.panelInfo.add(this.btnMaj);

        spGrilleDonnees = new JScrollPane(this.tblGrilleDonnees);

        this.add(spGrilleDonnees, BorderLayout.NORTH);
        this.add(panelInfo      , BorderLayout.SOUTH);

        // Ajout des écouteurs d'événements
        this.btnMaj    .addActionListener(this);
        this.btnAjouter.addActionListener(this);
    }

    /**
     * Gère les actions des boutons
     * 
     * @param e L'événement d'action
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
                            this.ctrl.mettreAJourDureeTache(tacheSelectionnee, duree);
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
                    int positionInsertion = lignesSelectionnees[lignesSelectionnees.length - 1] + 1;
                    this.ctrl.ajouterTacheAPosition(temp, positionInsertion);
                } 
                else 
                {
                    this.ctrl.ajouterTacheAPosition(temp, 1);
                    niveau = 1; 
                }
            
                if (lignesSelectionnees.length > 0) 
                {
                    int selectedIndex = lignesSelectionnees[lignesSelectionnees.length - 1];
                    TacheMPM tacheSelectionee = this.ctrl.getEntites().get(selectedIndex).getTache();
                    niveau = tacheSelectionee.getNiveau() + 1;
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
     * Récupère le modèle de la grille de données
     * 
     * @return Le modèle de la grille
     */
    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        return this.grilleDonneesModel;
    }
}
