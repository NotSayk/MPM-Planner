package src.Ihm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;


import src.Controleur;
import src.utils.ErrorUtils;

public class PanelModification extends JPanel implements ActionListener
{
	private Controleur ctrl;

	private JTable             tblGrilleDonnees;
	private GrilleDonneesModel grilleDonneesModel;
	private JTextField         txtTacheDuree;
	private JTextField		   txtTacheNom;

	private JButton			   btnValider;
	private JButton            btnMaj;
	private JPanel             panelInfo;

	public PanelModification (Controleur ctrl)
	{
		this.ctrl = ctrl;
		this.setLayout ( new BorderLayout() );

		JScrollPane spGrilleDonnees;
		

		// Création des composants
		this.grilleDonneesModel = new GrilleDonneesModel( ctrl                    );
		this.tblGrilleDonnees   = new JTable            ( this.grilleDonneesModel );
		this.panelInfo          = new JPanel            (                         );

		this.tblGrilleDonnees.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


		this.tblGrilleDonnees.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
			{
				int[] selectedRows = this.tblGrilleDonnees.getSelectedRows();

				if (selectedRows.length == 1)
				{
					int selectedRow = selectedRows[0];

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

		this.txtTacheNom = new JTextField( 20 );
		this.btnValider   = new JButton   ( "Valider" );

		this.txtTacheDuree = new JTextField( 10                    );
		this.btnMaj        = new JButton   ( "Mettre à jour"       );

		this.panelInfo.add( new JLabel("Tâche à ajouter :") );
		this.panelInfo.add( this.txtTacheNom );
		this.panelInfo.add( this.btnValider );

		this.panelInfo.add( new JLabel    ( "Durée de la tâche :" )          );
		this.panelInfo.add( this.txtTacheDuree);
		this.panelInfo.add( this.btnMaj       );

		spGrilleDonnees   = new JScrollPane( this.tblGrilleDonnees );

		this.add ( spGrilleDonnees, BorderLayout.NORTH );
		this.add ( panelInfo,       BorderLayout.SOUTH );


		this.btnMaj.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource() == this.btnMaj) 
		{
			int[] lignesSelectionnees = this.tblGrilleDonnees.getSelectedRows();

			if (lignesSelectionnees.length >= 1) 
			{
				String dureeStr = this.txtTacheDuree.getText();
				for (int i = 0; i < lignesSelectionnees.length; i++) 
				{
					int tacheSelectionnee = lignesSelectionnees[i];
					if (tacheSelectionnee >= 0 && tacheSelectionnee < this.tblGrilleDonnees.getRowCount()) 
					{
						try 
						{
							int duree = Integer.parseInt(dureeStr);
							this.ctrl.mettreAJourDureeTache(tacheSelectionnee, duree);
							this.grilleDonneesModel.refreshTab();
						} 
						catch (NumberFormatException ex) 
						{
							ErrorUtils.showError("La durée doit être un nombre entier.");
							break;
						}
					} 
					else 
					{
						ErrorUtils.showError("Tache non valide.");
						break;
					}
				}
			} 
			else 
			{
				ErrorUtils.showError("Aucune tâche sélectionnée.");
			}
		}
		else if (e.getSource() == this.btnValider) 
		{
			String nomTache = this.txtTacheNom.getText().trim();
			if (!nomTache.isEmpty()) 
			{
				//à faire
			} 
			else 
			{
				ErrorUtils.showError("Le nom de la tâche ne peut pas être vide.");
			}
		}



	}
}
