import java.util.List;
import java.util.ArrayList;

public class TacheMPM 
{
    private String         nom        ;
    private int            duree      ;
    private int            dateTot    ;
    private int            dateTard   ;
    private int            marge      ;
    private List<TacheMPM> precedents ;
    private List<TacheMPM> suivants   ;

    public TacheMPM(String nom, int duree, List<TacheMPM> precedents) 
    {
        this.nom        = nom                       ;
        this.duree      = duree                     ;
        this.precedents = precedents                ;
        this.suivants   = new ArrayList<TacheMPM>() ;
        this.dateTot    = 0                         ;
        this.dateTard   = 0                         ; 
        this.marge      = 0                         ;
    }

    public void setSuivants(List<TacheMPM> suivants) 
    {
        if (suivants == null) return ;
        this.suivants = suivants     ;
    }

    public String         getNom       ()     { return nom;        }
    public int            getDuree     ()     { return duree;      }
    public int            getDateTot   ()     { return dateTot;    }
    public int            getDateTard  ()     { return dateTard;   }
    public int            getMarge     ()     { return marge;      }
    public List<TacheMPM> getPrecedents()     { return precedents; }
    public List<TacheMPM> getSuivants  ()     { return suivants;   }

    public void setDateTot  ( int dateTot  )  { this.dateTot  = dateTot;  }
    public void setDateTard ( int dateTard )  { this.dateTard = dateTard; }
    public void setMarge    ( int marge    )  { this.marge    = marge;    }

    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append(nom).append(" : ").append(duree).append(" jour").append(duree > 1 ? "s" : "").append("\n")
        .append("    date au plus tôt  : ").append(GrapheMPM.ajouterJourDate(dateTot)).append("\n")
        .append("    date au plus tard : ").append(GrapheMPM.ajouterJourDate(dateTard)).append("\n")
        .append("    marge             : ").append(marge).append(marge == 0 || marge == 1 ? " jour" : " jours").append("\n")
        .append("    ").append(precedents.isEmpty() ? "pas de tâche précédente" : "tâches précédentes :").append("\n");
        
        for (TacheMPM t : precedents) sb.append("        ").append(t.getNom()).append("\n");
        
        sb.append("    liste des tâches suivantes :\n");
        if (suivants.isEmpty()) sb.append("        Aucune\n");
        else for (TacheMPM t : suivants) sb.append("        ").append(t.getNom()).append("\n");
        
        return sb.toString();
    }
    
}