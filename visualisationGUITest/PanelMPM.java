import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class PanelMPM extends JPanel {
    private GrapheMPM graphe;
    private Map<TacheMPM, Rectangle> positions = new HashMap<>();
    private TacheMPM tacheCourante;
    private Point pointDrag;
    private boolean afficherCheminCritique = true;
    
    // Constantes pour le style classique MPM
    private static final int TAILLE_BOITE = 80;  // Taille des boîtes carrées
    private static final int ESPACE_H = 120;     // Espacement horizontal entre boîtes
    private static final int ESPACE_V = 100;     // Espacement vertical entre boîtes
    private static final int MARGE = 50;         // Marge de bordure
    
    public PanelMPM(GrapheMPM graphe) {
        this.graphe = graphe;
        setBackground(Color.WHITE);
        
        // Gestionnaire de souris
        MouseAdapter souris = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tacheCourante = getTacheAt(e.getPoint());
                if (tacheCourante != null) {
                    Rectangle r = positions.get(tacheCourante);
                    pointDrag = new Point(e.getX() - r.x, e.getY() - r.y);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                tacheCourante = null;
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (tacheCourante != null) {
                    positions.get(tacheCourante).setLocation(
                        e.getX() - pointDrag.x, 
                        e.getY() - pointDrag.y
                    );
                    repaint();
                }
            }
        };
        
        addMouseListener(souris);
        addMouseMotionListener(souris);
        initialiserPositions();
    }
    
    private void initialiserPositions() {
        // Calculer les niveaux basés sur les dates au plus tôt
        int maxNiveau = 0;
        Map<Integer, Integer> compteurParNiveau = new HashMap<>();
        
        for (TacheMPM tache : graphe.getTaches()) {
            int niveau = tache.getDateTot();
            maxNiveau = Math.max(maxNiveau, niveau);
            compteurParNiveau.put(niveau, compteurParNiveau.getOrDefault(niveau, 0) + 1);
        }
        
        // Définir la taille du panel
        int largeurTotale = MARGE * 2 + (maxNiveau + 1) * (TAILLE_BOITE + ESPACE_H);
        int hauteurMax = compteurParNiveau.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int hauteurTotale = MARGE * 2 + hauteurMax * (TAILLE_BOITE + ESPACE_V);
        
        setPreferredSize(new Dimension(largeurTotale, hauteurTotale));
        
        // Regrouper les tâches par niveau
        Map<Integer, ArrayList<TacheMPM>> niveaux = new HashMap<>();
        for (TacheMPM tache : graphe.getTaches()) {
            int niveau = tache.getDateTot();
            niveaux.putIfAbsent(niveau, new ArrayList<>());
            niveaux.get(niveau).add(tache);
        }
        
        // Positionner les tâches
        for (Map.Entry<Integer, ArrayList<TacheMPM>> entry : niveaux.entrySet()) {
            int niveau = entry.getKey();
            ArrayList<TacheMPM> taches = entry.getValue();
            int x = MARGE + niveau * (TAILLE_BOITE + ESPACE_H);
            
            // Centrer verticalement les tâches du niveau
            int hauteurTotaleNiveau = taches.size() * TAILLE_BOITE + (taches.size() - 1) * ESPACE_V;
            int yStart = (getPreferredSize().height - hauteurTotaleNiveau) / 2;
            
            for (int i = 0; i < taches.size(); i++) {
                int y = yStart + i * (TAILLE_BOITE + ESPACE_V);
                positions.put(taches.get(i), new Rectangle(x, y, TAILLE_BOITE, TAILLE_BOITE));
            }
        }
    }
    
    private TacheMPM getTacheAt(Point p) {
        for (Map.Entry<TacheMPM, Rectangle> entry : positions.entrySet()) {
            if (entry.getValue().contains(p)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dessiner les liens en premier (arrière-plan)
        for (TacheMPM tache : graphe.getTaches()) {
            Rectangle r1 = positions.get(tache);
            for (TacheMPM suivant : tache.getSuivants()) {
                Rectangle r2 = positions.get(suivant);
                if (r1 != null && r2 != null) {
                    boolean lienCritique = (tache.getMarge() == 0 && suivant.getMarge() == 0 && afficherCheminCritique);
                    dessinerLien(g2d, r1, r2, lienCritique, tache.getDuree());
                }
            }
        }
        
        // Dessiner les tâches au premier plan
        for (TacheMPM tache : graphe.getTaches()) {
            Rectangle r = positions.get(tache);
            if (r != null) {
                boolean critique = tache.getMarge() == 0 && afficherCheminCritique;
                dessinerBoiteMPM(g2d, tache, r, critique);
            }
        }
    }
    
    private void dessinerBoiteMPM(Graphics2D g2d, TacheMPM tache, Rectangle r, boolean critique) {
        // Couleur de fond
        if (critique) {
            g2d.setColor(new Color(255, 230, 230));
        } else {
            g2d.setColor(Color.WHITE);
        }
        g2d.fillRect(r.x, r.y, r.width, r.height);
        
        // Bordure principale
        g2d.setColor(critique ? Color.RED : Color.BLACK);
        g2d.setStroke(new BasicStroke(critique ? 2.0f : 1.0f));
        g2d.drawRect(r.x, r.y, r.width, r.height);
        
        // Divisions internes (style MPM classique)
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setColor(Color.BLACK);
        
        // Division horizontale au milieu
        int milieu = r.y + r.height / 2;
        g2d.drawLine(r.x, milieu, r.x + r.width, milieu);
        
        // Division verticale dans la partie inférieure pour les dates
        g2d.drawLine(r.x + r.width / 2, milieu, r.x + r.width / 2, r.y + r.height);
        
        // Textes
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        
        // Nom de la tâche (partie haute, centré)
        String nom = tache.getNom();
        int nomWidth = fm.stringWidth(nom);
        g2d.drawString(nom, 
            r.x + (r.width - nomWidth) / 2, 
            r.y + 20);
        
        // Police plus petite pour les dates
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        fm = g2d.getFontMetrics();
        
        // Date au plus tôt (centrée dans le carré bas gauche)
        String dateTot = String.valueOf(tache.getDateTot());
        int totWidth = fm.stringWidth(dateTot);
        g2d.drawString(dateTot, 
            r.x + (r.width/2 - totWidth)/2, 
            r.y + milieu + (r.height/2 + fm.getHeight())/2 - 10);
        
        // Date au plus tard (centrée dans le carré bas droite)
        String dateTard = String.valueOf(tache.getDateTard());
        int tardWidth = fm.stringWidth(dateTard);
        g2d.drawString(dateTard, 
            r.x + r.width/2 + (r.width/2 - tardWidth)/2, 
            r.y + milieu + (r.height/2 + fm.getHeight())/2 - 10);
        
        // Durée (centre haut)
        String duree = String.valueOf(tache.getDuree());
        int dureeWidth = fm.stringWidth(duree);
        g2d.drawString(duree, 
            r.x + (r.width - dureeWidth) / 2, 
            r.y + 35);
        
        // Marge (centrée tout en bas)
        String marge = "M:" + tache.getMarge();
        int margeWidth = fm.stringWidth(marge);
        g2d.drawString(marge, 
            r.x + (r.width - margeWidth) / 2, 
            r.y + r.height - 8);
    }
    
    private void dessinerLien(Graphics2D g2d, Rectangle source, Rectangle dest, boolean critique, int duree) {
        // Points de connexion
        int x1 = source.x + source.width;
        int y1 = source.y + source.height / 2;
        int x2 = dest.x;
        int y2 = dest.y + dest.height / 2;
        
        // Couleur et épaisseur du lien
        if (critique) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2.0f));
        } else {
            g2d.setColor(Color.BLUE);
            g2d.setStroke(new BasicStroke(1.0f));
        }
        
        // Ligne principale
        g2d.drawLine(x1, y1, x2, y2);
        
        // Flèche à l'arrivée
        int tailleFlèche = 8;
        double angle = Math.atan2(y2 - y1, x2 - x1);
        
        int[] xPoints = {
            x2,
            (int)(x2 - tailleFlèche * Math.cos(angle - Math.PI/6)),
            (int)(x2 - tailleFlèche * Math.cos(angle + Math.PI/6))
        };
        int[] yPoints = {
            y2,
            (int)(y2 - tailleFlèche * Math.sin(angle - Math.PI/6)),
            (int)(y2 - tailleFlèche * Math.sin(angle + Math.PI/6))
        };
        
        g2d.fillPolygon(xPoints, yPoints, 3);
        
        // Afficher la durée sur le lien si ce n'est pas 0
        if (duree > 0) {
            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
            String durStr = String.valueOf(duree);
            
            // Position au milieu du lien
            int midX = (x1 + x2) / 2;
            int midY = (y1 + y2) / 2 - 5;
            
            // Fond blanc pour la lisibilité
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(durStr);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(midX - textWidth/2 - 2, midY - fm.getHeight()/2 - 2, 
                        textWidth + 4, fm.getHeight() + 4);
            
            // Texte de la durée
            g2d.setColor(critique ? Color.RED : Color.BLUE);
            g2d.drawString(durStr, midX - textWidth/2, midY + fm.getHeight()/2 - 2);
        }
        
        // Remettre le stroke par défaut
        g2d.setStroke(new BasicStroke(1.0f));
    }
    
    public void toggleCheminCritique() {
        afficherCheminCritique = !afficherCheminCritique;
        repaint();
    }
    
    public void setGraphe(GrapheMPM graphe) {
        this.graphe = graphe;
        positions.clear();
        initialiserPositions();
        repaint();
    }
}