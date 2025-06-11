package controleur;

import java.util.List;
import ihm.FrameMPM;
import ihm.FrameModification;
import ihm.FrameCritique;
import ihm.GrilleDonneesModel;
import ihm.composants.Entite;
import metier.CheminCritique;
import metier.Fichier;
import metier.GrapheMPM;
import metier.TacheMPM;
import utils.DateUtils;   

public class Controleur 
{
    /*-------------------------*
     * Attributs du contrôleur *
     *-------------------------*/
    private FrameMPM          frameMPM;
    private FrameModification frameModification;
    private FrameCritique     frameCritique;

    private GrapheMPM         grapheMPM;
    private Fichier           fichier;
    private boolean           formatDateTexte = false; 

    /*-----------------------------*
     * Point d'entrée du programme *
     *-----------------------------*/
    public static void main(String[] args) { new Controleur(); }

    /*-------------------------*
     * Constructeurs principal *
     *-------------------------*/
    public Controleur() 
    {
        this.grapheMPM = new GrapheMPM();
        this.frameMPM  = new FrameMPM(this);
    }

    /*--------------------------*
     * Initialisation du projet *
     *--------------------------*/
    public void initProjet(String dateRef, char typeDate, String nomFichier) 
    {
        this.fichier = new Fichier(nomFichier); 
        this.grapheMPM.setDateRef(dateRef);
        this.grapheMPM.setDateType(typeDate);
        this.grapheMPM.calculerDates();
        
        if (typeDate == 'F') 
            this.grapheMPM.setDateFin(dateRef);
        
        this.grapheMPM.initCheminCritique();
        this.grapheMPM.initNiveauTaches();
        this.afficherGraphe();
    }

    public void initComplet(char typeDate, String nomFichier) 
    {
        this.initProjet(this.getDateRef(), typeDate, nomFichier);
        this.afficherGraphe();
        
        this.frameMPM.setTheme(this.fichier.getTheme());
        this.frameMPM.setCritique(this.fichier.isCritique());
        this.grapheMPM.chargerEntites(nomFichier);
    }

    /*--------------------------*
     * Affichage des interfaces *
     *--------------------------*/
    public void afficherGraphe() 
    { 
        this.frameMPM.changerPanel(); 
    }

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

    /*---------------------------*
     * Actions utilisateur - IHM *
     *---------------------------*/
    public void ajouterTacheAPosition(TacheMPM tache, int position) 
    {
        this.grapheMPM.ajouterTacheAPosition(tache, position);
        this.fichier.ajouterTacheFichier(tache);
        this.afficherGraphe();
    }

    public void afficherCritiques()
    {
        if (this.frameCritique == null)
            this.frameCritique = new FrameCritique(this);
        this.frameCritique.setVisible(true);
    }

    public void changerTheme() 
    { 
        this.frameMPM.changerTheme(); 
    }

    public void resetPositions() 
    { 
        this.frameMPM.resetPositions(); 
        this.frameMPM.repaint(); 
    }

    public void sauvegarder() 
    { 
        this.fichier.sauvegarder(); 
    }

    public void chargerFichier() 
    { 
        this.fichier.chargerFichierB(); 
    }

    public void copierTache() 
    { 
        this.grapheMPM.copierTache(); 
    }

    public void collerTache() 
    { 
        this.grapheMPM.collerTache(); 
    }

    public void chercherTache() 
    { 
        this.grapheMPM.chercherTache(); 
    }

    public void sauvegarderFichier() 
    { 
        this.fichier.sauvegarderFichier(
            this.getTheme(), 
            this.isCritique(),
            this.getDateRef(),  
            this.frameMPM.getPanelMPM()
        ); 
    }

    public void mettreAJourDureeTache(int index, int duree) 
    { 
        this.grapheMPM.mettreAJourDureeTache(index, duree);
        this.fichier.modifierTacheFichier(this.grapheMPM.getTaches().get(index));
        this.afficherGraphe();
    }

    public void modifierPrecedents(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.grapheMPM.modifierPrecedents(tacheModifier, nouvelleValeur);
        this.fichier.modifierTacheFichier(tacheModifier);
        this.afficherGraphe();
    }

    public void modifierSuivants(TacheMPM tacheModifier, String nouvelleValeur) 
    { 
        this.grapheMPM.modifierSuivants(tacheModifier, nouvelleValeur);
        this.fichier.modifierTacheFichier(tacheModifier);
        this.afficherGraphe();
    }

    public void modifierNom(TacheMPM tacheModifier, String nouvelleValeur) 
    {
        this.grapheMPM.modifierNom(tacheModifier, nouvelleValeur);
        this.fichier.modifierTacheFichier(tacheModifier);
        this.afficherGraphe();
    }

    public void changerAffichage()
    {
        this.setFormatDateTexte(!this.isFormatDateTexte());
        this.getFrameMPM().repaint();
    } 

    /*-----------------------------------------*
     * Accesseurs - Informations sur le projet *
     *-----------------------------------------*/
    public void   setZoom(double zoom) { this.frameMPM.getPanelMPM().setScale(zoom); }
    public void   setTheme(String theme) { this.frameMPM.setTheme(theme); }
    public void   setFormatDateTexte(boolean format) { this.formatDateTexte = format; }
    public void   setNiveauTache(TacheMPM tache, int niveau) { this.grapheMPM.setNiveauTache(tache, niveau); }

    public void setAfficherDateTot(boolean val) { this.grapheMPM.setAfficherDateTot(val); }
    public void setAfficherDateTard(boolean val) { this.grapheMPM.setAfficherDateTard(val); }
    public void setCritique(boolean val) { this.grapheMPM.setCritique(val); }

    public String getDateDuJour() { return DateUtils.getDateDuJour(); }
    public String getTheme() { return this.frameMPM.getTheme(); }
    public String getDateRef() { return this.grapheMPM.getDateRef(); }
    public String getGrapheToString() { return this.grapheMPM.toString(); }
    public int getNumNiveauxTot() { return this.grapheMPM.getNumNiveauxTot(); }
    public int getNumNiveauxTard() { return this.grapheMPM.getNumNiveauxTard(); }

    public void setNumNiveauxTot(int val) { this.grapheMPM.setNumNiveauxTot(val); }
    public void setNumNiveauxTard(int val) { this.grapheMPM.setNumNiveauxTard(val); }

    public void setTacheSelectionnee(TacheMPM tache) { this.grapheMPM.setTacheSelectionnee(tache); }
    
    public boolean  isGriseTot() { return this.grapheMPM.isGriseTot(); }
    public boolean  isGriseTard() { return this.grapheMPM.isGriseTard(); }

    public char   getDateType() { return this.grapheMPM.getDateType(); }
    
    public int    getDureeProjet() { return this.grapheMPM.getDureeProjet(); }
    public int    getNiveauTache(TacheMPM tache) { return this.grapheMPM.getNiveauTache(tache); }
    public int[]  getNiveauxTaches() { return this.grapheMPM.getNiveaux(); }
    
    public boolean isFormatDateTexte() { return this.formatDateTexte; }
    public boolean isCritique() { return this.grapheMPM.isCritique(); }
    public boolean isAfficherDateTot() { return this.grapheMPM.isAfficherDateTot(); }
    public boolean isAfficherDateTard() { return this.grapheMPM.isAfficherDateTard(); }

    public List<CheminCritique> getCheminsCritiques() { return this.grapheMPM.getCheminsCritiques(); }

    public void afficherCheminCritique(boolean afficher) 
    { 
        this.frameMPM.getPanelMPM().afficherCheminCritique(afficher); 
    }

    public GrilleDonneesModel getGrilleDonneesModel() 
    {
        if (this.frameModification == null) 
            this.frameModification = new FrameModification(this);
        return this.frameModification.getGrilleDonneesModel();
    }

    public String afficherCheminsCritiques() { return this.grapheMPM.afficherCheminsCritiques(); }

    /*--------------------------------------------*
     * Accesseurs - Données du graphe et de l'IHM *
     *--------------------------------------------*/
    public List<TacheMPM> getTaches() { return this.grapheMPM.getTaches(); }
    public List<Entite>   getEntites() { return this.grapheMPM.getEntites(); }

    /*---------------------------*
     * Accesseurs - État général *
     *---------------------------*/
    public TacheMPM       getTacheSelectionnee() { return this.grapheMPM.getTacheSelectionnee(); }
    public FrameMPM       getFrameMPM()          { return this.frameMPM; }
    public GrapheMPM      getGraphe()            { return this.grapheMPM; }
    public Fichier        getFichier()           { return this.fichier; }
}