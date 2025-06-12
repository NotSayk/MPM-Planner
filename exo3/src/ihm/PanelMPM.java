package src.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import src.Controleur;
import src.ihm.composants.Entite;
import src.metier.TacheMPM;
import src.utils.DateUtils;


public class PanelMPM extends JPanel
{
    /*--------------------*
     * Constantes         *
     *--------------------*/
    private static final int MARGE      = 50;
    private static final int ESPACEMENT = 120;

    /*--------------------*
     * Attributs privés   *
     *--------------------*/
    private Controleur   ctrl;
    private List<Entite> lstEntites;
    private TacheMPM     tacheSelectionnee;

    private boolean      afficherDateTot;
    private boolean      afficherDateTard;
    private boolean      afficher;
    private int          numNiveauxTot;
    private int          numNiveauxTard;
    private PanelButton  panelButton;
    private GraphePanel  graphePanel;
    private JScrollPane  scrollPane;

    /*--------------*
     * Constructeur *
     *--------------*/
    public PanelMPM(Controleur ctrl) 
    {
        this.ctrl             = ctrl;
        this.afficherDateTot  = false;
        this.afficherDateTard = false;
        this.afficher         = false;

        this.lstEntites        = new ArrayList<>();
        this.panelButton       = new PanelButton(this.ctrl);
        this.tacheSelectionnee = null;

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
        this.scrollPane.setVerticalScrollBarPolicy  (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.initEntites();
        this.afficherDateTot();
        this.incrementeNiveauxTard();
        
        // Ajouter les composants
        this.add(new BarreMenu(ctrl), BorderLayout.NORTH);
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.add(this.panelButton, BorderLayout.SOUTH);
    }

    /*----------------------------------*
     * Méthodes privées d'initialisation *
     *----------------------------------*/
    private void incrementeNiveauxTard() 
    {
        for (int i = 0; i < this.ctrl.getNiveauxTaches().length; i++) 
        {
            if (this.ctrl.getNiveauxTaches()[i] !=  0) this.numNiveauxTard ++;
            else                                       break;
        }
    }

    private void initEntites() 
    {
        this.lstEntites.clear();
        
        List<TacheMPM> taches = this.ctrl.getTaches();
        
        // Étape 1: Compter le nombre de tâches par niveau
        int[] nbTachesParNiveau = new int[2000];
        int niveauMax = calculerNiveauxTaches(taches, nbTachesParNiveau);
        
        // Étape 2: Trouver le niveau avec le plus de tâches
        int maxTachesParNiveau = trouverMaxTachesParNiveau(nbTachesParNiveau, niveauMax);
        
        // Étape 3: Calculer le centre Y en fonction de la taille du plus gros niveau
        int centreY = calculerCentreY(maxTachesParNiveau);
        
        // Étape 4: Créer les entités avec positionnement centré
        creerEntitesPositionnees(taches, nbTachesParNiveau, centreY);
        
        // Mettre à jour la taille du panel de dessin
        if (this.graphePanel != null) 
        {
            this.graphePanel.updateSize();
        }
    }

    private int calculerNiveauxTaches(List<TacheMPM> taches, int[] nbTachesParNiveau) 
    {
        int niveauMax = 0;
        for (TacheMPM tache : taches) 
        {
            int niveau = this.ctrl.getNiveauTache(tache);
            nbTachesParNiveau[niveau]++;
            if (niveau > niveauMax) niveauMax = niveau;
        }
        return niveauMax;
    }

    private int trouverMaxTachesParNiveau(int[] nbTachesParNiveau, int niveauMax) 
    {
        int maxTachesParNiveau = 0;
        for (int i = 0; i <= niveauMax; i++) 
        {
            if (nbTachesParNiveau[i] > maxTachesParNiveau) 
            {
                maxTachesParNiveau = nbTachesParNiveau[i];
            }
        }
        return maxTachesParNiveau;
    }

    private int calculerCentreY(int maxTachesParNiveau) 
    {
        int hauteurMaxNiveau = maxTachesParNiveau * PanelMPM.ESPACEMENT;
        return 150 + hauteurMaxNiveau / 2;
    }

    private void creerEntitesPositionnees(List<TacheMPM> taches, int[] nbTachesParNiveau, int centreY) 
    {
        int[] compteurParNiveau = new int[2000];
        
        for (TacheMPM tache : taches) 
        {
            int niveau = this.ctrl.getNiveauTache(tache);
            int x = PanelMPM.MARGE + niveau * PanelMPM.ESPACEMENT;
            
            int y = calculerPositionY(nbTachesParNiveau, centreY, niveau, compteurParNiveau[niveau]);
            
            Entite entite = new Entite(tache, x, y);
            this.lstEntites.add(entite);
            
            compteurParNiveau[niveau]++;
        }
    }

    private int calculerPositionY(int[] nbTachesParNiveau, int centreY, int niveau, int positionDansNiveau) 
    {
        int nbTachesCeNiveau = nbTachesParNiveau[niveau];
        int hauteurCeNiveau  = nbTachesCeNiveau * PanelMPM.ESPACEMENT;
        int debutY           = centreY - hauteurCeNiveau / 2;
        
        int y = debutY + positionDansNiveau * PanelMPM.ESPACEMENT;
        
        if (y < PanelMPM.MARGE) y = PanelMPM.MARGE + positionDansNiveau * PanelMPM.ESPACEMENT;
        
        return y;
    }

    /*----------------------------------*
     * Méthodes privées utilitaires     *
     *----------------------------------*/
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

    private void dessinerConnexions(Graphics g)
    {
        Color couleurLigne = obtenirCouleurLigne();
        g.setColor(couleurLigne);
        
        for (Entite entite : this.lstEntites)
        {
            dessinerConnexionsEntite(g, entite);
        }
    }

    private Color obtenirCouleurLigne() 
    {
        return this.graphePanel.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private void dessinerConnexionsEntite(Graphics g, Entite entite) 
    {
        TacheMPM tache = entite.getTache();
        for (TacheMPM tacheSuivante : tache.getSuivants())
        {
            Entite entiteSuivante = this.getEntiteParTache(tacheSuivante);
            if (entiteSuivante == null) continue;

            int[] coordonnees = calculerCoordonneesLigne(entite, entiteSuivante);
            int x1 = coordonnees[0], y1 = coordonnees[1], x2 = coordonnees[2], y2 = coordonnees[3];
            
            g.drawLine(x1, y1, x2, y2);
            
            dessinerTexteDuree(g, entite, x1, y1, x2, y2);
            dessinerFleche(g, x1, y1, x2, y2);
        }
    }

    private int[] calculerCoordonneesLigne(Entite entite, Entite entiteSuivante) 
    {
        int x1 = entite.getX() + entite.getLargeur();
        int y1 = entite.getY() + entite.getHauteur() / 2;
        int x2 = entiteSuivante.getX();
        int y2 = entiteSuivante.getY() + entiteSuivante.getHauteur() / 2;
        return new int[]{x1, y1, x2, y2};
    }

    private void dessinerTexteDuree(Graphics g, Entite entite, int x1, int y1, int x2, int y2) 
    {
        int xCentre = (x1 + x2) / 2;
        int yCentre = (y1 + y2) / 2;
        
        FontMetrics fm   = g.getFontMetrics();
        String texte     = String.valueOf(entite.getTache().getDuree());
        int largeurTexte = fm.stringWidth(texte);
        int hauteurTexte = fm.getHeight();
        
        dessinerFondTexte(g, xCentre, yCentre, largeurTexte, hauteurTexte);
        
        g.setColor(obtenirCouleurLigne());
        g.drawString(texte, xCentre - largeurTexte / 2, yCentre + hauteurTexte / 4);
    }

    private void dessinerFondTexte(Graphics g, int xCentre, int yCentre, int largeurTexte, int hauteurTexte) 
    {
        int xRect       = xCentre - largeurTexte / 2 - 2;
        int yRect       = yCentre - hauteurTexte / 2 - 2;
        int largeurRect = largeurTexte + 2 * 2;
        int hauteurRect = hauteurTexte + 2 * 2;
        
        g.setColor(this.graphePanel.getBackground());
        g.fillRect(xRect, yRect, largeurRect, hauteurRect);
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

    private void supprimerTache(TacheMPM tache) 
    {
        this.ctrl.supprimerTacheFichier(tache);
        this.initEntites();
    
        this.setTheme(this.getTheme());
        this.afficherCheminCritique(this.afficher);
        this.getEntiteParTache(tacheSelectionnee).setCouleurContour(Color.BLUE);
        this.repaint();
    }

    /*----------------------------------*
     * Méthodes publiques d'affichage   *
     *----------------------------------*/
    public void afficherCheminCritique(boolean aff) 
    {
        this.afficher = aff;
        
        for (Entite entite : this.lstEntites) 
        {
            Color couleurContour = determinerCouleurContour(entite, aff);
            entite.setCouleurContour(couleurContour);
        }
        repaint();
    }

    private Color determinerCouleurContour(Entite entite, boolean afficherCritique) 
    {
        if (afficherCritique && entite.getTache().estCritique()) 
            return Color.RED;
        return this.graphePanel.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

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
        this.numNiveauxTard   = 0;

        this.afficherDateTot();
        for (int i = 0; i < this.ctrl.getNiveauxTaches().length; i++) 
        {
            if (this.ctrl.getNiveauxTaches()[i] !=  0) this.numNiveauxTard ++;
            else                                       break;
        }
        repaint();
    }

    public void setTheme(String theme) 
    {
        appliquerTheme(theme);
        this.afficherCheminCritique(this.afficher);
        this.ctrl.
        repaint();
    }

    private void appliquerTheme(String theme) 
    {
        if (theme == null || theme.isEmpty()) 
        {
            theme = "LIGHT";
        }
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
      
    }

    public void resetPositions() 
    {
        for (Entite entite : this.lstEntites) 
            entite.resetPosition();
        this.graphePanel.updateSize();
        repaint();
    }

    public void resetScale() 
    {
        this.graphePanel.scale = 1.0;
        this.graphePanel.updateSize();
        this.repaint();
    }

    /*----------------------------------*
     * Setters publics                  *
     *----------------------------------*/
    public void setTacheSelectionnee(TacheMPM tache) 
    {
        if (this.tacheSelectionnee != null) 
        {
            Entite entiteActuelle = getEntiteParTache(this.tacheSelectionnee);
            if (entiteActuelle != null) 
            {
                Color couleurContour = determinerCouleurContour(entiteActuelle, this.afficher);
                entiteActuelle.setCouleurContour(couleurContour);
            }
        }
        this.tacheSelectionnee = tache;

        Entite entite = getEntiteParTache(tache);
        if (entite == null) return;
        entite.setCouleurContour(Color.BLUE);
        this.tacheSelectionnee = entite.getTache();

        graphePanel.scale = 1.5;
        graphePanel.updateSize();

        centrerSurEntite(entite);
        repaint();
    }

    private void centrerSurEntite(Entite entite) 
    {
        Rectangle visibleRect = new Rectangle
        (
            (int)(entite.getX() * graphePanel.scale) - 100,
            (int)(entite.getY() * graphePanel.scale) - 100,
            (int)(entite.getLargeur() * graphePanel.scale) + 200,
            (int)(entite.getHauteur() * graphePanel.scale) + 200
        );
        graphePanel.scrollRectToVisible(visibleRect);
    }

    public void setCritique(boolean critique) 
    {
        this.afficher = critique;
        this.afficherCheminCritique(critique);
        this.panelButton.setCritiqueButton(critique);
    }

    public void setScale(double zoom)
    {
        this.graphePanel.scale = zoom;
        this.graphePanel.updateSize();
        this.repaint();
    }

    /*----------------------------------*
     * Getters publics                  *
     *----------------------------------*/
    public List<Entite> getEntites() { return this.lstEntites; }

    public Entite getEntiteParTache(TacheMPM tache) 
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

    public TacheMPM getTacheSelectionnee() { return this.tacheSelectionnee;                                                  }
    public boolean  estGriseTot         () { return this.numNiveauxTot  == numNiveauxTard-1;                                 }
    public boolean  estGriseTard        () { return this.numNiveauxTard == 0;                                                }
    public String   getTheme            () { return this.graphePanel.getBackground().equals(Color.WHITE) ? "LIGHT" : "DARK"; }
    public boolean  isCritique          () { return this.afficher;                                                           }
    public double   getScale            () { return this.graphePanel.scale;                                                  }

    /*------------------------------------------------------------*
     * Classe interne : Panel de dessin du graphe                *
     *------------------------------------------------------------*/
    private class GraphePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, 
                                                        ActionListener
    {
        /*--------------------*
         * Attributs privés   *
         *--------------------*/
        private JPopupMenu   popup;
        private JPopupMenu   popupEdit;

        private JMenuItem    jmCopier;
        private JMenuItem    jmSuprimer;
        private JMenuItem    jmDuree;
        private JMenuItem    jmNom;

        private Entite       entiteSelectionnee;
        private int          offsetX, offsetY;
        private Entite       dernierEntite;
        private double       scale;

        /*--------------*
         * Constructeur *
         *--------------*/
        public GraphePanel()
        {
            this.popup      = new JPopupMenu();
            this.popupEdit  = new JPopupMenu();

            this.jmCopier   = new JMenuItem("Copier");
            this.jmSuprimer = new JMenuItem("Supprimer");
            this.jmDuree    = new JMenuItem("Modifier durée");
            this.jmNom      = new JMenuItem("Modifier nom");

            this.popupEdit.add(this.jmCopier);
            this.popupEdit.add(this.jmSuprimer);
            this.popupEdit.addSeparator();
            this.popupEdit.add(this.jmDuree);
            this.popupEdit.add(this.jmNom);

            this.scale = 1.0;

            this.setBackground         (Color.WHITE);
            this.addMouseListener      (this);
            this.addMouseMotionListener(this);
            this.addMouseWheelListener (this);

            this.jmCopier  .addActionListener(this);
            this.jmSuprimer.addActionListener(this);
            this.jmDuree   .addActionListener(this);
            this.jmNom     .addActionListener(this);

            this.updateSize();
        }

        /*----------------------------------*
         * Méthodes de gestion de la taille *
         *----------------------------------*/
        public void updateSize()
        {
            // Calculer la taille nécessaire en fonction des entités
            int[] taillePanneau = calculerTaillePanneau();
            
            this.setPreferredSize(new Dimension(taillePanneau[0], taillePanneau[1]));
            this.revalidate();
        }

        private int[] calculerTaillePanneau() 
        {
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
            
            return new int[]{maxX, maxY};
        }

        @Override
        public Dimension getPreferredSize() 
        {
            Dimension base = super.getPreferredSize();
            return new Dimension((int)(base.width * scale), (int)(base.height * scale));
        }

        /*----------------------------------*
         * Méthode de dessin                *
         *----------------------------------*/
        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.scale(scale, scale);
            super.paintComponent(g);
            
            dessinerEntites(g2);
            dessinerConnexions(g2);
            g2.dispose();
        }

        private void dessinerEntites(Graphics2D g2) 
        {
            for (Entite entite : lstEntites)
            {
                entite.paint(g2);
                dessinerDatesSurEntite(g2, entite);
            }
        }

        private void dessinerDatesSurEntite(Graphics2D g2, Entite entite) 
        {
            FontMetrics fm = g2.getFontMetrics();

            if (afficherDateTot && entite.getNiveauTache() <= numNiveauxTot) 
            {
                dessinerDateTot(g2, entite, fm);
            }

            if (afficherDateTard && entite.getNiveauTache() >= numNiveauxTard) 
            {
                dessinerDateTard(g2, entite, fm);
            }
        }

        private void dessinerDateTot(Graphics2D g2, Entite entite, FontMetrics fm) 
        {
            String texte = obtenirTexteDate(entite.getTache().getDateTot());
            int[] coordonnees = calculerCoordonneesTexte(entite, fm, texte, 16, 55);
            
            g2.setColor(new Color(0, 183, 14));
            g2.drawString(texte, coordonnees[0], coordonnees[1]);
        }

        private void dessinerDateTard(Graphics2D g2, Entite entite, FontMetrics fm) 
        {
            String texte = obtenirTexteDate(entite.getTache().getDateTard());
            int[] coordonnees = calculerCoordonneesTexte(entite, fm, texte, 52, 55);
            
            g2.setColor(new Color(255, 27, 14));
            g2.drawString(texte, coordonnees[0], coordonnees[1]);
        }

        private String obtenirTexteDate(int date) 
        {
            if(ctrl.isFormatDateTexte())
            {
                String dateStr = DateUtils.ajouterJourDate(ctrl.getDateRef(), date);
                if (dateStr.length() >= 5) return dateStr.substring(0, 5); // JJ/MM
                else                       return dateStr;
            }
            else 
                return "" + date;
        }

        private int[] calculerCoordonneesTexte(Entite entite, FontMetrics fm, String texte, int offsetX, int offsetY) 
        {
            int textWidth = fm.stringWidth(texte);
            int x = entite.getX() + offsetX - textWidth / 2;
            int y = entite.getY() + offsetY;
            return new int[]{x, y};
        }

        /*----------------------------------*
         * Gestionnaires d'événements souris *
         *----------------------------------*/
        @Override
        public void mousePressed(MouseEvent e) 
        {
            if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) 
            {
                int[] coordonneesEchelle = obtenirCoordonneesEchelle(e);
                this.gererSelectionEntite(coordonneesEchelle[0], coordonneesEchelle[1]);
            }
        }

        private int[] obtenirCoordonneesEchelle(MouseEvent e) 
        {
            int Xscale = (int)(e.getX() / scale);
            int Yscale = (int)(e.getY() / scale);
            return new int[]{Xscale, Yscale};
        }

        private void gererSelectionEntite(int x, int y) 
        {

            this.entiteSelectionnee = trouverEntiteAuPoint(x, y);
            if (entiteSelectionnee == null) return;

            offsetX = x - entiteSelectionnee.getX();
            offsetY = y - entiteSelectionnee.getY();
            popup.setVisible(false);

            System.out.println("Entité sélectionnée: " + entiteSelectionnee.getTache().getNom());
        }

        @Override
        public void mouseClicked(MouseEvent e) 
        {
            int[] coordonneesEchelle = obtenirCoordonneesEchelle(e);
            Entite entiteCliquee = trouverEntiteAuPoint(coordonneesEchelle[0], coordonneesEchelle[1]);

            if (e.getButton() == MouseEvent.BUTTON1) 
            {
                gererClicGauche(entiteCliquee);
            }
            if(e.getButton() == MouseEvent.BUTTON3) 
            {
                gererClicDroit(e, entiteCliquee);
            }
        }

        private void gererClicGauche(Entite entiteCliquee) 
        {
            if (entiteCliquee != null) 
            {
                reinitialiserCouleursEntites();
                PanelMPM.this.tacheSelectionnee = entiteCliquee.getTache();
                entiteCliquee.setCouleurContour(Color.BLUE);
                repaint();
            }
        }

        private void reinitialiserCouleursEntites() 
        {
            for (Entite entite : lstEntites) 
            {
                Color couleur = determinerCouleurContour(entite, afficher);
                entite.setCouleurContour(couleur);
            }
        }

        private void gererClicDroit(MouseEvent e, Entite entiteCliquee) 
        {
            this.popup.setVisible(false);
            if (entiteCliquee != null) 
                this.popupEdit.show(e.getComponent(),e.getX(),e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) 
        {
            if (this.entiteSelectionnee != null) 
            {
                int[] coordonneesEchelle = obtenirCoordonneesEchelle(e);
                deplacerEntite(coordonneesEchelle[0], coordonneesEchelle[1], e);
            }
        }

        private void deplacerEntite(int x, int y, MouseEvent e) 
        {
            int newX = x - offsetX;
            int newY = y - offsetY;

            if(newX < 0) newX = 0;
            if(newY < 0) newY = 0;

            entiteSelectionnee.setPosition(newX, newY);
            
            this.updateSize();
            this.scrollRectToVisible(new Rectangle(e.getX() - 100, e.getY() - 100, 100, 100));
            repaint();
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
        public void mouseEntered(MouseEvent e) {}
        
        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        /*----------------------------------*
         * Gestionnaire de zoom             *
         *----------------------------------*/
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) 
        {
            if (e.isControlDown() || e.isMetaDown() || e.getPreciseWheelRotation() != 0) 
            {
                if (e.getWheelRotation() < 0) scale *= 1.1;
                else scale /= 1.1;
                scale = Math.max(0.2, Math.min(scale, 5.0)); // Limite le zoom
                revalidate();
                repaint();
            }
        }

        /*----------------------------------*
         * Gestionnaire d'actions           *
         *----------------------------------*/
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            if (e.getSource() == this.jmCopier) 
            {
                PanelMPM.this.ctrl.copierTache();
            }
            else if (e.getSource() == this.jmSuprimer) 
            {

                System.out.println("Suppression de la tâche : " + entiteSelectionnee.getTache().getNom());

                if (entiteSelectionnee == null) 
                    return;

                if (entiteSelectionnee.getTache().getNom().equals("DEBUT") || 
                    entiteSelectionnee.getTache().getNom().equals("FIN")) 
                    return;
                
                supprimerTache(entiteSelectionnee.getTache());
                ctrl.getGrilleDonneesModel().refreshTab();
            }
            else if (e.getSource() == this.jmDuree) 
            {
                String dureeTache = JOptionPane.showInputDialog(this, "Modifier la durée de la tâche :", 
                                                                entiteSelectionnee.getTache().getDuree());
                if (entiteSelectionnee.getTache().getNom().equals("DEBUT") || 
                    entiteSelectionnee.getTache().getNom().equals("FIN")) 
                {
                    JOptionPane.showMessageDialog(this, "Impossible de modifier la durée de la tâche DEBUT ou FIN.", 
                                                  "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (dureeTache != null && !dureeTache.isEmpty())
                {
                    try 
                    {
                        int duree = Integer.parseInt(dureeTache);
                        entiteSelectionnee.getTache().setDuree(duree);
                        ctrl.getGraphe().calculerDates();
                        ctrl.getGraphe().initCheminCritique();
                        ctrl.modifierTacheFichier(entiteSelectionnee.getTache());
                        initEntites();
                        repaint();
                        revalidate();   
                    } 
                    catch (NumberFormatException ex) 
                    {
                        JOptionPane.showMessageDialog(this, "Durée invalide. Veuillez entrer un nombre entier.", 
                                                      "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            else if (e.getSource() == this.jmNom) 
            {
                String nomTache = JOptionPane.showInputDialog(this, "Modifier le nom de la tâche :", 
                                                              entiteSelectionnee.getTache().getNom());
                if (nomTache != null && !nomTache.isEmpty())
                {
                    entiteSelectionnee.getTache().setNom(nomTache);
                    ctrl.modifierTacheFichier(entiteSelectionnee.getTache());
                    initEntites();
                    repaint();
                }
            }
        }
    }
}