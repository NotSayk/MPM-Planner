package src.ihm;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.Controleur;
import src.metier.CheminCritique;

/**
 * Classe PanelCritique qui gère l'affichage des chemins critiques du graphe MPM.
 * Cette classe hérite de JPanel et affiche une liste des chemins critiques dans une zone de texte.
 * Elle permet de visualiser tous les chemins critiques du projet de manière claire et organisée.
 */
public class PanelCritique extends JPanel 
{
    /** Contrôleur principal de l'application */
    private Controleur ctrl;

    /** Zone de texte pour afficher les chemins critiques */
    private JTextArea textArea;
    
    /**
     * Constructeur du panel des chemins critiques
     * @param ctrl Le contrôleur principal de l'application
     */
    public PanelCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;

        this.setLayout(new BorderLayout());
        
        // Zone de texte pour afficher les chemins critiques
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        this.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        this.afficherCheminsCritiques();
    }
    
    /**
     * Affiche la liste des chemins critiques dans la zone de texte.
     * Si aucun chemin critique n'est trouvé, affiche un message approprié.
     * Les chemins sont affichés avec leurs tâches séparées par des flèches.
     */
    private void afficherCheminsCritiques() 
    {
        String sRet = "";
        sRet += ("=== CHEMINS CRITIQUES ===\n\n");
        
        List<CheminCritique> chemins = this.ctrl.getCheminsCritiques();
        
        if (chemins.isEmpty()) 
        {
            sRet += ("Aucun chemin critique trouvé.\n");
        } 
        else 
        {
            for (int i = 0; i < chemins.size(); i++) 
            {
                sRet += ("Chemin " + (i + 1) + " :\n");
                sRet += (chemins.get(i).toString() + "\n\n");
            }
        }
        
        this.textArea.setText(sRet);
    }
    
    /**
     * Actualise l'affichage des chemins critiques.
     * Cette méthode est appelée lorsque les chemins critiques sont modifiés.
     */
    public void actualiser() 
    {
        this.afficherCheminsCritiques();
    }
}