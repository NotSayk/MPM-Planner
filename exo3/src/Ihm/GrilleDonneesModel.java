package src.Ihm;

import javax.swing.table.AbstractTableModel;

import src.Controleur;
import src.Metier.TacheMPM;

import java.util.List;

public class GrilleDonneesModel extends AbstractTableModel
{
	private Controleur ctrl;

	private String[]   tabEntetes;
	private Object[][] tabDonnees;

	public GrilleDonneesModel (Controleur ctrl)
	{
		this.ctrl = ctrl;

		TacheMPM tache;
		List<TacheMPM> lstClients = ctrl.getTaches();

		tabDonnees = new Object[lstClients.size()][7];

		for ( int lig=0; lig<lstClients.size(); lig++)
		{
			tache = lstClients.get(lig);

			tabDonnees[lig][0] = tache.getNom       ();
			tabDonnees[lig][1] = tache.getDateTot   ();
			tabDonnees[lig][2] = tache.getMarge     ();
			tabDonnees[lig][3] = tache.getDateTard  ();
			tabDonnees[lig][4] = ""; // Précédents
			tabDonnees[lig][5] = ""; // Suivants

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

		this.tabEntetes = new String[]   {  "Nom",
		                                    "Date Totale",
		                                    "Marge",
		                                    "Date Tardive",
		                                    "PrÃ©cÃ©dents",
		                                    "Suivants" };

	}

	public int    getColumnCount()                 { return this.tabEntetes.length;      }
	public int    getRowCount   ()                 { return this.tabDonnees.length;      }
	public String getColumnName (int col)          { return this.tabEntetes[col];        }
	public Object getValueAt    (int row, int col) { return this.tabDonnees[row][col];   }
	public Class  getColumnClass(int c)            { return getValueAt(0, c).getClass(); }

	/*public void setValueAt(Object value, int row, int col)
	{
		boolean bRet;

		if ( col == 2 )
		{
			bRet = this.ctrl.majPremiumClient ( row, (Boolean) value );
			if ( bRet )
			{
				this.tabDonnees[row][col] = value;
				this.fireTableCellUpdated(row, col);
			}
		}
	}
	*/


}