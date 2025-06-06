package src;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import src.Ihm.FrameMPM;
import src.Ihm.FrameModification;
import src.Ihm.composants.Entite;
import src.Metier.Fichier;
import src.Metier.GrapheMPM;
import src.Metier.TacheMPM;
import src.utils.DateUtils;   


public class Controleur 
{
    
    private FrameMPM           frameMPM         ;
    private FrameModification  frameModification;

    private Fichier            fichier;
    private GrapheMPM          graphe;

    private String             dateRef ;
    private char               typeDate;

    public static void main(String[] args) { new Controleur(); }

    public Controleur() 
    {
        this.graphe   = new GrapheMPM(this             );
        this.frameMPM = new FrameMPM (this, this.graphe);
    }

    public void initProjet(String dateRef, char typeDate, String nomFichier) 
    {
        this.fichier  = new Fichier(this.graphe, nomFichier); 

        this.dateRef  = dateRef ;
        this.typeDate = typeDate;

        this.graphe.setDateRef   (dateRef) ;
        this.graphe.setTypeDate  (typeDate);

        this.graphe.calculerDates     ();
        this.graphe.initCheminCritique();
        this.graphe.initNiveauTaches  ();

        this.afficherGraphe();
    }

    public void initComplet(char typeDate, String nomFichier) 
    {
        this.initProjet(this.dateRef, typeDate, nomFichier);

        this.afficherGraphe();
        this.frameMPM.setTheme   (this.fichier.getTheme  ());
        this.frameMPM.setCritique(this.fichier.isCritique());

        this.chargerEntites(nomFichier);

    }

    public void afficherGraphe() { this.frameMPM.changerPanel(); }

    private void chargerEntites(String nomFichier)
    {
        for(Entite e : this.getEntites())
        {
            int[] pos = fichier.getLocation(e.getTache(), nomFichier);
            e.setPosition(pos[0], pos[1]);
        }
    }

    public void afficherModification()
    {
        if(this.frameModification == null) this.frameModification = new FrameModification(this);
        this.frameModification.setVisible(true);
    }   

    public void cacherModification() 
    {
        if(this.frameModification != null) this.frameModification.setVisible(false);
    }

    public void changerTheme       () { this.frameMPM.changerTheme  ();                               }
    public void resetPositions     () { this.frameMPM.resetPositions(); this.frameMPM.repaint();      }
    public void sauvegarder        () { this.fichier.sauvegarder    ();                               }
    public void sauvegarderFichier () { this.fichier.sauvegarderFichier(this.getTheme  (),
                                                                        this.isCritique(),
                                                                        this.dateRef     ,  
                                                                        this.frameMPM.getPanelMPM()); }

    public void mettreAJourDureeTache (int index, int duree) 
    {
        List<TacheMPM> taches = this.fichier.getLstTacheMPMs();
        if (index >= 0 && index < taches.size()) 
        {
            TacheMPM tache = taches.get(index);
            tache.setDuree(duree);
            this.fichier.modifierTacheFichier(tache);
            initProjet(dateRef, typeDate, fichier.getNomFichier());
        } 
        else 
        {
            System.err.println("Index de tâche invalide : " + index);
        }
    }
    public void modifierPrecedents(TacheMPM tache, String nouveauxPrecedents) {
        Set<TacheMPM> nouveauxPrecedentsSet = new HashSet<>();
        
        if (!nouveauxPrecedents.isEmpty()) {
            for (String nomTache : nouveauxPrecedents.split(",")) {
                TacheMPM precedent = this.graphe.trouverTache(nomTache.trim());
                if (precedent != null && !precedent.equals(tache)) {
                    nouveauxPrecedentsSet.add(precedent);
                }
            }
        }
        
        // Mettre à jour les relations
        for (TacheMPM ancienPrecedent : tache.getPrecedents()) {
            ancienPrecedent.getSuivants().remove(tache);
        }
        
        tache.setPrecedents(new ArrayList<>(nouveauxPrecedentsSet));
        for (TacheMPM precedent : nouveauxPrecedentsSet) {
            precedent.getSuivants().add(tache);
        }
        
        // Mettre à jour le graphe et l'interface
        this.fichier.modifierTacheFichier(tache);
        initProjet(dateRef, typeDate, fichier.getNomFichier());
    }

    public void modifierSuivants(TacheMPM tache, String nouveauxSuivants) {
        Set<TacheMPM> nouveauxSuivantsSet = new HashSet<>();
        
        if (!nouveauxSuivants.isEmpty()) {
            for (String nomTache : nouveauxSuivants.split(",")) {
                TacheMPM suivant = this.graphe.trouverTache(nomTache.trim());
                if (suivant != null && !suivant.equals(tache)) {
                    nouveauxSuivantsSet.add(suivant);
                }
            }
        }
        
        // Mettre à jour les relations
        for (TacheMPM ancienSuivant : tache.getSuivants()) {
            ancienSuivant.getPrecedents().remove(tache);
        }
        
        tache.setSuivants(new ArrayList<>(nouveauxSuivantsSet));
        for (TacheMPM suivant : nouveauxSuivantsSet) {
            suivant.getPrecedents().add(tache);
        }
        
        // Mettre à jour le graphe et l'interface
        this.fichier.modifierTacheFichier(tache);
        initProjet(dateRef, typeDate, fichier.getNomFichier());
    }
    

    public String  getDateDuJour     () { return DateUtils.getDateDuJour();                       }
    public String  getGrapheString   () { return this.graphe.toString   ();                       }
    public String  getDateReference  () { return this.dateRef;                                    }
    public String  getTheme          () { return this.frameMPM.getTheme ();                       }
    
    public char    getTypeDate       () { return this.typeDate;                                   }
    public int     getDureeProjet    () { return this.graphe.getDureeProjet();                    }
    public int     getNiveauTaches   (TacheMPM tache) { return this.graphe.getNiveauTache(tache); }
    public int[]   getNiveauxTaches  () { return this.graphe.getNiveaux();                        }
    
    public List<TacheMPM> getTaches  () { return this.fichier.getLstTacheMPMs();                  }
    public List<Entite>   getEntites () { return this.frameMPM.getEntites    ();                  }

    public boolean        isCritique () { return this.frameMPM.isCritique    ();                  }

    public FrameMPM       getFrameMPM() { return this.frameMPM;                                   }
    public GrapheMPM getGraphe  () { return this.graphe;                                     }

    public Fichier          getFichier  () { return this.fichier;                                    }

}