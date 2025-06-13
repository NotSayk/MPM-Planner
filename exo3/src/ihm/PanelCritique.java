package src.ihm;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import src.Controleur;
import src.metier.CheminCritique;

public class PanelCritique extends JPanel 
{
    private Controleur ctrl;

    private JTextArea textArea;
    
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
            for (CheminCritique chemin : chemins) 
            {
                sRet += chemin.toString() + "\n";
            }
        }
        
        textArea.setText(sRet);
    }
    
    public void actualiser() 
    {
        this.afficherCheminsCritiques();
    }
}