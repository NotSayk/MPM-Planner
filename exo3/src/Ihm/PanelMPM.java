package src.Ihm;

import src.Metier.GrapheMPM;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import src.Controleur;

public class PanelMPM extends JPanel 
{
    private GrapheMPM graphe;
    private JLabel label;
    private Controleur ctrl;

    public PanelMPM(GrapheMPM graphe, Controleur ctrl) 
    {
        this.graphe = graphe;
        this.label = new JLabel("Graphe MPM");
        this.add(label);
        this.setBackground(Color.WHITE);
        this.ctrl = ctrl;

        this.setPreferredSize(new Dimension(800, 800));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int elementsCount = graphe.getTaches().size();

        for (int i = 0; i < elementsCount; i++) {
            // Constantes pour la disposition
            final int MARGE = 50;
            final int ESPACEMENT = 120;
            final int TAILLE_CASE = 70;
            final int DEMI_CASE = TAILLE_CASE / 2;

            // Calcul de la position
            int lig = i / 10;
            int col = i % 10;

            int x = MARGE + col * ESPACEMENT;
            int y = MARGE + lig * ESPACEMENT;

            // Dessin du rectangle
            g.setColor(Color.BLUE);
            g.fillRect(x, y, TAILLE_CASE, TAILLE_CASE);

            // Dessin du texte et des lignes
            g.setColor(Color.WHITE);
            String nomTache = graphe.getTaches().get(i).getNom();
            g.drawString(nomTache, x + DEMI_CASE - 5, y + DEMI_CASE - 20);
            g.drawLine(x + DEMI_CASE, y + DEMI_CASE-5, x + DEMI_CASE, y + 80);
            g.drawLine(x, y + 30, x + 70, y + 30);

            // Dates
            String dateTot = graphe.getTaches().get(i).getDateTot() + "";
            String dateTard = graphe.getTaches().get(i).getDateTard() + "";
            g.drawString("" + dateTard, x + 40, y + 60);
            g.drawString("" + dateTot, x + 5, y + 60);
        }
    }
}