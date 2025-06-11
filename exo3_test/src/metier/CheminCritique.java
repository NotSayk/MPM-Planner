package metier;

import java.util.ArrayList;
import java.util.List;

public class CheminCritique {
    private List<TacheMPM> taches;

    public CheminCritique() {
        this.taches = new ArrayList<>();
    }

    public void ajouterTache(TacheMPM tache) {
        this.taches.add(tache);
    }

    public static boolean estCheminCritique(List<TacheMPM> chemin) {
        if (chemin.size() < 2) return false;
        
        for (int i = 0; i < chemin.size() - 1; i++) {
            TacheMPM tache1 = chemin.get(i);
            TacheMPM tache2 = chemin.get(i + 1);
            
            if (!estLienCritique(tache1, tache2)) {
                return false;
            }
        }
        
        return true;
    }

    public static boolean estLienCritique(TacheMPM tache1, TacheMPM tache2) {
        return tache1.getDateTot() + tache1.getDuree() == tache2.getDateTot() &&
               tache1.getDateTard() + tache1.getDuree() == tache2.getDateTard();
    }

    public List<TacheMPM> getTaches() {
        return taches;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Chemin critique : ");
        
        for (int i = 0; i < taches.size(); i++) {
            sb.append(taches.get(i).getNom());
            if (i < taches.size() - 1) {
                sb.append(" -> ");
            }
        }
        
        return sb.toString();
    }
} 