package src.ihm;

import java.util.List;
import javax.swing.JFrame;
import src.Controleur;
import src.ihm.composants.Entite;
import src.metier.GrapheMPM;
import src.metier.TacheMPM;

/**
 * Classe FrameMPM qui représente la fenêtre principale de l'application MPM.
 * Cette classe hérite de JFrame et gère l'affichage du graphe MPM ainsi que
 * les différents panels de l'interface utilisateur.
 */
public class FrameMPM extends JFrame
{
    /** Contrôleur principal de l'application */
    private Controleur ctrl;
    
    /** Graphe MPM contenant les tâches et leurs relations */
    private GrapheMPM  graphe;

    /** Panel principal affichant le graphe MPM */
    private PanelMPM   panelMPM;
    /** Panel des paramètres de l'application */
    private PanelPara  panelPara;

    /**
     * Constructeur de la fenêtre principale
     * @param ctrl Le contrôleur principal de l'application
     */
    public FrameMPM(Controleur ctrl) 
    {
        this.ctrl      = ctrl;
        this.graphe    = this.ctrl.getGraphe();
        this.panelPara = new PanelPara(this.ctrl);

        this.setTitle("MPM - Choix des paramètres");
        this.setSize (850, 600);

        this.setLocationRelativeTo(null);
        this.setLocation(this.getX() - 50, this.getY());

        this.add(this.panelPara);
        
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    public void         setTheme      ( String  theme    ) { this.panelMPM.setTheme         ( theme    ); }
    public void         setCritique   ( boolean critique ) { this.panelMPM.setCritique      ( critique ); }
    public void         resetPositions(                  ) { this.panelMPM.resetPositions   (          ); }
    
    public boolean      isCritique    (                  ) { return this.panelMPM.isCritique(          ); }
    public List<Entite> getEntites    (                  ) { return this.panelMPM.getEntites(          ); }
    public String       getTheme      (                  ) { return this.panelMPM.getTheme  (          ); }
    public PanelMPM     getPanelMPM   (                  ) { return this.panelMPM                       ; }
    public TacheMPM     getTacheSelectionnee(            ) { return this.panelMPM.getTacheSelectionnee(); }
    public double       getScale      (                  ) { return this.panelMPM.getScale  (          ); }

  
    public void changerPanel() 
    {
        this.getContentPane().removeAll();
        this.panelMPM  = new PanelMPM(this.ctrl);
        this.add(this.panelMPM);
        this.setTitle("MPM - Graphe MPM");
        this.maj();
    }

    /**
     * Change le thème de l'interface entre clair et sombre
     */
    public void changerTheme() 
    {
        String currentTheme = this.panelMPM.getTheme();
        
        switch (currentTheme) 
        {
            case "LIGHT" -> this.panelMPM.setTheme("DARK");
            case "DARK"  -> this.panelMPM.setTheme("LIGHT");
            default      -> throw new AssertionError();
        }
        
        this.maj();
    }

    /**
     * Définit la tâche sélectionnée dans le graphe
     * @param tache La tâche à sélectionner
     */
    public void setTacheSelectionnee(TacheMPM tache) 
    {
        this.panelMPM.setTacheSelectionnee(tache);
    }

    /**
     * Met à jour l'affichage de la fenêtre
     */
    private void maj() 
    {
        this.revalidate();
        this.repaint();
    }
    
    /**
     * Définit l'échelle du graphe
     * @param zoom Le niveau de zoom à appliquer
     */
    public void setScale(double zoom) {this.panelMPM.setScale(zoom);}
}
