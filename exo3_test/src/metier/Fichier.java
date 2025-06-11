package metier;

import controleur.ControleurMPM;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Fichier {
    private ControleurMPM controleur;
    private String nomFichier;
    private String theme;
    private boolean critique;

    public Fichier(ControleurMPM controleur, String nomFichier) {
        this.controleur = controleur;
        this.nomFichier = nomFichier;
        this.theme = "clair";
        this.critique = false;
    }

    public void sauvegarder() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichier))) {
            // Sauvegarder le thème
            writer.println("THEME=" + theme);
            
            // Sauvegarder l'état critique
            writer.println("CRITIQUE=" + critique);
            
            // Sauvegarder les tâches
            for (TacheMPM tache : controleur.getTaches()) {
                writer.println("TACHE=" + tache.getNom() + "," + tache.getDuree());
                
                // Sauvegarder les précédents
                StringBuilder precedents = new StringBuilder();
                for (TacheMPM precedent : tache.getPrecedents()) {
                    if (precedents.length() > 0) precedents.append(",");
                    precedents.append(precedent.getNom());
                }
                writer.println("PRECEDENTS=" + precedents.toString());
                
                // Sauvegarder les suivants
                StringBuilder suivants = new StringBuilder();
                for (TacheMPM suivant : tache.getSuivants()) {
                    if (suivants.length() > 0) suivants.append(",");
                    suivants.append(suivant.getNom());
                }
                writer.println("SUIVANTS=" + suivants.toString());
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    public void chargerFichier() {
        try (BufferedReader reader = new BufferedReader(new FileReader(nomFichier))) {
            String ligne;
            TacheMPM tacheCourante = null;
            
            while ((ligne = reader.readLine()) != null) {
                if (ligne.startsWith("THEME=")) {
                    theme = ligne.substring(6);
                } else if (ligne.startsWith("CRITIQUE=")) {
                    critique = Boolean.parseBoolean(ligne.substring(9));
                } else if (ligne.startsWith("TACHE=")) {
                    String[] parts = ligne.substring(6).split(",");
                    tacheCourante = new TacheMPM(parts[0], Integer.parseInt(parts[1]));
                    controleur.getGrapheMPM().getTaches().add(tacheCourante);
                } else if (ligne.startsWith("PRECEDENTS=") && tacheCourante != null) {
                    tacheCourante.setPrecedents(ligne.substring(11));
                } else if (ligne.startsWith("SUIVANTS=") && tacheCourante != null) {
                    tacheCourante.setSuivants(ligne.substring(9));
                }
            }
            
            // Mettre à jour les liens entre les tâches
            for (TacheMPM tache : controleur.getTaches()) {
                mettreAJourLiens(tache);
            }
            
            controleur.getGrapheMPM().calculerDates();
            controleur.getGrapheMPM().initNiveauTaches();
            controleur.getGrapheMPM().initCheminCritique();
            
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
        }
    }

    private void mettreAJourLiens(TacheMPM tache) {
        // Mettre à jour les précédents
        for (String nomPrecedent : tache.getPrecedents().toString().split(",")) {
            nomPrecedent = nomPrecedent.trim();
            if (!nomPrecedent.isEmpty()) {
                for (TacheMPM autreTache : controleur.getTaches()) {
                    if (autreTache.getNom().equals(nomPrecedent)) {
                        tache.getPrecedents().add(autreTache);
                        autreTache.getSuivants().add(tache);
                        break;
                    }
                }
            }
        }
    }

    // Getters et Setters
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    
    public boolean isCritique() { return critique; }
    public void setCritique(boolean critique) { this.critique = critique; }

    public static void chargerFichier(String chemin, GrapheMPM graphe) {
        try (BufferedReader reader = new BufferedReader(new FileReader(chemin))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parties = ligne.split(";");
                if (parties.length >= 3) {
                    String nom = parties[0];
                    int duree = Integer.parseInt(parties[1]);
                    String[] precedents = parties[2].split(",");
                    
                    TacheMPM tache = new TacheMPM(nom, duree);
                    for (String precedent : precedents) {
                        if (!precedent.isEmpty()) {
                            TacheMPM tachePrecedente = graphe.getTaches().stream()
                                .filter(t -> t.getNom().equals(precedent))
                                .findFirst()
                                .orElse(null);
                            if (tachePrecedente != null) {
                                tache.ajouterPrecedent(tachePrecedente);
                            }
                        }
                    }
                    graphe.getTaches().add(tache);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sauvegarderFichier(String chemin, GrapheMPM graphe) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(chemin))) {
            for (TacheMPM tache : graphe.getTaches()) {
                StringBuilder ligne = new StringBuilder();
                ligne.append(tache.getNom()).append(";");
                ligne.append(tache.getDuree()).append(";");
                
                List<String> nomsPrecedents = new ArrayList<>();
                for (TacheMPM precedent : tache.getPrecedents()) {
                    nomsPrecedents.add(precedent.getNom());
                }
                ligne.append(String.join(",", nomsPrecedents));
                
                writer.println(ligne.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 