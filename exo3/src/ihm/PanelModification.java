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
        this.btnAjouter    = new JButton("Ajouter");
        this.txtTacheDuree = new JTextField(10);
        this.btnMaj        = new JButton("Mettre à jour");

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
                    this.ctrl.getFrameMPM().getPanelMPM().afficherCheminCritique(this.ctrl.isCritique());
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
    
    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        return this.grilleDonneesModel;
    }
}
