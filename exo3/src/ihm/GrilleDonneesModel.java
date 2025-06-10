package src.ihm;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import src.Controleur;
import src.metier.TacheMPM;
import src.utils.ErrorUtils;


public class GrilleDonneesModel extends AbstractTableModel
{
	private Controleur ctrl;

	private String[]   tabEntetes;
	private Object[][] tabDonnees;

	public GrilleDonneesModel (Controleur ctrl)
	{
		this.ctrl = ctrl;

		this.tabEntetes = new String[]   {  "Nom",
		                                    "Date Totale",
		                                    "Marge",
		                                    "Date Tardive",
		                                    "Précédents",
		                                    "Suivants" };

		this.refreshTab();

	}

	// Getters
	public int    getColumnCount()                 { return this.tabEntetes.length;      }
	public int    getRowCount   ()                 { return this.tabDonnees.length;      }
	public String getColumnName (int col)          { return this.tabEntetes[col];        }
	public Object getValueAt    (int row, int col) { return this.tabDonnees[row][col];   }
	public Class  getColumnClass(int c)            { return getValueAt(0, c).getClass(); }

	public void refreshTab()
	{
		List<TacheMPM> lstTaches = this.ctrl.getTaches();
		TacheMPM       tache     = null;

		tabDonnees = new Object[lstTaches.size()][7];

		for ( int lig=0; lig<lstTaches.size(); lig++)
		{
			tache = lstTaches.get(lig);
			
			tabDonnees[lig][0] = tache.getNom       ();
			tabDonnees[lig][1] = tache.getDateTot   ();
			tabDonnees[lig][2] = tache.getMarge     ();
			tabDonnees[lig][3] = tache.getDateTard  ();
			tabDonnees[lig][4] = "";
			tabDonnees[lig][5] = "";

			if (!tache.getPrecedents().isEmpty()) {
				for (int i = 0; i < tache.getPrecedents().size(); i++) {
					tabDonnees[lig][4] += tache.getPrecedents().get(i).getNom();
					if (i < tache.getPrecedents().size() - 1) tabDonnees[lig][4] += ", ";
				}
			}
			if (!tache.getSuivants().isEmpty()) {
				for (int i = 0; i < tache.getSuivants().size(); i++) {
					tabDonnees[lig][5] += tache.getSuivants().get(i).getNom();
					if (i < tache.getSuivants().size() - 1) tabDonnees[lig][5] += ", ";
				}
			}
			
		}

		this.fireTableDataChanged();
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) 
	{
        if (columnIndex == 4 || columnIndex == 5) 
		{
            String nouvelleValeur = value.toString().trim();
            TacheMPM tacheModifiee = ctrl.getTaches().get(rowIndex);
            
            try 
			{
                if (columnIndex == 4) this.ctrl.modifierPrecedents(tacheModifiee, nouvelleValeur);
                else                  this.ctrl.modifierSuivants  (tacheModifiee, nouvelleValeur);
				
                refreshTab();
            } catch (IllegalArgumentException e) { ErrorUtils.showError(e.getMessage()); }
        }
    }

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	 {
		if (columnIndex == 4 && rowIndex == 0) // DEBUT ne peut pas avoir de précédents
			return false;
		if (columnIndex == 5 && rowIndex == getRowCount() - 1) // FIN ne peut pas avoir de suivants
			return false;
		return columnIndex == 4 || columnIndex == 5;
	}

}