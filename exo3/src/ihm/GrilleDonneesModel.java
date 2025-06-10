// Dans GrilleDonneesModel.java - Correction complète

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
                                            "Durée",
                                            "Date Totale",
                                            "Date Tardive", 
                                            "Marge",
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
            tabDonnees[lig][1] = tache.getDuree     ();     // Durée
            tabDonnees[lig][2] = tache.getDateTot   ();     // Date au plus tôt
            tabDonnees[lig][3] = tache.getDateTard  ();     // Date au plus tard
            tabDonnees[lig][4] = tache.getMarge     ();     // Marge (calculée dynamiquement)
            tabDonnees[lig][5] = "";                        // Précédents
            tabDonnees[lig][6] = "";                        // Suivants

            // Construction de la liste des précédents
            if (!tache.getPrecedents().isEmpty()) {
                StringBuilder precedents = new StringBuilder();
                for (int i = 0; i < tache.getPrecedents().size(); i++) {
                    precedents.append(tache.getPrecedents().get(i).getNom());
                    if (i < tache.getPrecedents().size() - 1) {
                        precedents.append(", ");
                    }
                }
                tabDonnees[lig][5] = precedents.toString();
            }
            
            // Construction de la liste des suivants
            if (!tache.getSuivants().isEmpty()) {
                StringBuilder suivants = new StringBuilder();
                for (int i = 0; i < tache.getSuivants().size(); i++) {
                    suivants.append(tache.getSuivants().get(i).getNom());
                    if (i < tache.getSuivants().size() - 1) {
                        suivants.append(", ");
                    }
                }
                tabDonnees[lig][6] = suivants.toString();
            }
        }

        this.fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 5 || columnIndex == 6) { // Colonnes précédents ou suivants
            String nouvelleValeur = value.toString().trim();
            TacheMPM tacheModifiee = ctrl.getTaches().get(rowIndex);
            
            try {
                if (columnIndex == 5) { // Précédents
                    this.ctrl.modifierPrecedents(tacheModifiee, nouvelleValeur);
                } else { // Suivants
                    ctrl.modifierSuivants(tacheModifiee, nouvelleValeur);
                }
                refreshTab();
            } catch (IllegalArgumentException e) {
                ErrorUtils.showError(e.getMessage());
            }
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        if (columnIndex == 5 && rowIndex == 0) // DEBUT ne peut pas avoir de précédents
            return false;
        if (columnIndex == 6 && rowIndex == getRowCount() - 1) // FIN ne peut pas avoir de suivants
            return false;
        return columnIndex == 5 || columnIndex == 6; // Seules les colonnes précédents et suivants sont éditables
    }
}