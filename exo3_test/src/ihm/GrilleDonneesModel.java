package ihm;

import controleur.ControleurMPM;
import metier.TacheMPM;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class GrilleDonneesModel extends AbstractTableModel {
    private ControleurMPM controleur;
    private String[] colonnes = {"Nom", "Durée", "Précédents", "Suivants"};

    public GrilleDonneesModel(ControleurMPM controleur) {
        this.controleur = controleur;
    }

    @Override
    public int getRowCount() {
        return controleur.getTaches().size();
    }

    @Override
    public int getColumnCount() {
        return colonnes.length;
    }

    @Override
    public String getColumnName(int column) {
        return colonnes[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TacheMPM tache = controleur.getTaches().get(rowIndex);
        
        switch (columnIndex) {
            case 0: return tache.getNom();
            case 1: return tache.getDuree();
            case 2: return tache.getPrecedents().toString();
            case 3: return tache.getSuivants().toString();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        TacheMPM tache = controleur.getTaches().get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                controleur.modifierNom(tache, (String) value);
                break;
            case 1:
                controleur.mettreAJourDureeTache(rowIndex, Integer.parseInt((String) value));
                break;
            case 2:
                controleur.modifierPrecedents(tache, (String) value);
                break;
            case 3:
                controleur.modifierSuivants(tache, (String) value);
                break;
        }
        
        fireTableDataChanged();
    }
} 