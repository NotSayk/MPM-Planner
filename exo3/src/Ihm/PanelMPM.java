package src.Ihm;

import src.Metier.GrapheMPM;
import src.Metier.TacheMPM;
import src.Ihm.composants.Entite;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import src.Controleur;

public class PanelMPM extends JPanel {
    private GrapheMPM graphe;
    private JLabel label;
    private Controleur ctrl;
    private List<Entite> entites;
    
    // Constantes pour la disposition
    private static final int MARGE = 50;
    private static final int ESPACEMENT = 120;

    public PanelMPM(GrapheMPM graphe, Controleur ctrl) {
        this.graphe = graphe;
        this.label = new JLabel("Graphe MPM");
        this.add(label);
        this.setBackground(Color.WHITE);
        this.ctrl = ctrl;
        this.entites = new ArrayList<>();

        this.setPreferredSize(new Dimension(800, 800));
        creerEntites();
    }
    
    private void creerEntites() 
    {
        entites.clear();
        List<TacheMPM> taches = graphe.getTaches();
        
        for (int i = 0; i < taches.size(); i++) 
        {
            int lig = i / 10;
            int col = i % 10;
            
            int x = MARGE + col * ESPACEMENT;
            int y = MARGE + lig * ESPACEMENT;
            
            // Créer l'entité
            Entite entite = new Entite(taches.get(i), x, y);
            entites.add(entite);
        }
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        // Peindre toutes les entités
        for (Entite entite : entites) 
        {
            entite.paint(g);
        }
        
        // Dessiner les connexions entre les tâches
        dessinerConnexions(g);
    }
    
    private void dessinerConnexions(Graphics g) 
    {
        g.setColor(Color.BLACK);
        
        for (Entite entite : entites) {
            TacheMPM tache = entite.getTache();
            
            // Dessiner les connexions vers les tâches suivantes
            for (TacheMPM tacheSuivante : tache.getSuivants()) {
                Entite entiteSuivante = trouverEntiteParTache(tacheSuivante);
                if (entiteSuivante != null) {
                    // Calculer les points de connexion
                    int x1 = entite.getX() + entite.getLargeur();
                    int y1 = entite.getY() + entite.getHauteur() / 2;
                    
                    int x2 = entiteSuivante.getX();
                    int y2 = entiteSuivante.getY() + entiteSuivante.getHauteur() / 2;
                    
                    // Dessiner la ligne
                    g.drawLine(x1, y1, x2, y2);
                    
                    // Dessiner la pointe de la flèche
                    dessinerFleche(g, x1, y1, x2, y2);
                }
            }
        }
    }
    
    private void dessinerFleche(Graphics g, int x1, int y1, int x2, int y2) {
        // Calculer l'angle de la ligne
        double angle = Math.atan2(y2 - y1, x2 - x1);
        
        // Taille de la flèche
        int taillePointe = 10;
        
        // Calculer les points de la pointe de flèche
        int x3 = (int) (x2 - taillePointe * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - taillePointe * Math.sin(angle - Math.PI / 6));
        
        int x4 = (int) (x2 - taillePointe * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - taillePointe * Math.sin(angle + Math.PI / 6));
        
        // Dessiner la pointe de la flèche
        g.drawLine(x2, y2, x3, y3);
        g.drawLine(x2, y2, x4, y4);
    }
    
    private Entite trouverEntiteParTache(TacheMPM tache) 
    {
        for (Entite entite : entites) 
        {
            if (entite.getTache().getNom().equals(tache.getNom())) 
            {
                return entite;
            }
        }
        return null;
    }
    
    public List<Entite> getEntites() {return new ArrayList<>(entites);}
    
    public Entite getEntiteParNom(String nomTache) 
    {
        for (Entite entite : entites) 
        {
            if (entite.getTache().getNom().equals(nomTache)) 
            {
                return entite;
            }
        }
        return null;
    }
}