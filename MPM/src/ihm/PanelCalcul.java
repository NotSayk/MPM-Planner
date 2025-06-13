package src.ihm;

import java.awt.*;
import javax.swing.*;
import java.util.List;

import src.Controleur;
import src.metier.TacheMPM;
import src.utils.DateUtils;

public class PanelCalcul extends JPanel 
{
    private Controleur ctrl;
    
    private JTextArea  textArea;
    private JScrollPane scrollPane;
    
    public PanelCalcul(Controleur ctrl) 
    {
        this.ctrl = ctrl;
        
        // Configuration du layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Création de la zone de texte pour afficher les calculs
        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        this.textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        
        // Ajout d'un ScrollPane pour gérer les grands textes
        this.scrollPane = new JScrollPane(this.textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        
        // Initialisation du contenu
        this.afficherEntete();
    }
    
    /**
     * Affiche l'en-tête explicatif dans la zone de texte
     */
    public void afficherEntete() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CALCULS MPM ===\n\n");
        sb.append("Ce panneau affiche les calculs détaillés des dates au plus tôt et au plus tard.\n");
        sb.append("Utilisez les boutons Plus tôt/Plus tard dans l'interface principale pour voir les calculs associés.\n\n");
        sb.append("Durée totale du projet: ").append(this.ctrl.getDureeProjet()).append(" jours\n");
        
        if (this.ctrl.getDateRef() != null && !this.ctrl.getDateRef().isEmpty()) 
        {
            sb.append("Date de référence: ").append(this.ctrl.getDateRef());
            sb.append(" (").append(this.ctrl.getDateType() == 'D' ? "Début" : "Fin").append(" du projet)\n");
        }
        
        this.textArea.setText(sb.toString());
        this.textArea.setCaretPosition(0);
    }
    
    /**
     * Ajoute les calculs des dates au plus tôt pour un niveau spécifique
     * @param niveau Le niveau des tâches à calculer
     */
    public void ajouterCalculDatesTotNiveau(int niveau) 
    {
        StringBuilder sb = new StringBuilder();
        List<TacheMPM> taches = this.ctrl.getTaches();
        
        sb.append("\n=== NIVEAU ").append(niveau).append(" - CALCUL DES DATES AU PLUS TÔT ===\n\n");
        
        boolean tachesNiveauTrouvees = false;
        
        for (TacheMPM tache : taches) 
        {
            if (this.ctrl.getNiveauTache(tache) != niveau)
                continue;
            
            tachesNiveauTrouvees = true;
            String nom = tache.getNom();
            
            // Ne pas calculer pour la tâche DEBUT
            if (nom.equals("DEBUT")) 
            {
                sb.append("Tâche DEBUT : Date au plus tôt = 0 (par définition)\n\n");
                continue;
            }
            
            List<TacheMPM> precedents = tache.getPrecedents();
            
            sb.append("Calcul date au plus tôt de ").append(nom).append(" :\n");
            
            if (precedents.isEmpty()) 
            {
                sb.append("Aucun précédent -> Date au plus tôt = 0\n");
            } 
            else 
            {
                int maxDate = 0;
                TacheMPM predChoisi = null;
                
                for (TacheMPM pred : precedents) 
                {
                    int datePred;
                    
                    // Si le précédent est DEBUT, sa date de fin est sa durée
                    if (pred.getNom().equals("DEBUT")) 
                    {
                        datePred = pred.getDuree();
                        sb.append("   avec DEBUT en précédent -> 0+").append(pred.getDuree())
                          .append(" = ").append(datePred).append("\n");
                    } 
                    else 
                    {
                        datePred = pred.getDateTot() + pred.getDuree();
                        sb.append("   avec ").append(pred.getNom()).append(" en précédent -> ")
                          .append(pred.getDateTot()).append("+").append(pred.getDuree())
                          .append(" = ").append(datePred).append("\n");
                    }
                    
                    if (datePred > maxDate) 
                    {
                        maxDate = datePred;
                        predChoisi = pred;
                    }
                }
                
                if (predChoisi != null) 
                {
                    sb.append("   Choix final: ").append(predChoisi.getNom())
                      .append(" (valeur: ").append(maxDate).append(")\n");
                }
            }
            
            sb.append("Date au plus tôt de ").append(nom).append(" = ").append(tache.getDateTot()).append("\n\n");
        }
        
        if (!tachesNiveauTrouvees) 
        {
            sb.append("Aucune tâche de niveau ").append(niveau).append(" trouvée.\n\n");
        }
        
        // Ajouter le texte calculé à la zone de texte existante
        this.textArea.append(sb.toString());
        // Faire défiler jusqu'à la fin du texte pour voir les nouvelles informations
        this.textArea.setCaretPosition(this.textArea.getText().length());
    }
    
    /**
     * Ajoute les calculs des dates au plus tard pour un niveau spécifique
     * @param niveau Le niveau des tâches à calculer
     */
    public void ajouterCalculDatesTardNiveau(int niveau) 
    {
        StringBuilder sb = new StringBuilder();
        List<TacheMPM> taches = this.ctrl.getTaches();
        
        sb.append("\n=== NIVEAU ").append(niveau).append(" - CALCUL DES DATES AU PLUS TARD ===\n\n");
        
        boolean tachesNiveauTrouvees = false;
        
        // Calculer d'abord la date de fin du projet
        int dateFinProjet = 0;
        TacheMPM tacheFin = null;
        
        for (TacheMPM tache : taches) 
        {
            if (tache.getNom().equals("FIN")) 
            {
                tacheFin = tache;
                dateFinProjet = tache.getDateTot();
                break;
            }
            if (tache.getSuivants().isEmpty()) 
            {
                dateFinProjet = Math.max(dateFinProjet, tache.getDateTot() + tache.getDuree());
            }
        }
        
        // Filtre les tâches du niveau spécifié
        for (TacheMPM tache : taches) 
        {
            if (this.ctrl.getNiveauTache(tache) != niveau)
                continue;
            
            tachesNiveauTrouvees = true;
            String nom = tache.getNom();
            
            // Ne pas recalculer pour la tâche FIN si elle existe
            if (nom.equals("FIN") && tacheFin != null) 
            {
                sb.append("Tâche FIN : Date au plus tard = ").append(tacheFin.getDateTard())
                  .append(" (= date au plus tôt)\n\n");
                continue;
            }
            
            List<TacheMPM> suivants = tache.getSuivants();
            
            sb.append("Calcul date au plus tard de ").append(nom).append(" :\n");
            
            if (suivants.isEmpty()) 
            {
                sb.append("   Aucun suivant -> Date au plus tard = ").append(dateFinProjet).append("\n");
            } 
            else 
            {
                int minDate = Integer.MAX_VALUE;
                TacheMPM suivChoisi = null;
                
                for (TacheMPM suiv : suivants) 
                {
                    int dateSuiv = suiv.getDateTard() - tache.getDuree();
                    sb.append("   avec ").append(suiv.getNom()).append(" en suivant -> ")
                      .append(suiv.getDateTard()).append("-").append(tache.getDuree())
                      .append(" = ").append(dateSuiv).append("\n");
                    
                    if (dateSuiv < minDate) 
                    {
                        minDate = dateSuiv;
                        suivChoisi = suiv;
                    }
                }
                
                if (suivChoisi != null) 
                {
                    sb.append("   Choix final: ").append(suivChoisi.getNom())
                      .append(" (valeur: ").append(minDate).append(")\n");
                }
            }
            
            sb.append("Date au plus tard de ").append(nom).append(" = ").append(tache.getDateTard()).append("\n");
            
            // Afficher la marge
            int marge = tache.getDateTard() - tache.getDateTot();
            sb.append("Marge de ").append(nom).append(" = ").append(marge).append(" jour(s)");
            if (marge == 0) 
            {
                sb.append(" (tâche critique)");
            }
            sb.append("\n\n");
        }
        
        if (!tachesNiveauTrouvees) 
        {
            sb.append("Aucune tâche de niveau ").append(niveau).append(" trouvée.\n\n");
        }
        
        // Ajouter le texte calculé à la zone de texte existante
        this.textArea.append(sb.toString());
        // Faire défiler jusqu'à la fin du texte pour voir les nouvelles informations
        this.textArea.setCaretPosition(this.textArea.getText().length());
    }
    
    /**
     * Efface le contenu de la zone de texte et affiche l'en-tête
     */
    public void reinitialiser() 
    {
        this.textArea.setText("");
        this.afficherEntete();
    }
    
    /**
     * Rafraîchit l'affichage des calculs
     */
    public void actualiser() 
    {
        this.reinitialiser();
    }
}