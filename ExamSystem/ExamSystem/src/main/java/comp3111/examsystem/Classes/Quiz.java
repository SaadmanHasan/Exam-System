package comp3111.examsystem.Classes;

import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.Entity;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Quiz extends Entity {

    private List<String> students;
    private String teachername;

    //Assuming CourseID is unique
    private String courseID;

    private List<String> scores;
    private List<String> timeSpent;
    private String totalScore;
    private String examName;
    private String examTime;
    private List<String> questions;
    private String uniqueCourseID_and_examName;
    private String published;

    /**
     * Default constructor for creating a Quiz object.
     */
    public Quiz() {
        this.students = new ArrayList<>();
        this.scores = new ArrayList<>();
        this.timeSpent = new ArrayList<>();
        this.questions = new ArrayList<>();
        //this.uniqueCourseID_and_examName = "";
    }

    /**
     * Constructor for creating a Quiz object with specified attributes.
     *
     * @param id the unique identifier for the quiz
     * @param students the list of students taking the quiz
     * @param teacher the name of the teacher
     * @param courseID the course ID associated with the quiz
     * @param totalScore the total score of the quiz
     * @param examName the name of the exam
     * @param examTime the time of the exam
     * @param scores the list of scores for the quiz
     * @param timeSpent the list of time spent by students on the quiz
     * @param questions the list of questions in the quiz
     * @param publish the publish status of the quiz
     */
    public Quiz(Long id, List<String> students, String teacher, String courseID, String totalScore, String examName, String examTime, List<String> scores, List<String> timeSpent, List<String> questions, String publish) {
        super(id);
        this.students = students != null ? students : new ArrayList<>();
        this.teachername = teacher;
        this.courseID = courseID;
        this.totalScore = totalScore;
        this.examName = examName;
        this.examTime = examTime;
        this.scores = scores != null ? scores : new ArrayList<>();
        this.timeSpent = timeSpent != null ? timeSpent : new ArrayList<>();
        this.questions = questions != null ? questions : new ArrayList<>();
        this.published = publish;
        //Testing
        this.uniqueCourseID_and_examName = this.courseID + " " + getCourseName() + " | " + this.examName;
    }

    //
    //Helper functions
    //

    /**
     * Helper function
     * @param action the action to run on the JavaFX thread
     * @throws InterruptedException if the Platform.runLater does not complete in time
     */
    private void runAndWait(Runnable action) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            action.run();
            latch.countDown();
        });
        if (!latch.await(30, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Platform.runLater did not complete in time");
        }
    }

    /**
     * Retrieves the score of a student for the quiz.
     *
     * @param studentID the ID of the student
     * @return the score of the student as a Long
     * @throws NumberFormatException if the score cannot be parsed as a Long
     */
    public Long getStudentScore(String studentID) {
        int studentIndex = 0;
        //finding index of student in the student list
        for (int i = 0; i < this.students.size(); i++) {
            if (this.students.get(i).equals(studentID)) {
                studentIndex =  i;
            }
        }
        return Long.parseLong(this.scores.get(studentIndex));
    }

    /**
     * Retrieves the time spent by a student on the quiz.
     *
     * @param studentID the ID of the student
     * @return the time spent by the student as a Long
     * @throws NumberFormatException if the time spent cannot be parsed as a Long
     */
    public Long getStudentTimeSpent(String studentID) {
        int studentIndex = 0;
        //finding index of student in the student list
        for (int i = 0; i < this.students.size(); i++) {
            if (this.students.get(i).equals(studentID)) {
                studentIndex =  i;
            }
        }
        return Long.parseLong(this.timeSpent.get(studentIndex));
    }

    /**
     * Retrieves the list of questions associated with this quiz.
     *
     * @return a list of questions in this quiz
     */
    private List<Question> getQuestionsObject() {
        Database<Question> questionDatabase = new Database<>(Question.class);
        List<Question> all_questions = questionDatabase.getAll();
        List<Question> questionInThisQuiz = new ArrayList<>();

        for (Question question: all_questions) {
            if (this.questions.contains(String.valueOf(question.getId()))) {
                questionInThisQuiz.add(question);
            }
        }
        return questionInThisQuiz;
    }

    /**
     * Gets the list of students taking the quiz.
     *
     * @return the list of students
     */
    public List<String> getStudents() {
        return this.students;
    }

    //
    // Getters and Setters for all attributes
    //

    /**
     * Sets the list of students taking the quiz.
     *
     * @param students the list of students in type Long
     */
    public void setStudents(List<String> students) {
        this.students = students;
    }

    /**
     * Gets the name of the teacher.
     *
     * @return the name of the teacher
     */
    public String getTeacher() {
        return this.teachername;
    }

    /**
     * Sets the name of the teacher.
     *
     * @param teacher the name of the teacher
     */
    public void setTeachers(String teacher) {
        this.teachername = teacher;
    }

    /**
     * Gets the course ID associated with the quiz.
     *
     * @return the course ID
     */
    public String getCourseID() {
        return courseID;
    }

    /**
     * Sets the course ID associated with the quiz.
     *
     * @param courseID the course ID
     */
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    /**
     * Gets the list of scores for the quiz.
     *
     * @return the list of scores
     */
    public List<String> getScores() {
        return scores;
    }

    /**
     * Sets the list of scores for the quiz.
     *
     * @param scores the list of scores
     */
    public void setScores(List<String> scores) {
        this.scores = scores;
    }

    /**
     * Gets the list of time spent by students on the quiz.
     *
     * @return the list of time spent
     */
    public List<String> getTimeSpent() {
        return timeSpent;
    }

    /**
     * Sets the list of time spent by students on the quiz.
     *
     * @param timeSpent the list of time spent
     */
    public void setTimeSpent(List<String> timeSpent) {
        this.timeSpent = timeSpent;
    }

    /**
     * Gets the total score of the quiz.
     *
     * @return the total score
     */
    public String getTotalScore() {
        return totalScore;
    }

    /**
     * Sets the total score of the quiz.
     *
     * @param totalScore the total score
     */
    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * Gets the name of the exam.
     *
     * @return the name of the exam
     */
    public String getExamName() {
        return examName;
    }

    /**
     * Sets the name of the exam.
     *
     * @param examName the name of the exam
     */
    public void setExamName(String examName) {
        this.examName = examName;
    }

    /**
     * Gets the time of the exam.
     *
     * @return the time of the exam
     */
    public String getExamTime() {
        return examTime;
    }

    /**
     * Sets the time of the exam.
     *
     * @param examTime the time of the exam
     */
    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    /**
     * Gets the publish status of the quiz.
     *
     * @return the publish status
     */
    public String getPublished() {
        return published;
    }

    /**
     * Sets the publish status of the quiz.
     *
     * @param published the publish status
     */
    public void setPublished(String published) {
        this.published = published;
    }

    /**
     * Retrieves the name of the course associated with this quiz.
     *
     * @return the name of the course, or an empty string if the course is not found
     */
    public String getCourseName() {
        Database<Course> courseDatabase = new Database<>(Course.class);
        List<Course> all_courses = courseDatabase.getAll();
        for (Course course: all_courses) {
            if (course.getCourseID().equals(this.courseID)) {
                return course.getCourseName();
            }
        }
        return "";
    }

    /**
     * Adds a student's exam details to the quiz.
     *
     * @param studentId the ID of the student
     * @param score the score of the student
     * @param timeSpent the time spent by the student on the quiz
     */
    public void takeExam(String studentId, String score, String timeSpent) {

        if (this.scores.size() == 1) {
            this.students = new ArrayList<>();
            this.scores = new ArrayList<>();
            this.timeSpent = new ArrayList<>();
        }

        this.students.add(studentId);
        this.scores.add(score);
        this.timeSpent.add(timeSpent);

        //updating in the database
        Database<Quiz> quizDatabase = new Database<>(Quiz.class);
        quizDatabase.update(this);
    }

    /**
     * Calculates the mean score of the quiz.
     *
     * @return the mean score, upto 2 decimal places
     */
    public double getMean() {

        if (this.scores.isEmpty()) {
            return 0.00;
        }
        double sum = 0;
        for (String score: this.scores) {
            if (score != null) {
                sum += Double.parseDouble(score);
                System.out.println("Inside the loop");
            }
        }
        double mean =  sum / this.scores.size();
        return Double.parseDouble(String.format("%.2f", mean));
    }

    /**
     * Gets the list of questions in the quiz.
     *
     * @return the list of questions
     */
    public List<String> getQuestions() {
        return questions;
    }

    /**
     * Sets the list of questions in the quiz.
     *
     * @param questions the list of questions
     */
    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    /**
     * Gets the unique identifier of the quiz.
     *
     * @return the unique identifier of the quiz
     */
    public Long getID() {
        return this.id;
    }

    /**
     * Gets the unique identifier combining the course ID and exam name.
     *
     * @return the unique identifier combining the course ID and exam name
     */
    public String getUniqueCourseID_and_examName() {
        return uniqueCourseID_and_examName;
    }

    /**
     * Sets the unique identifier combining the course ID and exam name.
     *
     * @param uniqueCourseID_and_examName the unique identifier combining the course ID and exam name
     */
    public void setUniqueCourseID_and_examName(String uniqueCourseID_and_examName) {
        this.uniqueCourseID_and_examName = uniqueCourseID_and_examName;
    }
}