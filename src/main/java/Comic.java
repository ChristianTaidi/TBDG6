import java.util.List;

public class Comic {

    private Long id;
    private String title;
    private int issueNumber;
    private int year;
    private List<Long> characterIds;

    public Comic(Long id, String title, int issueNumber, List<Long> characterIds) {
        this.id = id;
        this.title = title;
        this.issueNumber = issueNumber;
        this.characterIds = characterIds;
        if(title.contains("#")){
            String[] arrOfStr = title.split("#", 2);
            String yearAux=  arrOfStr[0].substring(arrOfStr[0].length()-7, arrOfStr[0].length()-1);
            if(yearAux.contains("(") && yearAux.contains(")")){
                yearAux = yearAux.substring(1, 5);
                //System.out.println("Year= "+yearAux);
                
                if(yearAux!= null){
                    this.year= Integer.parseInt(yearAux);
                }
            }   
        }
        System.out.println(this.year);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = issueNumber;
    }

    public List<Long> getCharacterIds() {
        return characterIds;
    }

    public void setCharacterIds(List<Long> characterIds) {
        this.characterIds = characterIds;
    }
}
