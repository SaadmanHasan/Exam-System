package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Course;
import comp3111.examsystem.Classes.Question;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/**
 * This class is the controller for managing exams.
 * It provides functionalities to add, update, delete, and filter exams and questions.
 */
public class ExamManageController {

    String teacher_name;
    public void setTeacherId(String teacherId) {
        this.teacher_name = teacherId;
    }

    ObservableList<String> publish_list = FXCollections.observableArrayList("Yes", "No");
    ObservableList<String> publish_list_all = FXCollections.observableArrayList("Yes", "No", "All");
    ObservableList<String> type_list = FXCollections.observableArrayList("All","Single", "Multiple");

    List<Question> Curr_questions = new ArrayList<>();

    Database<Quiz> QuizDB = new Database<>(Quiz.class);

    @FXML
    private TableView<Quiz> table1;
    @FXML
    private TableColumn<Question, String> EN_table;
    @FXML
    private TableColumn<Question, String> CID_table;
    @FXML
    private TableColumn<Question, String> ET_table;
    @FXML
    private TableColumn<Question, String> Pub_table;

    @FXML
    private TableView<Question> table2;
    @FXML
    private TableColumn<Question, String> QIE_table;
    @FXML
    private TableColumn<Question, String> type1_table;
    @FXML
    private TableColumn<Question, String> score1_table;

    @FXML
    private TableView<Question> table3;
    @FXML
    private TableColumn<Question, String> Q_table;
    @FXML
    private TableColumn<Question, String> type2_table;
    @FXML
    private TableColumn<Question, String> score2_table;

    @FXML
    private TextField input_exam;
    @FXML
    private TextField input_ques;
    @FXML
    private TextField input_score;
    @FXML
    TextField name_publish;
    @FXML
    TextField time_publish;
    @FXML
    private ComboBox<String> input_type;
    @FXML
    private ComboBox<String> input_publish;
    @FXML
    private ComboBox<String> input_courseid;
    @FXML
    ComboBox<String> course_id;
    @FXML
    ComboBox<String> publish;

    /**
     * Initializes the controller. Displays the current exams and questions in two tables.
     * Sets the values of Course ID and Question Type ComboBoxes.
     */
    @FXML
    private void initialize(){
        Platform.runLater(() -> {
            input_type.setValue("Single");
            input_type.setItems(type_list);
            input_publish.setValue("Yes");
            input_publish.setItems(publish_list_all);
            publish.setValue("Yes");
            publish.setItems(publish_list);

            List<String> Ids = new ArrayList<>();
            Database<Course> courses = new Database<>(Course.class);
            List<Course> all_courses = courses.getAll();
            for(Course course: all_courses){
                Ids.add(course.getCourseID());
            }
            course_id.setItems(FXCollections.observableArrayList(Ids));
            course_id.setValue(Ids.getFirst());
            input_courseid.setItems(FXCollections.observableArrayList(Ids));
            input_courseid.setValue(Ids.getFirst());

            Database<Question> ques_database = new Database<>(Question.class);

            //Get the Question list from the specified teacher
            List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);
            Q_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            type2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));
            score2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));

            table3.setItems(observableQuestions);

            Database<Quiz> quiz_database = new Database<>(Quiz.class);
            List<Quiz> teacher_quizzes = quiz_database.queryByField("teachername", teacher_name);
            ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

            EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
            CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
            ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
            Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

            table1.setItems(observableQuizzes);
        });

    }

    /**
     * Deletes the selected question from the second table
     * If no question is selected, a message is displayed to the user.
     */
    @FXML
    public void delete_from_left(){
        Platform.runLater(()->{
            Question q = table2.getSelectionModel().getSelectedItem();
            if(q == null){
                MsgSender.showMsg("Please Select a Question to Delete");
                return;
            }
            Curr_questions.remove(q);
            if(Curr_questions.isEmpty()){
                table2.getItems().clear();
                return;
            }
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(Curr_questions);
            QIE_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            type1_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));
            score1_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            table2.setItems(observableQuestions);
            return;
        });
    }

    /**
     * Adds the selected question from the third table to the second table
     * If no question is selected, a message is displayed to the user.
     * If the question is already added, a message is displayed to the user.
     * If the question is successfully added, it is displayed in the second table.
     */
    @FXML
    public void add_to_left(){
        Platform.runLater(()->{
            Question q = table3.getSelectionModel().getSelectedItem();
            if(q == null){
                MsgSender.showMsg("Please Select a Question to Add");
                return;
            }
            Long id = q.getID();
            for(Question question: Curr_questions){
                if(Objects.equals(question.getID(), id)){
                    MsgSender.showMsg("Question Already Added");
                    return;
                }
            }
            Curr_questions.add(q);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(Curr_questions);
            QIE_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            type1_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));
            score1_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            table2.setItems(observableQuestions);
            return;
        });
    }

    /**
     * Deletes the selected question from the second table
     * If no question is selected, a message is displayed to the user.
     * If the question is successfully deleted, the second table is updated.
     */
    @FXML
    public void delete(){
        Platform.runLater(()->{
            Quiz q = table1.getSelectionModel().getSelectedItem();
            if(q == null){
                MsgSender.showMsg("Please Select an Exam to Delete");
                return;
            }
            table1.getItems().remove(q);
            Database<Quiz> quiz_database = new Database<>(Quiz.class);
            quiz_database.delByFiled("examName", q.getExamName());
            Database<Course> course_dbase = new Database<>(Course.class);
            List<Course> all_courses = course_dbase.getAll();
            for(Course c: all_courses){
                if(c.getId().equals(q.getID())){
                    c.getQuizes().remove(q.getID().toString());
                    course_dbase.update(c);
                    break;
                }
            }
            MsgSender.showMsg("Exam Deleted Successfully");
        });
    }

    /**
     * Clears the list of questions and the text fields for exam name and exam time
     */
    @FXML
    public void refresh(){
        Platform.runLater(()->{
            table2.getItems().clear();
            Curr_questions.clear();
            name_publish.clear();
            time_publish.clear();
        });
    }

    /**
     * Adds an exam to the database based on the input provided by the user
     * If the exam name or exam time is empty, a message is displayed to the user
     * If the exam already exists, a message is displayed to the user
     * If there are less than 2 questions in the exam, a message is displayed to the user
     * If the exam is successfully added, a message is displayed to the user and the text fields are cleared
     * The first table is updated with the new exam
     */
    @FXML
    public void add(javafx.event.ActionEvent actionEvent) {
        Platform.runLater(()->{
            String exam_name = name_publish.getText();
            String courseid = course_id.getValue();
            String exam_time = time_publish.getText();
            String published = publish.getValue();
            List<Quiz> all_quizzes = QuizDB.queryByField("teachername", teacher_name);
            if(exam_name == null || exam_name.isEmpty() || exam_time == null || exam_time.isEmpty()){
                MsgSender.showMsg("Please Fill in the Exam Name and Exam Time");
                return;
            }
            for(Quiz q: all_quizzes){
                if(q.getExamName().equals(exam_name)){
                    MsgSender.showMsg("Exam Already Exists. Please Choose Another Name or Update the Exam");
                    return;
                }
            }

            if(Curr_questions.isEmpty() || Curr_questions.size() < 2){
                MsgSender.showMsg("Please Add More Questions to the Exam");
                return;
            }
            List<String> quesiton_ids = new ArrayList<>();
            Long total_score = 0L;
            for(Question q: Curr_questions){
                quesiton_ids.add(q.getID().toString());
                total_score += Long.parseLong(q.getScore());
            }
            Long uniqueId = System.currentTimeMillis();
            Quiz new_quiz = new Quiz(uniqueId, null, teacher_name, courseid, total_score.toString(), exam_name, exam_time, null, null, quesiton_ids, published);
            QuizDB.add(new_quiz);
            MsgSender.showMsg("Exam Added Successfully");
            name_publish.clear();
            time_publish.clear();
            table2.getItems().clear();
            Database<Course> course_dbase = new Database<>(Course.class);
            List<Course> all_courses = course_dbase.getAll();
            for(Course c: all_courses){
                if(c.getId().equals(uniqueId)){
                    c.addQuiz(uniqueId.toString());
                    course_dbase.update(c);
                    break;
                }
            }

            List<Quiz> teacher_quizzes = QuizDB.queryByField("teachername", teacher_name);
            ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

            EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
            CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
            ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
            Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

            table1.setItems(observableQuizzes);
            Curr_questions.clear();
        });
    }

    /**
     * Updates the selected exam based on the input provided by the user
     * If the exam name or exam time is empty, a message is displayed to the user
     * If the exam does not exist, a message is displayed to the user
     * If there are less than 2 questions in the exam, a message is displayed to the user
     * If the exam is successfully updated, a message is displayed to the user and the text fields are cleared
     */
    @FXML
    public void update(javafx.event.ActionEvent actionEvent){
        Platform.runLater(()->{
            String exam_name = name_publish.getText();
            String courseid = course_id.getValue();
            String exam_time = time_publish.getText();
            String published = publish.getValue();
            List<Quiz> all_quizzes = QuizDB.queryByField("teachername", teacher_name);
            if(exam_name == null || exam_name.isEmpty() || exam_time == null || exam_time.isEmpty()){
                MsgSender.showMsg("Please Fill in the Exam Name and Exam Time");
                return;
            }
            boolean found = false;
            Long id = null;
            List<String> update_students = null;
            String publish = null;
            for(Quiz q: all_quizzes){
                if(q.getExamName().equals(exam_name) && q.getCourseID().equals(courseid)){
                    found = true;
                    id = q.getID();
                    update_students = q.getStudents();
                    publish = q.getPublished();
                    break;
                }
            }
            if(!found){
                MsgSender.showMsg("Exam Does Not Exist. Please Add the Exam");
                return;
            }
            if(Curr_questions.isEmpty() || Curr_questions.size() < 2){
                MsgSender.showMsg("Please Add More Questions to the Exam");
                return;
            }
            if(publish.equals("Yes")){
                MsgSender.showMsg("Exam Already Published. Please Unpublish the Exam to Update");
                return;
            }

            List<String> quesiton_ids = new ArrayList<>();
            Long total_score = 0L;
            for(Question q: Curr_questions){
                quesiton_ids.add(q.getID().toString());
                total_score += Long.parseLong(q.getScore());
            }

            Quiz new_quiz = new Quiz(id,update_students, teacher_name, courseid, total_score.toString(), exam_name, exam_time, null, null, quesiton_ids, published);
            QuizDB.update(new_quiz);
            MsgSender.showMsg("Exam Updated Successfully");
            name_publish.clear();
            time_publish.clear();
            table2.getItems().clear();
            Database<Quiz>quiz_database = new Database<>(Quiz.class);
            List<Quiz> teacher_quizzes = quiz_database.queryByField("teachername", teacher_name);
            ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

            EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
            CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
            ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
            Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

            table1.setItems(observableQuizzes);

            Curr_questions.clear();
        });
    }

    /**
     * Resets the exam query fields and displays all exams
     */
    @FXML
    public void reset_exam(){
        Platform.runLater(()->{
            Database<Quiz> quiz_database = new Database<>(Quiz.class);
            List<Quiz> all_quizzes = quiz_database.getAll();
            List<Quiz> teacher_quizzes = quiz_database.queryByField("teachername", teacher_name);
            ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

            EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
            CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
            ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
            Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

            table1.setItems(observableQuizzes);
        });
    }

    /**
     * Filters the exams based on the input provided by the user
     */
    @FXML
    public void filter_exam(){
        Platform.runLater(()->{
            String exam = input_exam.getText();
            String cid = input_courseid.getValue();
            String publish = input_publish.getValue();

            if(exam == null || exam.isEmpty()){
                if(publish.equals("All")){
                    Database<Quiz> all_quiz= new Database<>(Quiz.class);
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("courseID");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(cid);
                    List<Quiz> teacher_quizzes = all_quiz.queryByFields(fields, values);
                    ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

                    EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
                    CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
                    ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
                    Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

                    table1.setItems(observableQuizzes);
                }
                else{
                    Database<Quiz> all_quiz= new Database<>(Quiz.class);
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("courseID");
                    fields.add("published");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(cid);
                    values.add(publish);
                    List<Quiz> teacher_quizzes = all_quiz.queryByFields(fields, values);
                    ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

                    EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
                    CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
                    ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
                    Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

                    table1.setItems(observableQuizzes);
                }
            }
            else{
                if(publish.equals("All")){
                    Database<Quiz> all_quiz= new Database<>(Quiz.class);
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("courseID");
                    fields.add("examName");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(cid);
                    values.add(exam);
                    List<Quiz> teacher_quizzes = all_quiz.queryByFields(fields, values);
                    ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

                    EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
                    CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
                    ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
                    Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

                    table1.setItems(observableQuizzes);
                }
                else{
                    Database<Quiz> all_quiz= new Database<>(Quiz.class);
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("courseID");
                    fields.add("published");
                    fields.add("examName");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(cid);
                    values.add(publish);
                    values.add(exam);

                    List<Quiz> teacher_quizzes = all_quiz.queryByFields(fields, values);
                    ObservableList<Quiz> observableQuizzes = FXCollections.observableArrayList(teacher_quizzes);

                    EN_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examName"));
                    CID_table.setCellValueFactory(new PropertyValueFactory<Question,String>("courseID"));
                    ET_table.setCellValueFactory(new PropertyValueFactory<Question,String>("examTime"));
                    Pub_table.setCellValueFactory(new PropertyValueFactory<Question,String>("published"));

                    table1.setItems(observableQuizzes);
                }
            }
        });
    }

    /**
     * Resets the question query fields and displays all questions
     */
    @FXML
    public void reset_ques(){
        Platform.runLater(()->{
            input_ques.clear();
            input_score.clear();

            Database<Question> ques_database = new Database<>(Question.class);
            List<Question> all_questions = ques_database.getAll();

            //Get the Question list from the specified teacher
            List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);

            Q_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            score2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            type2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

            table3.setItems(observableQuestions);
        });
    }

    /**
     * Filters the questions based on the input provided by the user
     */
    @FXML
    public void filter_ques(){
        Platform.runLater(()->{
            String ques = input_ques.getText();
            String score = input_score.getText();
            String type = input_type.getValue();

            // Done
            if ((ques == null || ques.isEmpty()) && (score == null || score.isEmpty())) {
                Database<Question> ques_database = new Database<>(Question.class);
                List<Question> all_questions = ques_database.getAll();
                if(type.equals("All")){
                    List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
                    ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);
                    Q_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                    score2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                    type2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                    table3.setItems(observableQuestions);
                    return;
                }
                else{
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("type");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(type);
                    List<Question> teacher_questions = ques_database.queryByFields(fields, values);

                    ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);

                    Q_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                    score2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                    type2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                    table3.setItems(observableQuestions);
                    return;
                }

            }
            // Done
            else if((ques == null || ques.isEmpty())){
                Database<Question> ques_database = new Database<>(Question.class);
                List<Question> all_questions = ques_database.getAll();
                List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
                if(type.equals("All")){
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("score");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(score);
                    teacher_questions.clear();
                    teacher_questions = ques_database.queryByFields(fields, values);
                }
                else{
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("score");
                    fields.add("type");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(score);
                    values.add(type);
                    teacher_questions.clear();
                    teacher_questions = ques_database.queryByFields(fields, values);
                }
                ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);
                Q_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                score2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                type2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                table3.setItems(observableQuestions);
                return;
            }
            //Done
            else if(score == null || score.isEmpty()){
                Database<Question> ques_database = new Database<>(Question.class);
                List<Question> all_questions = ques_database.getAll();
                List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
                if(type.equals("All")){
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("Question");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(ques);
                    teacher_questions.clear();
                    teacher_questions = ques_database.queryByFields(fields, values);
                }
                else{
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("Question");
                    fields.add("type");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(ques);
                    values.add(type);
                    teacher_questions.clear();
                    teacher_questions = ques_database.queryByFields(fields, values);
                }
                ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);
                Q_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                score2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                type2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                table3.setItems(observableQuestions);
                return;
            }
            // Done
            else{
                Database<Question> ques_database = new Database<>(Question.class);
                List<Question> all_questions = ques_database.getAll();
                List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
                if(type.equals("All")){
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("Question");
                    fields.add("score");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(ques);
                    values.add(score);
                    teacher_questions.clear();
                    teacher_questions = ques_database.queryByFields(fields, values);
                }
                else{
                    List<String> fields = new ArrayList<>();
                    fields.add("teachername");
                    fields.add("Question");
                    fields.add("score");
                    fields.add("type");
                    List<String> values = new ArrayList<>();
                    values.add(teacher_name);
                    values.add(ques);
                    values.add(score);
                    values.add(type);
                    teacher_questions.clear();
                    teacher_questions = ques_database.queryByFields(fields, values);
                }
                ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);
                Q_table.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                score2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                type2_table.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                table3.setItems(observableQuestions);
                return;
            }
        });
    }

}
