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
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import javax.swing.SwingUtilities;
import java.util.ArrayList;

import src.Controleur;
import src.utils.ErrorUtils;
import src.Metier.TacheMPM;

public class PanelModification extends JPanel implements ActionListener
{
	private Controleur ctrl;

	private JTable             tblGrilleDonnees;
	private GrilleDonneesModel grilleDonneesModel;
	private JTextField         txtTacheDuree;
	private JTextField         txtAjoutDuree;
	private JButton            btnMaj;
	private JButton            btnAjout;
	private JTextField         txtNomTache;
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
					if (dureeValue != null)
					{
						this.txtTacheDuree.setText(dureeValue.toString());
					}
					else
					{
						this.txtTacheDuree.setText("");
					}
				}
				else
				{
					this.txtTacheDuree.setText("");
				}
			}
		});

		this.tblGrilleDonnees.setFillsViewportHeight(true);

		JLabel lblDuree    = new JLabel    ( "Durée de la tâche :" );
		this.txtTacheDuree = new JTextField( 10                    );
		this.btnMaj        = new JButton   ( "Mettre à jour"       );

		this.panelInfo.add( lblDuree          );
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



	}

	public void afficherAjout() 
	{
		/*this.panelInfo.setVisible(false);
		List<TacheMPM> lstTache = this.ctrl.getTaches();
		
		// Création du panel principal
		JPanel panelAjout = new JPanel(new GridLayout(1, 2, 10, 0));
		
		// Préparation des données pour les tableaux
		String[] columnNames = {"Tâche", "Sélectionner"};
		Object[][] data = new Object[lstTache.size()][2];
		
		for (int i = 0; i < lstTache.size(); i++) {
			data[i][0] = lstTache.get(i).getNom();
			data[i][1] = Boolean.FALSE;
		}
		
		// Création des modèles de table avec les mêmes caractéristiques
		DefaultTableModel predModel = creerModeleTable(data, columnNames);
		DefaultTableModel succModel = creerModeleTable(data, columnNames);
		
		// Création des tables
		JTable tablePredecesseurs = new JTable(predModel);
		JTable tableSuccesseurs   = new JTable(succModel);
		
		// Ajout des listeners pour afficher les clics dans la console
		tablePredecesseurs.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = tablePredecesseurs.getSelectedRow();
				if (selectedRow >= 0) {
					System.out.println("Prédécesseur sélectionné: " + 
						tablePredecesseurs.getValueAt(selectedRow, 0) + 
						", Sélectionné: " + tablePredecesseurs.getValueAt(selectedRow, 1));
				}
			}
		});
		
		tableSuccesseurs.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = tableSuccesseurs.getSelectedRow();
				if (selectedRow >= 0) {
					System.out.println("Successeur sélectionné: " + 
						tableSuccesseurs.getValueAt(selectedRow, 0) + 
						", Sélectionné: " + tableSuccesseurs.getValueAt(selectedRow, 1));
				}
			}
		});
		
		// Création des panels pour les prédécesseurs et successeurs
		JPanel panelPredecesseurs = creerPanelAvecTable("Sélectionnez les prédécesseurs :", tablePredecesseurs);
		JPanel panelSuccesseurs   = creerPanelAvecTable("Sélectionnez les successeurs :", tableSuccesseurs);
		
		// Assemblage du panel principal
		panelAjout.add(panelPredecesseurs);
		panelAjout.add(panelSuccesseurs  );
		
		// Création du panel des contrôles
		JPanel panelBottom = new JPanel    (         );
		this.txtNomTache   = new JTextField(10       );
		this.txtAjoutDuree = new JTextField(5        );
		this.btnAjout      = new JButton   ("Ajouter");

		this.btnAjout.addActionListener(this);
		
		panelBottom.add(new JLabel("Nom de la nouvelle tâche :"));
		panelBottom.add(this.txtNomTache                        );
		panelBottom.add(new JLabel("Durée :")                   );
		panelBottom.add(this.txtAjoutDuree                      );
		panelBottom.add(this.btnAjout                           );
		
		// Création et ajout du conteneur final
		JPanel panelContainer = new JPanel(new BorderLayout());
		panelContainer.add(panelAjout, BorderLayout.CENTER);
		panelContainer.add(panelBottom, BorderLayout.SOUTH);
		
		this.add(panelContainer, BorderLayout.CENTER);
		this.revalidate();
	}

	// Méthode utilitaire pour créer un modèle de table
	private DefaultTableModel creerModeleTable(Object[][] data, String[] columnNames) 
	{
		return new DefaultTableModel(data, columnNames) 
		{
			public boolean isCellEditable(int row, int column) 
			{
				return column == 1; // Seule la colonne "Sélectionner" est modifiable
			}
			
			public Class<?> getColumnClass(int column) 
			{
				return column == 1 ? Boolean.class : String.class;
			}
		};
	}

	// Méthode utilitaire pour créer un panel avec label et table
	private JPanel creerPanelAvecTable(String labelText, JTable table) 
	{
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JLabel(labelText),  BorderLayout.NORTH );
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		return panel;*/
	}
}
