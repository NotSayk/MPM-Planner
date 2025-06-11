package src.ihm;

import javax.swing.*;
import java.awt.*;
import src.Controleur;

public class PanelCritique extends JPanel 
{
    private Controleur ctrl;

    private JTextArea  textArea;
    
    public PanelCritique(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        this.setLayout(new BorderLayout());
        
        // Zone de texte pour afficher les chemins critiques
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        
        this.textArea.setText(this.ctrl.getGraphe().afficherCheminsCritiques());
    }
    
    public void actualiser() 
    {
        this.textArea.setText(this.ctrl.getGraphe().afficherCheminsCritiques());
    }
}