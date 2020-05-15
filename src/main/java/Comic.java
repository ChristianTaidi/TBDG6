import java.util.List;

public class Comic {

    private Long id;
    private String title;
    private int issueNumber;
    private List<Long> characterIds;

    public Comic(Long id, String title, int issueNumber, List<Long> characterIds) {
        this.id = id;
        this.title = title;
        this.issueNumber = issueNumber;
        this.characterIds = characterIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
