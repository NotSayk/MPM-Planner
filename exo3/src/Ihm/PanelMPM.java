package src.Ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.Controleur;
import src.Ihm.composants.Entite;
import src.Metier.GrapheMPM;
import src.Metier.TacheMPM;

public class PanelMPM extends JPanel 
{
    private Controleur   ctrl;

    private GrapheMPM    graphe;
    private List<Entite> entites;
    
    // Variables pour le déplacement
    private Entite entiteSelectionnee;
    private int    offsetX, offsetY;
    
    // Constantes pour la disposition
    private static final int MARGE = 50;
    private static final int ESPACEMENT = 120;

    public PanelMPM(GrapheMPM graphe, Controleur ctrl) 
    {
        this.graphe = graphe;
        this.ctrl    = ctrl;
        this.entites = new ArrayList<>();

        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(800, 800));

        this.add(new JLabel("Graphe MPM"), BorderLayout.NORTH);

        this.initEntites();
        this.ajouterEcouteursSouris();

        this.add(new panelButton(this.ctrl), BorderLayout.SOUTH);

        this.add(new BarreMenu(), BorderLayout.NORTH);
    }
    
    private void ajouterEcouteursSouris() 
    {
        // Écouteur pour les clics de souris
        this.addMouseListener(new MouseAdapter() 
        {
            public void mousePressed(MouseEvent e) 
            {
                entiteSelectionnee = trouverEntiteAuPoint(e.getX(), e.getY());
                if (entiteSelectionnee != null) 
                {
                    offsetX = e.getX() - entiteSelectionnee.getX();
                    offsetY = e.getY() - entiteSelectionnee.getY();
                }
            }
            
            public void mouseReleased(MouseEvent e) 
            {
                entiteSelectionnee = null;
            }
        });
        
        // Écouteur pour les mouvements de souris
        this.addMouseMotionListener(new MouseMotionAdapter() 
        {
            public void mouseDragged(MouseEvent e) 
            {
                if (entiteSelectionnee != null) 
                {
                    int newX = e.getX() - offsetX;
                    int newY = e.getY() - offsetY;
                    entiteSelectionnee.setPosition(newX, newY);
                    repaint();
                }
            }
        });
    }
    
    private Entite trouverEntiteAuPoint(int x, int y) 
    {
        for (Entite entite : entites) 
        {
            if (x >= entite.getX() && x <= entite.getX() + entite.getLargeur() &&
                y >= entite.getY() && y <= entite.getY() + entite.getHauteur()) 
            {
                return entite;
            }
        }
        return null;
    }
    
    private void initEntites() 
    {
        this.getEntites().clear();

        List<TacheMPM> taches = graphe.getTaches();
        
        for (TacheMPM tache : taches) 
        {
            int niveau = ctrl.getNiveauTaches(tache);
            
            int x = MARGE + niveau * ESPACEMENT;
            
            int positionNiveau = 0;
            for (TacheMPM t : taches)
                if (ctrl.getNiveauTaches(t) == niveau && taches.indexOf(t) < taches.indexOf(tache)) 
                    positionNiveau++;
            
            int y = MARGE + positionNiveau * ESPACEMENT;
            
            Entite entite = new Entite(tache, x, y);
            entites.add(entite);
        }
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        // Peindre toutes les entités
        for (Entite entite : entites) entite.paint(g);
        
        // Dessiner les connexions entre les tâches
        this.dessinerConnexions(g);
    }
    
    private void dessinerConnexions(Graphics g)
    {
        g.setColor(Color.BLACK);
        for (Entite entite : this.getEntites())
        {
            TacheMPM tache = entite.getTache();
            // Dessiner les connexions vers les tâches suivantes
            for (TacheMPM tacheSuivante : tache.getSuivants())
            {
                Entite entiteSuivante = this.getEntiteParTache(tacheSuivante);
                if (entiteSuivante == null) continue;

                // Calculer les points de connexion
                int x1 = entite.getX() + entite.getLargeur();
                int y1 = entite.getY() + entite.getHauteur() / 2;
                int x2 = entiteSuivante.getX();
                int y2 = entiteSuivante.getY() + entiteSuivante.getHauteur() / 2;
                
                // Dessiner la ligne
                g.drawLine(x1, y1, x2, y2);
                
                // Calculer le point central de la ligne
                int xCentre = (x1 + x2) / 2;
                int yCentre = (y1 + y2) / 2;
                
                FontMetrics fm   = g.getFontMetrics();
                String texte     = String.valueOf(entite.getTache().getDuree());
                int largeurTexte = fm.stringWidth(texte);
                int hauteurTexte = fm.getHeight();
                
                // Calculer les coordonnées du rectangle
                int xRect       = xCentre - largeurTexte / 2 - 2;
                int yRect       = yCentre - hauteurTexte / 2 - 2;
                int largeurRect = largeurTexte + 2 * 2;
                int hauteurRect = hauteurTexte + 2 * 2;
                
                // Dessiner le rectangle blanc avec bordure
                g.setColor(Color.WHITE);
                g.fillRect(xRect, yRect, largeurRect, hauteurRect);
                g.setColor(Color.BLACK);

                // Centrer le texte par rapport au point central
                g.drawString(texte, xCentre - largeurTexte / 2, yCentre + hauteurTexte / 4);
                
                // Dessiner la pointe de la flèche
                dessinerFleche(g, x1, y1, x2, y2);
            }
        }
    }
    
    private void dessinerFleche(Graphics g, int x1, int y1, int x2, int y2) 
    {
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
    
    private Entite getEntiteParTache(TacheMPM tache) 
    {
        for (Entite entite : entites) 
            if (entite.getTache().getNom().equals(tache.getNom())) return entite;
        return null;
    }
    
    public Entite getEntiteParNom(String nomTache) 
    {
        for (Entite entite : entites) 
            if (entite.getTache().getNom().equals(nomTache)) return entite;
        return null;
    }

    public void resetPositions() 
    {
        for (Entite entite : entites) 
        {
            entite.resetPosition();
        }
        repaint();
    }

    public List<Entite> getEntites() { return new ArrayList<>(entites); }

}