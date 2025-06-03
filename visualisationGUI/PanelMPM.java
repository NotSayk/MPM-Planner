import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class PanelMPM extends JPanel {
    private GrapheMPM graphe;
    private Map<TacheMPM, Rectangle> positions = new HashMap<>();
    private TacheMPM tacheCourante;
    private Point pointDrag;
    private boolean afficherCheminCritique = true;
    
    // Constantes réduites pour rapprocher les éléments
    private static final int TAILLE_X = 120; // Taille réduite
    private static final int TAILLE_Y = 50;  // Taille réduite
    private static final int ESPACE_H = 40;  // Espacement horizontal réduit
    private static final int ESPACE_V = 25;  // Espacement vertical réduit
    private static final int MARGE = 30;     // Marge de bordure réduite
    
    public PanelMPM(GrapheMPM graphe) {
        this.graphe = graphe;
        setBackground(Color.WHITE);
        
        // Gestionnaire de souris simplifié
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
        // Calculer la taille du graphe
        int maxNiveau = 0;
        Map<Integer, Integer> compteurParNiveau = new HashMap<>();
        
        for (TacheMPM tache : graphe.getTaches()) {
            int niveau = tache.getDateTot();
            maxNiveau = Math.max(maxNiveau, niveau);
            
            // Compter combien de tâches par niveau
            compteurParNiveau.put(niveau, compteurParNiveau.getOrDefault(niveau, 0) + 1);
        }
        
        // Définir la taille du panel en fonction du graphe
        int largeurTotale = MARGE + (maxNiveau + 1) * (TAILLE_X + ESPACE_H);
        int hauteurMax = 0;
        for (Map.Entry<Integer, Integer> entry : compteurParNiveau.entrySet()) {
            hauteurMax = Math.max(hauteurMax, entry.getValue());
        }
        int hauteurTotale = MARGE + hauteurMax * (TAILLE_Y + ESPACE_V);
        
        setPreferredSize(new Dimension(largeurTotale, hauteurTotale));
        
        // Placer les tâches
        Map<Integer, ArrayList<TacheMPM>> niveaux = new HashMap<>();
        for (TacheMPM tache : graphe.getTaches()) {
            int niveau = tache.getDateTot();
            niveaux.putIfAbsent(niveau, new ArrayList<>());
            niveaux.get(niveau).add(tache);
        }
        
        for (Map.Entry<Integer, ArrayList<TacheMPM>> entry : niveaux.entrySet()) {
            int niveau = entry.getKey();
            ArrayList<TacheMPM> taches = entry.getValue();
            int x = MARGE + niveau * (TAILLE_X + ESPACE_H);
            
            for (int i = 0; i < taches.size(); i++) {
                int y = MARGE + i * (TAILLE_Y + ESPACE_V);
                positions.put(taches.get(i), new Rectangle(x, y, TAILLE_X, TAILLE_Y));
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
        
        // Dessiner les liens
        for (TacheMPM tache : graphe.getTaches()) {
            Rectangle r1 = positions.get(tache);
            for (TacheMPM suivant : tache.getSuivants()) {
                Rectangle r2 = positions.get(suivant);
                if (r1 != null && r2 != null) {
                    dessinerFleche(g2d, r1, r2);
                }
            }
        }
        
        // Dessiner les tâches
        for (TacheMPM tache : graphe.getTaches()) {
            Rectangle r = positions.get(tache);
            if (r != null) {
                boolean critique = tache.getMarge() == 0 && afficherCheminCritique;
                dessinerTache(g2d, tache, r, critique);
            }
        }
    }
    
    private void dessinerTache(Graphics2D g2d, TacheMPM tache, Rectangle r, boolean critique) {
        // Couleur de fond selon criticité
        g2d.setColor(critique ? new Color(255, 200, 200) : new Color(220, 240, 255));
        g2d.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        
        // Bordure
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
        
        // Optimisation du texte pour mieux tenir dans les tâches réduites
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2d.setColor(Color.BLACK);
        
        // Nom de la tâche centré
        String nom = tache.getNom();
        FontMetrics fm = g2d.getFontMetrics();
        int largeur = fm.stringWidth(nom);
        g2d.drawString(nom, r.x + (r.width - largeur) / 2, r.y + 14);
        
        // Infos ultra-compactes sur une ligne
        String info = String.format("D:%d T:%d→%d M:%d", 
                                  tache.getDuree(), 
                                  tache.getDateTot(), 
                                  tache.getDateTard(),
                                  tache.getMarge());
        
        g2d.drawString(info, r.x + 5, r.y + 30);
        
        // Marge en rouge si critique
        if (critique) {
            g2d.setColor(Color.RED);
            g2d.drawString("CRITIQUE", r.x + 5, r.y + 44);
        }
    }
    
    private void dessinerFleche(Graphics2D g2d, Rectangle source, Rectangle dest) {
        // Points de départ et d'arrivée
        int x1 = source.x + source.width;
        int y1 = source.y + source.height / 2;
        int x2 = dest.x;
        int y2 = dest.y + dest.height / 2;
        
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(x1, y1, x2, y2);
        
        // Flèche simplifiée
        int taille = 5; // Taille de flèche réduite
        double angle = Math.atan2(y2 - y1, x2 - x1);
        g2d.fillPolygon(
            new int[] {x2, (int)(x2 - taille * Math.cos(angle - Math.PI/6)), (int)(x2 - taille * Math.cos(angle + Math.PI/6))},
            new int[] {y2, (int)(y2 - taille * Math.sin(angle - Math.PI/6)), (int)(y2 - taille * Math.sin(angle + Math.PI/6))},
            3
        );
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