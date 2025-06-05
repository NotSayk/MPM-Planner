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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import src.Controleur;
import src.utils.ErrorUtils;
import src.Metier.TacheMPM;

public class PanelModification extends JPanel implements ActionListener
{
	private Controleur ctrl;

	private JTable             tblGrilleDonnees;
	private GrilleDonneesModel grilleDonneesModel;
	private JTextField         txtTacheDuree;
	private JButton            btnMaj;
	private JButton            btnAjout;
	private JTextField         txtNomTache;
	private JPanel             panelInfo;
	private List<JCheckBox>    lstTaches;

	public PanelModification (Controleur ctrl)
	{
		this.ctrl = ctrl;
		this.setLayout ( new BorderLayout() );

		JScrollPane spGrilleDonnees;
		
		// CrÃƒÂ©ation des composants
		this.grilleDonneesModel = new GrilleDonneesModel(ctrl);
		this.tblGrilleDonnees = new JTable ( this.grilleDonneesModel );
		this.tblGrilleDonnees.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.panelInfo = new JPanel();
		this.lstTaches = new java.util.ArrayList<>();

		this.tblGrilleDonnees.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int[] selectedRows = this.tblGrilleDonnees.getSelectedRows();
				if (selectedRows.length == 1) {
					int selectedRow = selectedRows[0];
					Object dureeValue = this.tblGrilleDonnees.getValueAt(selectedRow, 1); 
					if (dureeValue != null) {
						this.txtTacheDuree.setText(dureeValue.toString());
					} else {
						this.txtTacheDuree.setText("");
					}
				} else {
					this.txtTacheDuree.setText("");
				}
			}
		});
		this.tblGrilleDonnees.setFillsViewportHeight(true);

		JLabel lblDuree  = new JLabel("Durée de la tâche :");
		this.txtTacheDuree = new JTextField(10);
		this.btnMaj = new JButton("Mettre à jour");
		this.panelInfo.add(lblDuree);
		this.panelInfo.add(this.txtTacheDuree);
		this.panelInfo.add(this.btnMaj);

 		spGrilleDonnees   = new JScrollPane( this.tblGrilleDonnees );

		this.add ( spGrilleDonnees, BorderLayout.NORTH );
		this.add ( panelInfo, BorderLayout.SOUTH );

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
		this.panelInfo.setVisible(false);
		List<TacheMPM> lstTache = this.ctrl.getTaches();
		JPanel panelAjout = new JPanel(new BorderLayout());
		
		// Create a table model for dependencies
		String[] columnNames = {"Tâche", "Sélectionner"};
		Object[][] data = new Object[lstTache.size()][2];
		
		for (int i = 0; i < lstTache.size(); i++) {
			data[i][0] = lstTache.get(i).getNom();
			data[i][1] = Boolean.FALSE;
		}
		
		// Create a non-editable table except for the checkbox column
		JTable tableDependencies = new JTable(new javax.swing.table.DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1; // Only the "Sélectionner" column is editable
			}
			
			@Override
			public Class<?> getColumnClass(int column) {
				return column == 1 ? Boolean.class : String.class;
			}
		});
		
		tableDependencies.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(tableDependencies);
		
		JPanel panelBottom = new JPanel();
		this.txtNomTache = new JTextField(10);
		this.btnAjout = new JButton("Ajouter");
		this.btnAjout.addActionListener(this);
		
		panelBottom.add(new JLabel("Nom de la nouvelle tâche :"));
		panelBottom.add(this.txtNomTache);
		panelBottom.add(this.btnAjout);
		
		panelAjout.add(new JLabel("Sélectionnez les tâches dépendantes :"), BorderLayout.NORTH);
		panelAjout.add(scrollPane, BorderLayout.CENTER);
		panelAjout.add(panelBottom, BorderLayout.SOUTH);
		
		this.add(panelAjout, BorderLayout.CENTER);
		this.revalidate();
	}

}