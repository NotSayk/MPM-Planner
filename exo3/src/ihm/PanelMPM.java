package src.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import src.Controleur;
import src.ihm.composants.Entite;
import src.metier.GrapheMPM;
import src.metier.TacheMPM;
import src.utils.DateUtils;


public class PanelMPM extends JPanel
{
    private static final int MARGE      = 50;
    private static final int ESPACEMENT = 120;

    private Controleur   ctrl;
    private List<Entite> lstEntites;
    private boolean      afficherDateTot;
    private boolean      afficherDateTard;
    private boolean      afficher;
    private int          numNiveauxTot;
    private int          numNiveauxTard;
    private PanelButton  panelButton;
    private JPopupMenu   popup;
    
    // Nouveau panel pour le dessin du graphe
    private GraphePanel  graphePanel;
    private JScrollPane  scrollPane;

    public PanelMPM(GrapheMPM graphe, Controleur ctrl) 
    {
        this.ctrl             = ctrl;
        this.afficherDateTot  = false;
        this.afficherDateTard = false;
        this.afficher         = false;

        this.lstEntites       = new ArrayList<>();
        this.popup            = new JPopupMenu();
        this.panelButton      = new PanelButton(this.ctrl, this);

        this.numNiveauxTot    = -1;
        this.numNiveauxTard   = 0;

        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // Créer le panel de dessin séparé
        this.graphePanel = new GraphePanel();
        
        // Créer le JScrollPane avec le panel de dessin
        this.scrollPane = new JScrollPane(this.graphePanel);
        this.scrollPane.setPreferredSize(new Dimension(800, 600));
        this.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.initEntites();
        this.afficherDateTot();
        this.incrementeNiveauxTard();
        
        // Ajouter les composants
        this.add(new BarreMenu(ctrl), BorderLayout.NORTH);
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.add(this.panelButton, BorderLayout.SOUTH);
    }
    
    /**
     * Panel interne pour dessiner le graphe avec gestion des événements souris
     */
    private class GraphePanel extends JPanel implements MouseListener, MouseMotionListener
    {
        private Entite entiteSelectionnee;
        private int    offsetX, offsetY;
        private Entite dernierEntite;
        
        private double scale = 1.0;
        public GraphePanel()
        {
            this.setBackground(Color.WHITE);
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addMouseWheelListener(e -> 
            {
                if (e.isControlDown() || e.isMetaDown() || e.getPreciseWheelRotation() != 0) 
                {
                    if (e.getWheelRotation() < 0) scale *= 1.1;
                    else scale /= 1.1;
                    scale = Math.max(0.2, Math.min(scale, 5.0)); // Limite le zoom
                    this.revalidate();
                    this.repaint();
                }
            });

            this.updateSize();
        }
        
        public void updateSize()
        {
            // Calculer la taille nécessaire en fonction des entités
            int maxX = 0, maxY = 0;
            for (Entite entite : lstEntites) 
            {
                maxX = Math.max(maxX, entite.getX() + entite.getLargeur());
                maxY = Math.max(maxY, entite.getY() + entite.getHauteur());
            }
            
            // Ajouter une marge supplémentaire
            maxX += MARGE * 2;
            maxY += MARGE * 2;
            
            // Définir une taille minimale
            maxX = Math.max(maxX, 1000);
            maxY = Math.max(maxY, 800);
            
            this.setPreferredSize(new Dimension(maxX, maxY));
            this.revalidate();
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.scale(scale, scale);
            super.paintComponent(g);
            
            for (Entite entite : lstEntites)
            {
                //entite.paint(g);
                entite.paint(g2);

                if (afficherDateTot) 
                {
                    if (entite.getNiveauTache() <= numNiveauxTot) 
                    {
                        g2.setColor(Color.GREEN);
                        g2.drawString("" + entite.getTache().getDateTot(), entite.getX() + 15, entite.getY() + 55);
                    }
                } 
                
                if (afficherDateTard) 
                {
                    if(entite.getNiveauTache() >= numNiveauxTard) 
                    {
                        g2.setColor(Color.RED);
                        g2.drawString("" + entite.getTache().getDateTard(), entite.getX() + 50, entite.getY() + 55);
                    }
                }
            }
            
            //dessinerConnexions(g);
            dessinerConnexions(g2);
            g2.dispose();
        }

        @Override
        public void mouseClicked(MouseEvent e) 
        {
            // Implémentation vide
        }
        
        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.getButton() == MouseEvent.BUTTON1) 
            {
                // Ajuster les coordonnées en fonction du zoom
                int Xscale = (int)(e.getX() / scale);
                int Yscale = (int)(e.getY() / scale);
                
                entiteSelectionnee = trouverEntiteAuPoint(Xscale, Yscale);
                if (entiteSelectionnee != null) 
                {
                    offsetX = Xscale - entiteSelectionnee.getX();
                    offsetY = Yscale - entiteSelectionnee.getY();
                    popup.setVisible(false);
                }
            } 
            else if (e.getButton() == MouseEvent.BUTTON3) 
            {
                int Xscale = (int)(e.getX() / scale);
                int Yscale = (int)(e.getY() / scale);
                
                entiteSelectionnee = trouverEntiteAuPoint(Xscale, Yscale);
                if (entiteSelectionnee == null) 
                {
                    return;  
                }
                if (entiteSelectionnee.getTache().getNom().equals("DEBUT") || entiteSelectionnee.getTache().getNom().equals("FIN")) 
                {
                    return;
                }
                supprimerTache(entiteSelectionnee.getTache());
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
                // Ajuster les coordonnées en fonction du zoom
                int Xscale = (int)(e.getX() / scale);
                int Yscale = (int)(e.getY() / scale);
                
                int newX = Xscale - offsetX;
                int newY = Yscale - offsetY;

                if(newX < 0) newX = 0;
                if(newY < 0) newY = 0;

                entiteSelectionnee.setPosition(newX, newY);
                
                this.updateSize();
                this.scrollRectToVisible(new Rectangle(e.getX() - 100, e.getY() - 100, 100, 100));
                repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) 
        {
            // Ajuster les coordonnées en fonction du zoom
            int Xscale = (int)(e.getX() / scale);
            int Yscale = (int)(e.getY() / scale);
            
            Entite entite = trouverEntiteAuPoint(Xscale, Yscale);
            
            if (entite != null) 
            {
                if (entite != this.dernierEntite) 
                {
                    TacheMPM tache = entite.getTache();

                    String anterieur = "";
                    if (!tache.getPrecedents().isEmpty()) 
                    {
                        for (int i = 0; i < tache.getPrecedents().size(); i++) 
                        {
                            anterieur += tache.getPrecedents().get(i).getNom();
                            if (i < tache.getPrecedents().size() - 1) anterieur += ", ";
                        }
                    }

                    // Popup menu
                    popup.setVisible(false);
                    popup.removeAll();
                    popup.add(new JLabel("Infos sur: " + entite.getTache().getNom()));
                    popup.add(new JSeparator());
                    popup.add(new JLabel("• Antériorité: " + (anterieur.isEmpty() ? "Aucune" : anterieur)));
                    popup.add(new JLabel("• Date au plus tot: " + DateUtils.ajouterJourDate(ctrl.getDateRef(), entite.getTache().getDateTot()) ));
                    popup.add(new JLabel("• Date au plus tard: " + DateUtils.ajouterJourDate(ctrl.getDateRef(), entite.getTache().getDateTard())));
                    popup.add(new JLabel("• Durée: "  + tache.getDuree()));
                    popup.add(new JSeparator());
                    popup.add(new JLabel("• Niveau: " + tache.getNiveau()));
                    popup.add(new JLabel("• Position: (" + entite.getX() + ", " + entite.getY() + ")"));
                    popup.add(new JLabel("• Chemin critique: " + (tache.estCritique() ? "Oui" : "Non")));
                    popup.add(new JSeparator());
                    popup.add(new JLabel("Clic droit pour modifier la tâche"));

                    popup.revalidate(); 
                    popup.pack();

                    this.dernierEntite = entite;

                    // Calculer la position du popup en tenant compte du zoom
                    Point screenPoint = this.getLocationOnScreen();
                    
                    // Position de l'entité à l'écran avec le zoom appliqué
                    int entiteScreenX = (int)(entite.getX() * scale);
                    int entiteScreenY = (int)(entite.getY() * scale);
                    int entiteLargeurScaled = (int)(entite.getLargeur() * scale);

                    int newX = screenPoint.x + entiteScreenX + entiteLargeurScaled + 10;
                    int newY = screenPoint.y + entiteScreenY;                        

                    popup.setLocation(newX, newY);
                    popup.setVisible(true);
                }
            } 
            else 
            {
                if (popup.isVisible()) popup.setVisible(false);
                this.dernierEntite = null;
            }
        }

        @Override
        public Dimension getPreferredSize() 
        {
            Dimension base = super.getPreferredSize();
            return new Dimension((int)(base.width * scale), (int)(base.height * scale));
        }
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
        
        // Étape 1: Compter le nombre de tâches par niveau
        int[] nbTachesParNiveau = new int[2000];
        int niveauMax = 0;
        
        for (TacheMPM tache : taches) 
        {
            int niveau = this.ctrl.getNiveauTache(tache);
            nbTachesParNiveau[niveau]++;
            if (niveau > niveauMax) niveauMax = niveau;
        }
        
        // Étape 2: Trouver le niveau avec le plus de tâches
        int maxTachesParNiveau = 0;
        for (int i = 0; i <= niveauMax; i++) 
        {
            if (nbTachesParNiveau[i] > maxTachesParNiveau) 
            {
                maxTachesParNiveau = nbTachesParNiveau[i];
            }
        }
        
        // Étape 3: Calculer le centre Y en fonction de la taille du plus gros niveau
        int hauteurMaxNiveau = maxTachesParNiveau * PanelMPM.ESPACEMENT;
        int centreY = 150 + hauteurMaxNiveau / 2;
        
        // Étape 4: Créer les entités avec positionnement centré
        int[] compteurParNiveau = new int[2000];
        
        for (TacheMPM tache : taches) 
        {
            int niveau = this.ctrl.getNiveauTache(tache);
            int x = PanelMPM.MARGE + niveau * PanelMPM.ESPACEMENT;
            
            int nbTachesCeNiveau = nbTachesParNiveau[niveau];
            int hauteurCeNiveau = nbTachesCeNiveau * PanelMPM.ESPACEMENT;
            int debutY = centreY - hauteurCeNiveau / 2;
            
            int positionDansNiveau = compteurParNiveau[niveau];
            int y = debutY + positionDansNiveau * PanelMPM.ESPACEMENT;
            
            if (y < PanelMPM.MARGE) y = PanelMPM.MARGE + positionDansNiveau * PanelMPM.ESPACEMENT;
            
            Entite entite = new Entite(tache, x, y);
            this.lstEntites.add(entite);
            
            compteurParNiveau[niveau]++;
        }
        
        // Mettre à jour la taille du panel de dessin
        if (this.graphePanel != null) 
        {
            this.graphePanel.updateSize();
        }
    }

    public void afficherCheminCritique(boolean aff) 
    {
        this.afficher = aff;
        
        if (!aff) 
        {
            for (Entite entite : this.lstEntites) 
                entite.setCouleurContour(this.graphePanel.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);
        }
        else
        {
            for (Entite entite : this.lstEntites) 
            {
                if (entite.getTache().estCritique()) 
                    entite.setCouleurContour(Color.RED);
                else
                    entite.setCouleurContour(this.graphePanel.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);
            }
        }
        repaint();
    }
    
    private void dessinerConnexions(Graphics g)
    {
        g.setColor(this.graphePanel.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);
        for (Entite entite : this.lstEntites)
        {
            TacheMPM tache = entite.getTache();
            for (TacheMPM tacheSuivante : tache.getSuivants())
            {
                Entite entiteSuivante = this.getEntiteParTache(tacheSuivante);
                if (entiteSuivante == null) continue;

                int x1 = entite.getX() + entite.getLargeur();
                int y1 = entite.getY() + entite.getHauteur() / 2;
                int x2 = entiteSuivante.getX();
                int y2 = entiteSuivante.getY() + entiteSuivante.getHauteur() / 2;
                
                g.drawLine(x1, y1, x2, y2);
                
                int xCentre = (x1 + x2) / 2;
                int yCentre = (y1 + y2) / 2;
                
                FontMetrics fm   = g.getFontMetrics();
                String texte     = String.valueOf(entite.getTache().getDuree());
                int largeurTexte = fm.stringWidth(texte);
                int hauteurTexte = fm.getHeight();
                
                int xRect       = xCentre - largeurTexte / 2 - 2;
                int yRect       = yCentre - hauteurTexte / 2 - 2;
                int largeurRect = largeurTexte + 2 * 2;
                int hauteurRect = hauteurTexte + 2 * 2;
                
                g.setColor(this.graphePanel.getBackground());
                g.fillRect(xRect, yRect, largeurRect, hauteurRect);
                g.setColor(this.graphePanel.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE);

                g.drawString(texte, xCentre - largeurTexte / 2, yCentre + hauteurTexte / 4);
                
                dessinerFleche(g, x1, y1, x2, y2);
            }
        }
    }
    
    private void dessinerFleche(Graphics g, int x1, int y1, int x2, int y2) 
    {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int taillePointe = 10;
        
        int x3 = (int) (x2 - taillePointe * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - taillePointe * Math.sin(angle - Math.PI / 6));
        
        int x4 = (int) (x2 - taillePointe * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - taillePointe * Math.sin(angle + Math.PI / 6));
        
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
        this.graphePanel.updateSize();
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
            this.graphePanel.setBackground(Color.WHITE);
            for (Entite entite : this.lstEntites) 
                entite.setCouleurContour(Color.BLACK);
        } 
        else if (theme.equals("DARK")) 
        {
            this.setBackground(Color.DARK_GRAY);
            this.graphePanel.setBackground(Color.DARK_GRAY);
            for (Entite entite : this.lstEntites) 
                entite.setCouleurContour(Color.WHITE);
        }
        this.afficherCheminCritique(this.afficher);
        repaint();
    }

    private void supprimerTache(TacheMPM tache) 
    {
        this.ctrl.getFichier().supprimerTacheFichier(tache);
        this.initEntites();
    
        this.setTheme(this.getTheme());
        this.afficherCheminCritique(this.afficher);
        
        this.repaint();
    }

    public boolean estGriseTot () { return this.numNiveauxTot  == numNiveauxTard-1; }
    public boolean estGriseTard() { return this.numNiveauxTard == 0; }
    public String  getTheme    () { return this.graphePanel.getBackground().equals(Color.WHITE) ? "LIGHT" : "DARK"; }
    public boolean isCritique  () { return this.afficher; }

    public void setCritique(boolean critique) 
    {
        this.afficher = critique;
        this.afficherCheminCritique(critique);
        this.panelButton.setCritiqueButton(critique);
    }
}