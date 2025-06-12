package src.ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import src.Controleur;
import src.ihm.composants.Entite;
import src.metier.TacheMPM;


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

        // Créer le panel de dessin séparé avec les références nécessaires
        this.graphePanel = new GraphePanel(this, ctrl);
        
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

    public void initEntites() 
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
        
        // Synchroniser avec GraphePanel
        if (this.graphePanel != null) 
        {
            this.graphePanel.setEntites(this.lstEntites);
            this.graphePanel.setAfficherDateTot(this.afficherDateTot);
            this.graphePanel.setAfficherDateTard(this.afficherDateTard);
            this.graphePanel.setAfficher(this.afficher);
            this.graphePanel.setNumNiveauxTot(this.numNiveauxTot);
            this.graphePanel.setNumNiveauxTard(this.numNiveauxTard);
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

    // Rendre cette méthode publique pour GraphePanel
    public void dessinerConnexions(Graphics g)
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

    // Rendre cette méthode publique pour GraphePanel
    public void supprimerTache(TacheMPM tache) 
    {
        this.ctrl.supprimerTacheFichier(tache);
        this.initEntites();
    
        this.setTheme(this.getTheme());
        this.afficherCheminCritique(this.afficher);
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
        if (this.tacheSelectionnee != null) 
            this.getEntiteParTache(tacheSelectionnee).setCouleurContour(Color.BLUE);
        repaint();
    }

    // Rendre cette méthode publique pour GraphePanel
    public Color determinerCouleurContour(Entite entite, boolean afficherCritique) 
    {
        if (afficherCritique && entite.getTache().estCritique()) 
        {
            return Color.RED;
        }
        return this.graphePanel.getBackground().equals(Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    // Rendre cette méthode publique pour GraphePanel
    public void reinitialiserCouleursEntites() 
    {
        for (Entite entite : this.lstEntites) 
        {
            Color couleur = determinerCouleurContour(entite, this.afficher);
            entite.setCouleurContour(couleur);
        }
    }

    public void afficherDateTot()
    {
        this.afficherDateTot = true;
        this.numNiveauxTot++;
        if (this.graphePanel != null) {
            this.graphePanel.setAfficherDateTot(this.afficherDateTot);
            this.graphePanel.setNumNiveauxTot(this.numNiveauxTot);
        }
        repaint();
    }

    public void afficherDateTard()
    {
        this.afficherDateTard = true;
        this.numNiveauxTard--;
        if (this.graphePanel != null) {
            this.graphePanel.setAfficherDateTard(this.afficherDateTard);
            this.graphePanel.setNumNiveauxTard(this.numNiveauxTard);
        }
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
        
        if (this.graphePanel != null) {
            this.graphePanel.setAfficherDateTot(this.afficherDateTot);
            this.graphePanel.setAfficherDateTard(this.afficherDateTard);
            this.graphePanel.setNumNiveauxTot(this.numNiveauxTot);
            this.graphePanel.setNumNiveauxTard(this.numNiveauxTard);
        }
        repaint();
    }

    public void setTheme(String theme) 
    {
        appliquerTheme(theme);
        this.afficherCheminCritique(this.afficher);
        if (this.tacheSelectionnee != null)
            this.getEntiteParTache(tacheSelectionnee).setCouleurContour(Color.BLUE);
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
        this.graphePanel.setScale(1.0);
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

        this.graphePanel.setScale(1.5);
        this.graphePanel.updateSize();

        centrerSurEntite(entite);
        repaint();
    }

    private void centrerSurEntite(Entite entite) 
    {
        double scale = this.graphePanel.getScale();
        Rectangle visibleRect = new Rectangle
        (
            (int)(entite.getX() * scale) - 100,
            (int)(entite.getY() * scale) - 100,
            (int)(entite.getLargeur() * scale) + 200,
            (int)(entite.getHauteur() * scale) + 200
        );
        this.graphePanel.scrollRectToVisible(visibleRect);
    }

    public void setCritique(boolean critique) 
    {
        this.afficher = critique;
        this.afficherCheminCritique(critique);
        this.panelButton.setCritiqueButton(critique);
    }

    public void setScale(double zoom)
    {
        this.graphePanel.setScale(zoom);
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
    public double   getScale            () { return this.graphePanel.getScale();                                                  }

    /*------------------------------------------------------------*
     * Classe interne : Panel de dessin du graphe                *
     *------------------------------------------------------------*/
    
    
}