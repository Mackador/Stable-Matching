import java.util.ArrayList;

public class Student {

    private String name;
    private ArrayList<String> preferences;
    private String match;

    public Student(String name, ArrayList<String> preferences) {
        this.name = name;
        this.preferences = preferences;
    }

    public boolean isMatched() {
        if (this.match == null) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(ArrayList<String> preferences) {
        this.preferences = preferences;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

}
