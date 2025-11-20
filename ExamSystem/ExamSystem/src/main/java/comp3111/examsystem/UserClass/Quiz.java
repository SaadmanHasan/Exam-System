// Aarav
package comp3111.examsystem.UserClass;
import comp3111.examsystem.Classes.Student;
import comp3111.examsystem.Classes.Teacher;
import comp3111.examsystem.Utils.Entity;
import java.util.List;
import javafx.fxml.FXML;


public class Quiz extends Entity{

    private List<Student> students;
    private List<Teacher> teachers;
    private String courseID;
    private Boolean publish;
    //private List<Question> questions;
    private Integer totalScore;
    private String examName;
    private String examTime;
    //Default Constructor
    public Quiz() {}

    public Quiz(List<Student> students, List<Teacher> teachers, String courseID, List<String> questions, Integer totalScore, String examName, String examTime) {
        this.courseID = courseID;
        this.students = students;
        this.teachers = teachers;
        this.examName = examName;
        this.examTime = examTime;
        //this.questions = questions;

        //Maybe we can set this automatically
        this.totalScore = totalScore;
    }


    //Getters and Setters for all attributes
    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }
}
