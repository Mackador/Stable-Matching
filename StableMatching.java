import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class StableMatching {

    private String[][] matching;

    // Creates a list of employers from the given .csv or .txt files
    public ArrayList<Employer> addEmployers(String fileName, ArrayList<Employer> listOfEmployers) {
        File file = new File(fileName);
        try {
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                String[] line = inputStream.nextLine().split(",");
                ArrayList<String> preferences = new ArrayList<String>();
                for (int i = 1 ; i < line.length ; i++) {
                    preferences.add(line[i]);
                }
                Employer e = new Employer(line[0], preferences);
                listOfEmployers.add(e);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return listOfEmployers;
    }
    
    // Creates a list of students from the given .csv or .txt files
    public ArrayList<Student> addStudents(String fileName, ArrayList<Student> listOfStudents) {
        File file = new File(fileName);
        try {
            Scanner inputStream = new Scanner(file);
            while (inputStream.hasNext()) {
                String[] line = inputStream.nextLine().split(",");
                ArrayList<String> preferences = new ArrayList<String>();
                for (int i = 1 ; i < line.length ; i++) {
                    preferences.add(line[i]);
                }
                Student s = new Student(line[0], preferences);
                listOfStudents.add(s);
            }
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return listOfStudents;
    }
    
    // Using the student's name, the method finds the student with that name and returns the student
    public Student findStudent(ArrayList<Student> students, String studentsName) {
        for (Student student : students) {
            if (student.getName().equals(studentsName)) {
                return student;
            }
        }
        return null;
    }
    
    // Using the employer's name, the method finds the employer with that name and returns the employer
    public Employer findEmployer(ArrayList<Employer> employers, String employersName) {
        for (Employer employer : employers) {
            if (employer.getName().equals(employersName)) {
                return employer;
            }
        }
        return null;
    }
    
    // Verifies if all the matches have been found, by checking if matching is full
    public boolean allMatchesFound(String[][] matching) {
        for (String[] element : matching) {
            if (element[1] == null) {
                return false;
            }
        }
        return true;
    }
    
    // Checks if the student prefers their current partner or their potential new partner
    public boolean prefersNew(Student mostPreferredStudent, String currentPartner, String newPartner) {
        for (String employer : mostPreferredStudent.getPreferences()) {
            if (employer.equals(newPartner)) {
                return true;
            }
            if (employer.equals(currentPartner)) {
                return false;
            }
        }
        return false;
    }
    
    // Removes a match from the matching array
    public void removeMatch(String formerMatchsName) {
        for (String[] element : matching) {
            if (element[0] == null) {
                continue;
            }
            if (element[0].equals(formerMatchsName)) {
                element[1] = null;
                break;
            }
        }
    }
    
    // Implements the Gale-Shapley algorithm to find the correct student and employer matching, adds them to an array and returns the array
    public String[][] match(ArrayList<Employer> employers, ArrayList<Student> students) {
        int N = employers.get(0).getPreferences().size();
        matching = new String[employers.get(0).getPreferences().size()][2];
        while (!allMatchesFound(matching)) {
            for (int i = 0 ; i < N ; i++) {
                Employer currentEmployer = employers.get(i);
                String currentEmployersName = currentEmployer.getName();
                String mostPreferredStudentsName = currentEmployer.getPreferences().get(0);
                Student mostPreferredStudent = findStudent(students,mostPreferredStudentsName);
                if (!mostPreferredStudent.isMatched()) {
                	// If student does not have a match, matches the employer and the student
                    matching[i][0] = currentEmployer.getName();
                    matching[i][1] = mostPreferredStudentsName;
                    currentEmployer.setMatch(mostPreferredStudentsName);
                    mostPreferredStudent.setMatch(currentEmployer.getName());
                } else {
                	// If the student has a match and the current employer iteration is their current match, it goes to the next iteration
                    if (mostPreferredStudent.getMatch().equals(currentEmployersName)) {
                        continue;
                    }
                    String currentMatchsName = mostPreferredStudent.getMatch();
                    if (prefersNew(mostPreferredStudent,currentMatchsName,currentEmployersName)) {
                    	// If the student prefers the new employer, they replace old match and old match replaces them
                        matching[i][0] = currentEmployer.getName();
                        matching[i][1] = mostPreferredStudentsName;
                        currentEmployer.setMatch(mostPreferredStudentsName);
                        mostPreferredStudent.setMatch(currentEmployer.getName());
                        Employer formerMatch = findEmployer(employers,currentMatchsName);
                        formerMatch.setMatch(null);
                        removeMatch(formerMatch.getName());
                        formerMatch.getPreferences().remove(mostPreferredStudentsName);
                    } else {
                    	// If the employer was rejected, the employer removes the student from their preferences
                        currentEmployer.getPreferences().remove(mostPreferredStudentsName);
                    }
                }
            }
        }
        return matching;
    }
    
    // Converts the matching array into a .csv file that gets saved to the current directory
    public void matchingToCSV() {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0 ; i < matching.length ; i++) {
    	   for (int j = 0 ; j < 2 ; j++) {
    		   sb.append(matching[i][j]);
    	      if (j == 0) { sb.append(","); }
    	      }
    	   sb.append("\n");
    	}
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter("matches_java_" + matching.length + "x" + matching.length + ".csv"));
    		writer.write(sb.toString());
        	writer.close();
    	} catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        System.out.println("Please submit the file name for the employers' preferences.");
        Scanner employerInput = new Scanner(System.in);
        String employersPreferences = employerInput.nextLine();
        System.out.println("Please submit the file name for the students' preferences.");
        Scanner studentInput = new Scanner(System.in);
        String studentsPreferences = studentInput.nextLine();
        StableMatching sm = new StableMatching();
        ArrayList<Employer> employers = new ArrayList<Employer>();
        employers = sm.addEmployers(employersPreferences, employers);
        ArrayList<Student> students = new ArrayList<Student>();
        students = sm.addStudents(studentsPreferences, students);
        employerInput.close();
        studentInput.close();
        sm.match(employers,students);
        sm.matchingToCSV();
    }

}
