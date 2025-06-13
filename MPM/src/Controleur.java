package src;

import java.util.List;

import src.ihm.FrameCalcul;
import src.ihm.FrameCritique;
import src.ihm.FrameMPM;
import src.ihm.FrameModification;
import src.ihm.GrilleDonneesModel;
import src.ihm.composants.Entite;
import src.metier.CheminCritique;
import src.metier.GrapheMPM;
import src.metier.TacheMPM;
import src.utils.DateUtils;   

/**
 * Contrôleur principal de l'application MPM.
 * Gère la coordination entre l'interface graphique et la logique métier.
 */
public class Controleur 
{
    private GrapheMPM         graphe;
    private FrameMPM          frameMPM;
    private FrameModification frameModification;
    private FrameCritique     frameCritique;
    private FrameCalcul       frameCalcul;

    public static void main(String[] args) { new Controleur(); }

    public Controleur() 
    {
        this.graphe   = new GrapheMPM();
        this.frameMPM = new FrameMPM (this);
    }

    /**
     * Initialise un nouveau projet MPM avec les paramètres spécifiés.
     * @param dateRef    Date de référence du projet
     * @param typeDate   Type de date (Début/Fin)
     * @param nomFichier Fichier source des tâches (optionnel)
     */
    public void initProjet(String dateRef, char typeDate, String nomFichier) 
    {
        if (nomFichier == null || nomFichier.isEmpty()) 
        {
            this.graphe.setDateRef(dateRef);
            this.graphe.setDateType(typeDate);
            this.afficherGraphe();
            return;
        }
        this.graphe.initTache(nomFichier);

        this.graphe.setDateRef(dateRef);
        this.graphe.setDateType(typeDate);
        this.graphe.calculerDates();
        
        if (typeDate == 'F') this.graphe.setDateFin(dateRef);
        
        this.graphe.initCheminCritique();
        this.graphe.initNiveauTaches();
        this.afficherGraphe();
    }

    /**
     * Initialise un projet complet avec chargement des entités et configuration du thème.
     */
    public void initComplet(char typeDate, String nomFichier) 
    {
        this.initProjet(this.getDateRef(), typeDate, nomFichier);
        this.afficherGraphe();
        
        this.frameMPM.setTheme(this.getTheme());
        this.graphe.chargerEntites(nomFichier, this.getEntites());
    }

    /**
     * Affiche la fenêtre de calculs détaillés.
     * Si la fenêtre n'existe pas, elle est créée.
     */
    public void afficherCalculs() 
    {
        if (this.frameCalcul == null)
            this.frameCalcul = new FrameCalcul(this);
        
        this.frameCalcul.setVisible(true);
        this.frameCalcul.actualiser();
    }


    public void afficherGraphe() { this.frameMPM.changerPanel(); }

    public void afficherModification()
    {
        if (this.frameModification == null) 
            this.frameModification = new FrameModification(this);
        this.frameModification.setVisible(true);
    }   

    public void cacherModification()
    {
        if (this.frameModification != null) 
            this.frameModification.setVisible(false);
    }

    public void afficherCritiques()
    {
        if (this.frameCritique == null)
            this.frameCritique = new FrameCritique(this);
        
        this.frameCritique.setVisible(true);
        this.frameCritique.recreerPanelCritique();
    }

    public void changerTheme() { this.frameMPM.changerTheme(); }

    public void resetPositions() 
    { 
        this.frameMPM.resetPositions(); 
        this.frameMPM.repaint(); 
    }

    public void changerAffichage()
    {
        this.setFormatDateTexte(!this.isFormatDateTexte());
        this.getFrameMPM().repaint();
    }

    /**
     * Ajoute une tâche à une position spécifique dans le graphe.
     * Met à jour l'affichage et le chemin critique si nécessaire.
     */
    public void ajouterTacheAPosition(TacheMPM tache, int position) 
    {
        this.graphe.ajouterTacheAPosition(tache, position);
        this.getFrameMPM().getPanelMPM().initEntites();
        this.getFrameMPM().getPanelMPM().repaint();
        if (this.graphe.getTaches().size() == 3) {this.getFrameMPM().getPanelMPM().cacherDates(); } 

        this.afficherCheminCritique(this.isCritique());
    }

    /**
     * Crée un nouveau projet avec les tâches DEBUT et FIN.
     * Initialise les positions des entités et sauvegarde le projet.
     */
    public void nouveauProjet()
    {
        this.graphe.nouveauProjet(this.getDateType());
        this.frameMPM.changerPanel();
        this.frameMPM.getPanelMPM().initEntites();
        
        int y = 100;
        for (Entite entite : this.getEntites()) 
        {
            if (entite.getTache().getNom().equals("DEBUT")) entite.setPosition(100, y);
            if (entite.getTache().getNom().equals("FIN")  ) entite.setPosition(500, y);
            y += 100;
        }
        
        if (this.frameModification != null) 
            this.frameModification.getGrilleDonneesModel().refreshTab();
        
        this.frameMPM.repaint();
        
        this.sauvegarderFichier();
    }

    public void copierTache()        
    { 
        this.graphe.copierTache(this.getFrameMPM().getTacheSelectionnee());   
        this.getGrilleDonneesModel().refreshTab();
    }

    public void collerTache          (              )  { this.graphe.collerTache(); this.afficherGraphe(); }
    public void modifierTacheFichier (TacheMPM tache)  { this.graphe.modifierTacheFichier(tache);          }
    public void supprimerTacheFichier(TacheMPM tache)  { this.graphe.supprimerTacheFichier(tache);         }

    public TacheMPM trouverTache     (String nomTache) { return this.graphe.trouverTache(nomTache);        }
    /**
     * Met à jour la durée d'une tâche et recalcule les chemins critiques.
     * Met à jour l'affichage et les couleurs des chemins critiques.
     */
    public void mettreAJourDureeTache(int index, int duree) 
    { 
        if(this.graphe.mettreAJourDureeTache(index, duree))
        {        
            this.afficherGraphe();
            this.setTheme(this.getTheme());
            
            this.graphe.initCheminCritique();
            
            boolean estCritiqueActuel = this.isCritique();
            this.afficherCheminCritique(false);
            this.afficherCheminCritique(estCritiqueActuel);
            
            this.getFrameMPM().getPanelMPM().setScale(this.getFrameMPM().getScale());
        }
        this.graphe.initCheminCritique();
    }

    /**
     * Modifie les tâches précédentes d'une tâche donnée.
     * Réinitialise le projet avec les nouvelles valeurs.
     */
    public void modifierPrecedents(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.graphe.modifierPrecedents(tacheModifier, nouvelleValeur); 
        this.initProjet(this.getDateRef(), this.getDateType(), this.getNomFichier());
    }

    /**
     * Modifie les tâches suivantes d'une tâche donnée.
     * Réinitialise le projet avec les nouvelles valeurs.
     */
    public void modifierSuivants(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.graphe.modifierSuivants(tacheModifier, nouvelleValeur); 
        this.initProjet(this.getDateRef(), this.getDateType(), this.getNomFichier());
    }

    /**
     * Modifie le nom d'une tâche.
     * Réinitialise le projet avec le nouveau nom.
     */
    public void modifierNom(TacheMPM tacheModifier, String nouvelleValeur) 
    {
        this.graphe.modifierNom(tacheModifier, nouvelleValeur);
        this.initProjet(this.getDateRef(), this.getDateType(), this.getNomFichier());
    }

    public void sauvegarder() { this.graphe.sauvegarderFichier(getTheme(), isCritique(), getDateRef(), this.frameMPM.getPanelMPM()); }

    public void chargerFichier()     
    { 
        this.graphe.chargerFichierB(this);  
        if (this.frameModification != null) 
            this.frameModification.getGrilleDonneesModel().refreshTab();                
    }

    public void sauvegarderFichier() 
    { 
        this.graphe.sauvegarderFichier(this.getTheme(), 
                                        this.isCritique(),
                                        this.getDateRef(),  
                                        this.frameMPM.getPanelMPM()); 
    }

    public void afficherCheminCritique(boolean afficher) 
    { 
        this.frameMPM.getPanelMPM().afficherCheminCritique(afficher); 
    }

    public List<CheminCritique> getCheminsCritiques() { return this.graphe.getCheminsCritiques(); }

    public void setZoom           (double zoom)                { this.frameMPM.getPanelMPM().setScale(zoom); }
    public void setTheme          (String theme)               { this.frameMPM.setTheme(theme);              }
    public void setFormatDateTexte(boolean format)             { this.graphe.setFormatDateTexte(format);     }
    public void setNiveauTache    (TacheMPM tache, int niveau) { this.graphe.setNiveauTache(tache, niveau);  }

    public String  getDateDuJour    (              ) { return DateUtils.getDateDuJour();                }
    public String  getDateRef       (              ) { return this.graphe.getDateRef();                 }
    public String  getGrapheToString(              ) { return this.graphe.toString();                   }
    public char    getDateType      (              ) { return this.graphe.getDateType();                }
    public int     getDureeProjet   (              ) { return this.graphe.getDureeProjet();             }
    public int     getNiveauTache   (TacheMPM tache) { return this.graphe.getNiveauTache(tache);        }
    public int[]   getNiveauxTaches (              ) { return this.graphe.getNiveaux();                 }
    public boolean isFormatDateTexte(              ) { return this.graphe.isFormatDateTexte();          }
    public boolean getAfficher      (              ) { return this.frameMPM.getPanelMPM().isCritique(); }

    public List<TacheMPM> getTaches () { return this.graphe.getTaches();    }
    public List<Entite>   getEntites() { return this.frameMPM.getEntites(); }

    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        if (this.frameModification == null) 
            this.frameModification = new FrameModification(this);
        
        return this.frameModification.getGrilleDonneesModel();
    }

    public TacheMPM  getTacheSelectionnee() { return this.frameMPM.getTacheSelectionnee(); }
    public FrameMPM  getFrameMPM         () { return this.frameMPM;                        }
    public GrapheMPM getGraphe           () { return this.graphe;                          }
    public String    getTheme            () { return this.graphe.getTheme();               }
    public boolean   isCritique          () { return this.graphe.isCritique();             }
    public String    getNomFichier       () { return this.graphe.getNomFichier();          }

    /**
     * Notifie l'affichage des dates au plus tôt pour un niveau donné
     * @param niveau Le niveau des tâches affichées
     */
    public void notifierAffichageDateTot(int niveau) 
    {
        if (this.frameCalcul != null && this.frameCalcul.isVisible()) 
        {
            this.frameCalcul.ajouterCalculDatesTotNiveau(niveau);
        }
    }

    /**
     * Notifie l'affichage des dates au plus tard pour un niveau donné
     * @param niveau Le niveau des tâches affichées
     */
    public void notifierAffichageDateTard(int niveau) 
    {
        if (this.frameCalcul != null && this.frameCalcul.isVisible()) 
        {
            this.frameCalcul.ajouterCalculDatesTardNiveau(niveau);
        }
    }
}