package src.ihm;

import javax.swing.JFrame;
import src.Controleur;
import src.metier.TacheMPM;

public class FrameMPM extends JFrame
{
    
    private Controleur ctrl;

    private PanelMPM   panelMPM;
    private PanelPara  panelPara;

    public FrameMPM(Controleur ctrl) 
    {
        this.ctrl      = ctrl;
    
        this.panelPara = new PanelPara(this.ctrl);

        this.setTitle("MPM - Choix des paramètres");
        this.setSize (850, 600);

        this.setLocationRelativeTo(null);
        this.setLocation(this.getX() - 50, this.getY());

        this.add(this.panelPara);
    

        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    public void         setTheme      ( String  theme    ) { this.panelMPM.setTheme         ( theme    ); }
    public void         resetPositions(                  ) { this.panelMPM.resetPositions   (          ); }
    
    public String       getTheme            () { return this.panelMPM.getTheme  ();           }
    public double       getScale            () { return this.panelMPM.getScale();             }

    public PanelMPM     getPanelMPM         () { return this.panelMPM;                        }
    public PanelPara    getPanelPara        () { return this.panelPara;                       }

  
    public void changerPanel() 
    {
        this.getContentPane().remove(this.panelPara);
        this.panelMPM  = new PanelMPM(this.ctrl);
        this.add(this.panelMPM);
        this.maj();
    }


    public void changerTheme() 
    {
        String currentTheme = this.panelMPM.getTheme();
        
        switch (currentTheme) 
        {
            case "LIGHT" -> this.panelMPM.setTheme("DARK");
            case "DARK"  -> this.panelMPM.setTheme("LIGHT");
            default      -> throw new AssertionError();
        }
        
        this.maj();
    }

    public void setTacheSelectionnee(TacheMPM tache) 
    {
        this.panelMPM.setTacheSelectionnee(tache);
    }


    private void maj() 
    {
        this.revalidate();
        this.repaint();
    }
    
    public void setScale(double zoom) {this.panelMPM.setScale(zoom);}

}
