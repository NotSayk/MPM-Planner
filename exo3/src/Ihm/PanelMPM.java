package src.Ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import src.Controleur;
import src.Ihm.composants.Entite;
import src.Metier.GrapheMPM;
import src.Metier.TacheMPM;
import src.utils.DateUtils;

public class PanelMPM extends JPanel implements MouseListener, MouseMotionListener
{

    private static final int MARGE = 50;
    private static final int ESPACEMENT = 120;

    private Controleur   ctrl;

    private List<Entite> lstEntites;

    private boolean      afficherDateTot;
    private boolean      afficherDateTard;
    private boolean      afficher;

    private int          numNiveauxTot;
    private int          numNiveauxTard;

    private panelButton  panelButton;
    private JPopupMenu   popup;
    
    private Entite entiteSelectionnee;
    private int    offsetX, offsetY;

    private Entite dernierEntite;
    
    // Constantes pour la disposition

    public PanelMPM(GrapheMPM graphe, Controleur ctrl) 
    {
        this.ctrl             = ctrl;

        this.afficherDateTot  = false;
        this.afficherDateTard = false;
        this.afficher         = false;

        this.lstEntites       = new ArrayList<>();
        this.popup            = new JPopupMenu();
        this.panelButton      = new panelButton(this.ctrl, this);

        this.numNiveauxTot    = -1;
        this.numNiveauxTard   = 0;

        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(800, 800));

        this.add(new JLabel("Graphe MPM"), BorderLayout.NORTH);

        this.initEntites();
        this.afficherDateTot() ;
        this.incrementeNiveauxTard();
        

        this.add(this.panelButton, BorderLayout.SOUTH);
        this.add(new BarreMenu(ctrl, this), BorderLayout.NORTH);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    private void incrementeNiveauxTard() 
    {
        for (int i = 0; i < this.ctrl.getNiveauxTaches().length; i++) 
        {
            if (this.ctrl.getNiveauxTaches()[i] !=  0) this.numNiveauxTard ++;
            else                                       break;
        }
    }

    private Entite trouverEntiteAuPoint(int x, int y) 
    {
        for (Entite entite : this.lstEntites) 
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
        this.lstEntites.clear();

        List<TacheMPM> taches = this.ctrl.getTaches();
        
        for (TacheMPM tache : taches) 
        {
            int niveau = ctrl.getNiveauTaches(tache);
            
            int x = MARGE + niveau * ESPACEMENT;
            
            int positionNiveau = 0;
            for (TacheMPM t : taches)
            if (ctrl.getNiveauTaches(t) == niveau && taches.indexOf(t) < taches.indexOf(tache)) 
                positionNiveau++;
            
            int y;
            if (niveau == 0) y = 230;
            else             y = MARGE + positionNiveau * ESPACEMENT;
            
            Entite entite = new Entite(tache, x, y);
            this.lstEntites.add(entite);
        }
    }

    public void afficherCheminCritique(boolean afficher) 
    {
        this.afficher = afficher;
        this.afficherCheminCritique();
    }

    public void afficherCheminCritique()
    {
        if (!afficher) 
        {
            for (Entite entite : this.lstEntites) 
                entite.setCouleurContour(this.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);
            repaint();
            return;
        }
        for (Entite entite : this.lstEntites) 
            if (entite.getTache().estCritique()) 
                entite.setCouleurContour(Color.RED);
        repaint();
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        // Peindre toutes les entités
        for (Entite entite : this.lstEntites)
        {
            entite.paint(g);

            // Affichage des dates selon le flag
            if (afficherDateTot) 
            {
                if (entite.getNiveauTache() <= numNiveauxTot) 
                {
                    g.setColor(Color.GREEN);
                    g.drawString("" + entite.getTache().getDateTot(), entite.getX() + 15, entite.getY() + 55);
                }
            } 
            
            if (afficherDateTard) 
            {
                if(entite.getNiveauTache() >= numNiveauxTard  ) 
                {
                    g.setColor(Color.RED);
                    g.drawString("" + entite.getTache().getDateTard(), entite.getX() + 50, entite.getY() + 55);
                }
            }
        }
        
        // Dessiner les connexions entre les tâches
        this.dessinerConnexions(g);
    }
    
    private void dessinerConnexions(Graphics g)
    {
        g.setColor(this.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);
        for (Entite entite : this.lstEntites)
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
                g.setColor(this.getBackground());
                g.fillRect(xRect, yRect, largeurRect, hauteurRect);
                g.setColor(this.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);

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
        for (Entite entite : this.lstEntites) 
            if (entite.getTache().getNom().equals(tache.getNom())) return entite;
        return null;
    }
    
    public Entite getEntiteParNom(String nomTache) 
    {
        for (Entite entite : this.lstEntites) 
            if (entite.getTache().getNom().equals(nomTache)) return entite;
        return null;
    }

    public void resetPositions() 
    {
        for (Entite entite : this.lstEntites) 
            entite.resetPosition();
        repaint();
    }

    public List<Entite> getEntites() { return this.lstEntites; }

    public void afficherDateTot()
    {
        this.afficherDateTot = true;
        this.numNiveauxTot++;
        repaint();
    }

    public void afficherDateTard()
    {
        this.afficherDateTard = true;
        this.numNiveauxTard--;
        repaint();
    }

    public void cacherDates()
    {
        this.afficherDateTot  = false;
        this.afficherDateTard = false;
        this.numNiveauxTot    = -1;
        this.afficherDateTot();
        this.numNiveauxTard   = 0;
        for (int i = 0; i < this.ctrl.getNiveauxTaches().length; i++) 
        {
            if (this.ctrl.getNiveauxTaches()[i] !=  0) this.numNiveauxTard ++;
            else                                       break;
        }
        repaint();
    }



    public void setTheme(String theme) 
    {
        System.out.println("Changement de thème : " + theme);
        if (theme.equals("LIGHT")) 
        {
            this.setBackground(Color.WHITE);
            for (Entite entite : this.lstEntites) 
                entite.setCouleurContour(Color.BLACK);
            this.afficherCheminCritique();
        } 
        else if (theme.equals("DARK")) 
        {
            this.setBackground(Color.DARK_GRAY);
            for (Entite entite : this.lstEntites) 
                entite.setCouleurContour(Color.WHITE);
            this.afficherCheminCritique();
        }
        repaint();
    }

    public boolean estGriseTot () { return this.numNiveauxTot  == numNiveauxTard-1;                     }
    public boolean estGriseTard() { return this.numNiveauxTard == 0;                                    }
    public String  getTheme    () { return this.getBackground().equals(Color.WHITE) ? "LIGHT" : "DARK"; }
    public boolean isCritique  () { return this.afficher;                                               }

    public void setCritique(boolean critique) 
    {
        this.afficher = critique;
        this.afficherCheminCritique();
        this.panelButton.setCritiqueButton(!critique);
    }

    @Override
    public void mouseClicked(MouseEvent e) 
    {
    }
    
    @Override
    public void mousePressed(MouseEvent e) 
    {
        entiteSelectionnee = trouverEntiteAuPoint(e.getX(), e.getY());
        if (entiteSelectionnee != null) 
        {
            offsetX = e.getX() - entiteSelectionnee.getX();
            offsetY = e.getY() - entiteSelectionnee.getY();
            popup.setVisible(false);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) 
    {
        entiteSelectionnee = null;
    }
    
    @Override
    public void mouseEntered(MouseEvent e) 
    {
        // Implémentation vide
    }
    
    @Override
    public void mouseExited(MouseEvent e) 
    {
        // Implémentation vide
    }

    @Override
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

    @Override
    public void mouseMoved(MouseEvent e) 
    {
        Entite entite = trouverEntiteAuPoint(e.getX(), e.getY());
        
        if (entite != null) {
            if (entite != this.dernierEntite) {
                PanelMPM.this.popup.setVisible(false);
                PanelMPM.this.popup.removeAll();
                PanelMPM.this.popup.add(new JLabel("Infos sur: " + entite.getTache().getNom()));
                PanelMPM.this.popup.add(new JSeparator());
                PanelMPM.this.popup.add(new JLabel("• Antériorité: " + "z"));
                PanelMPM.this.popup.add(new JLabel("• Date au plus tot: " + DateUtils.ajouterJourDate(DateUtils.getDateDuJour(), entite.getTache().getDateTot()) ));
                PanelMPM.this.popup.add(new JLabel("• Date au plus tard: " + DateUtils.ajouterJourDate(DateUtils.getDateDuJour(), entite.getTache().getDateTard())));
                PanelMPM.this.popup.add(new JLabel("• Durée: " + entite.getTache().getDuree()));
                PanelMPM.this.popup.add(new JSeparator());
                PanelMPM.this.popup.add(new JLabel("• Niveau: " + entite.getNiveauTache()));
                PanelMPM.this.popup.add(new JLabel("• Position: (" + entite.getX() + ", " + entite.getY() + ")"));
                PanelMPM.this.popup.add(new JLabel("• Chemin critique: " + (entite.getTache().estCritique() ? "Oui" : "Non")));
                PanelMPM.this.popup.add(new JSeparator());
                PanelMPM.this.popup.add(new JLabel("Clic droit pour modifier la tâche"));

                PanelMPM.this.popup.revalidate(); 
                PanelMPM.this.popup.pack();

                this.dernierEntite = entite;

                Point entitePos = new Point(entite.getX(), entite.getY());
                Point screenPoint = PanelMPM.this.getLocationOnScreen();

                int newX = screenPoint.x + entitePos.x + entite.getLargeur() + 10;
                int newY = screenPoint.y + entitePos.y;                        

                PanelMPM.this.popup.setLocation(newX, newY);
                PanelMPM.this.popup.setVisible(true);
            }
        } else {
            if (PanelMPM.this.popup.isVisible()) {
                PanelMPM.this.popup.setVisible(false);
            }
            this.dernierEntite = null;
        }
    }

}