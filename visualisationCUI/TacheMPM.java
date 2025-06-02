import java.util.List;
import java.util.ArrayList;

public class TacheMPM 
{
    private String         nom        ;
    private String         duree      ;
    private String         dateTot    ;
    private String         dateTard   ;
    private String         marge      ;
    private List<TacheMPM> precedents ;
    private List<TacheMPM> suivants   ;

    public TacheMPM(String nom, String duree, List<TacheMPM> precedents) 
    {
        this.nom        = nom             ;
        this.duree      = duree           ;
        this.precedents = precedents      ;
        this.suivants   = new ArrayList<TacheMPM>();
        this.dateTot    = ""              ;
        this.dateTard   = ""              ;
        this.marge      = ""              ;
    }

    public void setSuivants(List<TacheMPM> suivants) 
    {
        if (suivants == null) {
            return;
        }
        this.suivants = suivants;
    }

    public String         getNom       () { return nom;        }
    public String         getDuree     () { return duree;      }
    public String         getDateTot   () { return dateTot;    }
    public String         getDateTard  () { return dateTard;   }
    public String         getMarge     () { return marge;      }
    public List<TacheMPM> getPrecedents() { return precedents; }
    public List<TacheMPM> getSuivants  () { return suivants;   }


    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Tâche: ").append(nom)
          .append(", Durée: ").append(duree)
          .append(", Date Tot: ").append(dateTot)
          .append(", Date Tard: ").append(dateTard)
          .append(", Marge: ").append(marge)
          .append(", Précédents: ");
        
          if (precedents.size() == 0) {
            sb.append("Aucun ");
          }
          else {for (TacheMPM tache : precedents) {
            sb.append(tache.getNom()).append(" ");
        }}
        
        
        sb.append(", Suivants: ");
        if (suivants.size() == 0) {
            sb.append("Aucun ");
          }
          else {for (TacheMPM tache : suivants) {
            sb.append(tache.getNom()).append(" ");
        }}
        
        return sb.toString().trim();
    }
    
}
