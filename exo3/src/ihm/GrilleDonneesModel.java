package src.ihm;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import src.Controleur;
import src.metier.TacheMPM;
import src.utils.ErrorUtils;

/**
 * Modèle de données pour la grille d'affichage des tâches
 * 
 * Cette classe gère :
 * - L'affichage des tâches dans un tableau
 * - La modification des propriétés des tâches
 * - Le rafraîchissement des données
 * - La validation des modifications
 */
public class GrilleDonneesModel extends AbstractTableModel
{
    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur ctrl;           // Référence au contrôleur
    private String[]   tabEntetes;     // En-têtes des colonnes
    private Object[][] tabDonnees;     // Données du tableau

    /**
     * Crée un nouveau modèle de grille
     * 
     * @param ctrl Le contrôleur de l'application
     */
    public GrilleDonneesModel(Controleur ctrl)
    {
        this.ctrl = ctrl;

        this.tabEntetes = new String[] 
        {  
            "Nom",           // Nom de la tâche
            "Durée",         // Durée en jours
            "Date Totale",   // Date au plus tôt
            "Date Tardive",  // Date au plus tard
            "Marge",         // Marge de la tâche
            "Précédents",    // Liste des tâches précédentes
            "Suivants"       // Liste des tâches suivantes
        };

        this.refreshTab();
    }

    /*---------------------------------*
     * Méthodes de l'interface TableModel *
     *---------------------------------*/
    /**
     * Retourne le nombre de colonnes du tableau
     */
    public int getColumnCount() { return this.tabEntetes.length; }

    /**
     * Retourne le nombre de lignes du tableau
     */
    public int getRowCount() { return this.tabDonnees.length; }

    /**
     * Retourne le nom d'une colonne
     * 
     * @param col Index de la colonne
     */
    public String getColumnName(int col) { return this.tabEntetes[col]; }

    /**
     * Retourne la valeur d'une cellule
     * 
     * @param row Index de la ligne
     * @param col Index de la colonne
     */
    public Object getValueAt(int row, int col) { return this.tabDonnees[row][col]; }

    /**
     * Retourne la classe d'une colonne
     * 
     * @param c Index de la colonne
     */
    public Class getColumnClass(int c) { return getValueAt(0, c).getClass(); }

    /**
     * Rafraîchit les données du tableau
     * Met à jour toutes les cellules avec les données actuelles des tâches
     */
    public void refreshTab()
    {
        List<TacheMPM> lstTaches = this.ctrl.getTaches();
        TacheMPM       tache;

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

    /**
     * Modifie la valeur d'une cellule
     * 
     * @param value Nouvelle valeur
     * @param rowIndex Index de la ligne
     * @param columnIndex Index de la colonne
     */
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

    /**
     * Détermine si une cellule est éditable
     * 
     * @param rowIndex Index de la ligne
     * @param columnIndex Index de la colonne
     * @return true si la cellule est éditable, false sinon
     */
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