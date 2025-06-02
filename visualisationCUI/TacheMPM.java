import java.util.List;
import java.util.ArrayList;

public class TacheMPM 
{
    private String         nom        ;
    private int            duree      ;
    private String         dateTot    ;
    private String         dateTard   ;
    private String         marge      ;
    private List<TacheMPM> precedents ;
    private List<TacheMPM> suivants   ;

    public TacheMPM(String nom, int duree, List<TacheMPM> precedents) 
    {
        this.nom        = nom                       ;
        this.duree      = duree                     ;
        this.precedents = precedents                ;
        this.suivants   = new ArrayList<TacheMPM>() ;
        this.dateTot    = "0"                       ;
        this.dateTard   = "0"                       ; 
        this.marge      = ""                        ;
    }

    public void setSuivants(List<TacheMPM> suivants) 
    {
        if (suivants == null) return ;
        this.suivants = suivants     ;
    }

    public String         getNom       () { return nom;        }
    public int            getDuree     () { return duree;      }
    public String         getDateTot   () { return dateTot;    }
    public String         getDateTard  () { return dateTard;   }
    public String         getMarge     () { return marge;      }
    public List<TacheMPM> getPrecedents() { return precedents; }
    public List<TacheMPM> getSuivants  () { return suivants;   }

    public void setDateTot(String dateTot) 
    {
        if (dateTot == null || dateTot.isEmpty()) return ;
        this.dateTot = dateTot;
    }

    public void setDateTard(String dateTard) 
    {
        if (dateTard == null || dateTard.isEmpty()) return ;
        this.dateTard = dateTard;
    }

    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append(nom).append(" : ").append(duree).append(" jour").append(duree > 1 ? "s" : "").append("\n")
        .append("    date au plus tôt  : ").append(dateTot).append("\n")
        .append("    date au plus tard : ").append(dateTard).append("\n")
        .append("    marge             : ").append(marge).append(marge.equals("0") || marge.equals("1") ? " jour" : " jours").append("\n")
        .append("    ").append(precedents.isEmpty() ? "pas de tâche précédente" : "tâches précédentes :").append("\n");
        
        for (TacheMPM t : precedents) sb.append("        ").append(t.getNom()).append("\n");
        
        sb.append("    liste des tâches suivantes :\n");
        if (suivants.isEmpty()) sb.append("        Aucune\n");
        else for (TacheMPM t : suivants) sb.append("        ").append(t.getNom()).append("\n");
        
        return sb.toString();
    }
    
}