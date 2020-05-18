import java.util.List;
import java.util.Map;

public class Character {

    private Long id;
    private String name;
    private String alignment;
    private String gender;
    private String eyeColor;
    private String publisher;
    private String skinColor;
    private String hairColor;
    private String race;
    private float height;
    private float weight;
    private String status;
    private String appearances;
    private String firstAppearance;
    private String year;
    private String universe;
    private String identity;
    private Map<String,String> stats;
    private List<String> powers;

    public Character(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Character(Long id, String name, String alignment, String gender, String eyeColor, String publisher, String skinColor, int height, int weight, Map<String, String> stats, List<String> powers) {
        this.id = id;
        this.name = name;
        this.alignment = alignment;
        this.gender = gender;
        this.eyeColor = eyeColor;
        this.publisher = publisher;
        this.skinColor = skinColor;
        this.height = height;
        this.weight = weight;
        this.stats = stats;
        this.powers = powers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Map<String, String> getStats() {
        return stats;
    }

    public void setStats(Map<String, String> stats) {
        this.stats = stats;
    }

    public List<String> getPowers() {
        return powers;
    }

    public void setPowers(List<String> powers) {
        this.powers = powers;
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alignment='" + alignment + '\'' +
                ", gender='" + gender + '\'' +
                ", eyeColor='" + eyeColor + '\'' +
                ", publisher='" + publisher + '\'' +
                ", skinColor='" + skinColor + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", stats=" + stats +
                ", powers=" + powers +
                '}';
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setFirstAppearance(String firstAppearance) {
        this.firstAppearance = firstAppearance;
    }

    public String getFirstAppearance() {
        return firstAppearance;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getAppearances() {
        return appearances;
    }

    public void setAppearances(String appearances) {
        this.appearances = appearances;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setUniverse(String universe) {
        this.universe = universe;
    }

    public String getUniverse() {
        return universe;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }
}
