package comp3111.examsystem.Classes;

import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course extends Entity {

    //Assuming that if the teacher goes away, then they find a replacement to teach this course
    private String courseID;
    private String courseName;
    private String department;
    private List<String> quizes;

    // Default Constructor
    public Course() {}

    public Course(Long id, String courseID, String courseName, String department, List<String> quizes) {
        super(id);
        this.courseID = courseID;
        this.courseName = courseName;
        this.department = department;
        this.quizes = quizes != null ? quizes : new ArrayList<>();
    }

    // Getters to access private data members
    public String getCourseID() {
        return this.courseID;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String getDepartment() {
        return this.department;
    }

    public Long getId() {
        return this.id;
    }

    public List<String> getQuizes() {
        return this.quizes;
    }

    public double getAverageScore() {
//        return 0;

        if (quizes ==null || quizes.isEmpty()) {
            return 0;
        }
        double sum = 0;
        System.out.println(quizes.size());
        Database<Quiz> quizDatabase = new Database<>(Quiz.class);
        for (String quizId : quizes) {
            Quiz quiz = quizDatabase.queryByKey(quizId.toString());
            if (quiz == null) {
                continue;
            }
            sum += quiz.getMean();
        }
        return sum / quizes.size();

    }

    public void addQuiz(String quizId) {
        if (quizes == null) {
            quizes = new ArrayList<>();
        }
        quizes.add(quizId);
    }
    public Long id() {
        return this.id;
    }

}
