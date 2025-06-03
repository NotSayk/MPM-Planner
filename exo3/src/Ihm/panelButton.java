package src.Ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class panelButton extends JPanel
{

    private JButton btnPlusTot;
    private JButton btnPlusTard;
    private JButton reset;
    private JButton btnTheme;

    public panelButton()
    {
        this.setBackground(new Color(ABORT, 51, 51, 51));
        this.setLayout(new FlowLayout());

        this.btnPlusTot = new JButton("+ tôt");

        this.btnPlusTard = new JButton("+ tard");

        this.reset = new JButton("Réinitialiser");
        this.btnTheme = new JButton("Changer le thème");


        this.add(this.btnPlusTot);
        this.add(this.btnPlusTard);
        this.add(this.reset, BorderLayout.EAST);
        this.add(this.btnTheme, BorderLayout.SOUTH);
    }

    public void setOpaque(boolean b)
    {
        super.setOpaque(b);
    }
}