package controleur;

import metier.GrapheMPM;
import metier.TacheMPM;
import metier.CheminCritique;
import metier.Fichier;
import ihm.FrameMPM;
import ihm.FrameModification;
import ihm.FrameCritique;
import utils.DateUtils;
import ihm.composants.Entite;

import java.util.List;
import java.util.ArrayList;

public class ControleurMPM {
    private GrapheMPM grapheMPM;
    private Fichier fichier;
    private FrameMPM frameMPM;
    private FrameModification frameModification;
    private FrameCritique frameCritique;
    private boolean formatDateTexte;
    private List<Entite> entites;

    public ControleurMPM() {
        this.grapheMPM = new GrapheMPM();
        this.entites = new ArrayList<>();
        this.frameMPM = new FrameMPM(this);
        this.formatDateTexte = false;
    }

    public void initProjet(String dateRef, char typeDate, String nomFichier) {
        this.fichier = new Fichier(this, nomFichier);
        
        this.grapheMPM.setDateRef(dateRef);
        this.grapheMPM.setDateType(typeDate);
        this.grapheMPM.calculerDates();
        
        if (typeDate == 'F') {
            this.grapheMPM.setDateRef(DateUtils.ajouterJourDate(dateRef, -this.grapheMPM.getDureeProjet()));
        }
        
        this.grapheMPM.initCheminCritique();
        this.grapheMPM.initNiveauTaches();
        this.afficherGraphe();
    }

    public void initComplet(char typeDate, String nomFichier) {
        this.initProjet(this.getDateRef(), typeDate, nomFichier);
        this.afficherGraphe();
        
        this.frameMPM.setTheme(this.fichier.getTheme());
        this.frameMPM.setCritique(this.fichier.isCritique());
        this.chargerEntites(nomFichier);
    }

    public void afficherGraphe() {
        this.frameMPM.changerPanel();
    }

    public void afficherModification() {
        if (this.frameModification == null) {
            this.frameModification = new FrameModification(this);
        }
        this.frameModification.setVisible(true);
    }

    public void cacherModification() {
        if (this.frameModification != null) {
            this.frameModification.setVisible(false);
        }
    }

    public void afficherCritiques() {
        if (this.frameCritique == null) {
            this.frameCritique = new FrameCritique(this);
        }
        this.frameCritique.setVisible(true);
    }

    public void ajouterTacheAPosition(TacheMPM tache, int position) {
        this.grapheMPM.getTaches().add(position, tache);
        this.grapheMPM.calculerDates();
        this.grapheMPM.initNiveauTaches();
        this.afficherGraphe();
    }

    public void mettreAJourDureeTache(int index, int duree) {
        TacheMPM tache = this.grapheMPM.getTaches().get(index);
        tache.setDuree(duree);
        this.grapheMPM.calculerDates();
        this.afficherGraphe();
    }

    public void modifierNom(TacheMPM tache, String nouveauNom) {
        tache.setNom(nouveauNom);
        this.afficherGraphe();
    }

    public void modifierPrecedents(TacheMPM tache, String nouveauxPrecedents) {
        tache.setPrecedents(nouveauxPrecedents);
        this.grapheMPM.calculerDates();
        this.grapheMPM.initNiveauTaches();
        this.afficherGraphe();
    }

    public void modifierSuivants(TacheMPM tache, String nouveauxSuivants) {
        tache.setSuivants(nouveauxSuivants);
        this.grapheMPM.calculerDates();
        this.grapheMPM.initNiveauTaches();
        this.afficherGraphe();
    }

    public void sauvegarder() {
        this.fichier.sauvegarder();
    }

    public void chargerFichier() {
        if (this.fichier == null) {
            String nomFichier = javax.swing.JOptionPane.showInputDialog(null, "Nom du fichier à charger :", "Charger un fichier", javax.swing.JOptionPane.QUESTION_MESSAGE);
            if (nomFichier == null || nomFichier.trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(null, "Aucun fichier spécifié.");
                return;
            }
            this.fichier = new Fichier(this, nomFichier);
        }
        this.fichier.chargerFichier();
    }

    public void changerTheme() {
        this.frameMPM.changerTheme();
    }

    public void resetPositions() {
        this.frameMPM.resetPositions();
        this.frameMPM.repaint();
    }

    public void changerAffichage() {
        this.formatDateTexte = !this.formatDateTexte;
        this.frameMPM.repaint();
    }

    public void chargerEntites(String chemin) {
        Fichier.chargerFichier(chemin, grapheMPM);
        grapheMPM.calculerDates();
        grapheMPM.initNiveauTaches();
        grapheMPM.initCheminCritique();
        chargerEntites();
        frameMPM.repaint();
    }

    public void chargerEntites() {
        entites.clear();
        for (TacheMPM tache : grapheMPM.getTaches()) {
            entites.add(new Entite(tache));
        }
    }

    // Getters et Setters
    public GrapheMPM getGrapheMPM() { return grapheMPM; }
    public Fichier getFichier() { return fichier; }
    public FrameMPM getFrameMPM() { return frameMPM; }
    public String getDateRef() { return grapheMPM.getDateRef(); }
    public boolean isFormatDateTexte() { return formatDateTexte; }
    public List<TacheMPM> getTaches() { return grapheMPM.getTaches(); }
    public List<CheminCritique> getCheminsCritiques() { return grapheMPM.getCheminsCritiques(); }
    public List<Entite> getEntites() { return entites; }
} 