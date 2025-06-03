import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FrameMPM extends JFrame
{
    private GrapheMPM graphe;
    private PanelMPM panelMPM;
    
    public FrameMPM()
    {
        super("Visualisation MPM");
        
        // Créer le graphe MPM
        graphe = new GrapheMPM();
        
        // Créer le panel de visualisation
        panelMPM = new PanelMPM(graphe);
        
        // Créer une barre d'outils
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton btnCheminCritique = new JButton("Afficher/Masquer chemin critique");
        btnCheminCritique.addActionListener(e -> panelMPM.toggleCheminCritique());
        toolBar.add(btnCheminCritique);
        
        // Configuration de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(new JScrollPane(panelMPM), BorderLayout.CENTER);
        
        // Taille et position
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            new FrameMPM().setVisible(true);
        });
    }
}