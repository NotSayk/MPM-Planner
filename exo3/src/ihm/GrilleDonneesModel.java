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

    public GrilleDonneesModel(Controleur ctrl)
    {
        this.ctrl = ctrl;

        this.tabEntetes = new String[] 
        {  
            "Nom",
            "Durée",
            "Date Tot",
            "Date Tard", 
            "Marge",
            "Précédents",
            "Suivants" 
        };

        this.refreshTab();
    }

    // Getters
    public int    getColumnCount()                 { return this.tabEntetes.length;          }
    public int    getRowCount   ()                 { return this.tabDonnees.length;          }
    public String getColumnName (int col)          { return this.tabEntetes[col];            }
    public Object getValueAt    (int row, int col) { return this.tabDonnees[row][col];       }
    public Class  getColumnClass(int c)            { return getValueAt(0, c).getClass(); }

    public void refreshTab()
    {
        List<TacheMPM> lstTaches = this.ctrl.getTaches();
        TacheMPM       tache     = null;

        tabDonnees = new Object[lstTaches.size()][7];

        for (int lig = 0; lig < lstTaches.size(); lig++)
        {
            tache = lstTaches.get(lig);
            
            tabDonnees[lig][0] = tache.getNom();
            tabDonnees[lig][1] = tache.getDuree();     // Durée
            tabDonnees[lig][2] = tache.getDateTot();   // Date au plus tôt
            tabDonnees[lig][3] = tache.getDateTard();  // Date au plus tard
            tabDonnees[lig][4] = tache.getMarge();     // Marge (calculée dynamiquement)
            tabDonnees[lig][5] = "";                   // Précédents
            tabDonnees[lig][6] = "";                   // Suivants

            // Construction de la liste des précédents
            if (!tache.getPrecedents().isEmpty()) 
            {
                String precedents = "";
                for (int i = 0; i < tache.getPrecedents().size(); i++) 
                {
                    precedents += tache.getPrecedents().get(i).getNom();
                    if (i < tache.getPrecedents().size() - 1) precedents += ", ";
                }
                tabDonnees[lig][5] = precedents;
            }
            
            // Construction de la liste des suivants
            if (!tache.getSuivants().isEmpty()) 
            {
                String suivants = "";
                for (int i = 0; i < tache.getSuivants().size(); i++) 
                {
                    suivants += tache.getSuivants().get(i).getNom();
                    if (i < tache.getSuivants().size() - 1) suivants += ", ";
                }
                tabDonnees[lig][6] = suivants;
            }
        }

        this.fireTableDataChanged();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) 
    {
        if (columnIndex == 5 || columnIndex == 6 || columnIndex == 0) 
        {
            String nouvelleValeur = value.toString().trim();
            TacheMPM tacheModifiee = ctrl.getTaches().get(rowIndex);
            
            try 
            {
                if (columnIndex == 5) 
                { 
                    // Précédents
                    this.ctrl.modifierPrecedents(tacheModifiee, nouvelleValeur);
                } 
                if (columnIndex == 6)
                { 
                    // Suivants
                    ctrl.modifierSuivants(tacheModifiee, nouvelleValeur);
                }
                if (columnIndex == 0) 
                { 
                    // Nom
                    ctrl.modifierNom(tacheModifiee, nouvelleValeur);
                }
                refreshTab();
            } 
            catch (IllegalArgumentException e) 
            {
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
        if (columnIndex == 0 && rowIndex == 0)
            return false;
        if (columnIndex == 0 && rowIndex == getRowCount() - 1)
            return false;
        return columnIndex == 5 || columnIndex == 6 || columnIndex == 0; // Seules les colonnes précédents et suivants sont éditables
    }
}