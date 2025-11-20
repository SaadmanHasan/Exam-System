package comp3111.examsystem.controller;

import comp3111.examsystem.Classes.Question;
import comp3111.examsystem.Classes.Quiz;
import comp3111.examsystem.Utils.Database;
import comp3111.examsystem.Utils.MsgSender;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


import java.util.ArrayList;
import java.util.List;

public class QuestionBankController {

    String teacher_name;

    Database<Question> QuestionDB = new Database<>(Question.class);
    List<Question> teacher_questions = QuestionDB.queryByField("teachername", teacher_name);

    ObservableList<String> typelist1 = FXCollections.observableArrayList("All","Single", "Multiple");
    ObservableList<String> typelist2 = FXCollections.observableArrayList("Single", "Multiple");

    @FXML
    private ComboBox<String> input_type;
    @FXML
    private ComboBox<String> type;

    @FXML
    TableView<Question> table;
    @FXML
    private TableColumn<Question, String> show_ques;
    @FXML
    private TableColumn<Question, String> show_opa;
    @FXML
    private TableColumn<Question, String> show_opb;
    @FXML
    private TableColumn<Question, String> show_opc;
    @FXML
    private TableColumn<Question, String> show_ans;
    @FXML
    private TableColumn<Question, String> show_type;
    @FXML
    private TableColumn<Question, String> show_score;

    @FXML
    TextField input_question;
    @FXML
    TextField input_score;
    @FXML
    TextField option_a;
    @FXML
    TextField option_b;
    @FXML
    TextField option_c;
    @FXML
    TextField answer;
    @FXML
    TextField score;
    @FXML
    TextField question;

    /**
     * Initializes the Question Bank Controller.
     * Sets the input type and type ComboBoxes with the appropriate values.
     * Retrieves the list of questions from the database and displays them in the table.
     */
    @FXML
    private void initialize(){
        input_type.setValue("Single");
        input_type.setItems(typelist1);

        type.setValue("Single");
        type.setItems(typelist2);

        Database<Question> ques_database = new Database<>(Question.class);
        List<Question> all_questions = ques_database.getAll();

        //Get the Question list from the specified teacher
        List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
        ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);

        show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
        show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
        show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
        show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
        show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
        show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
        show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

        table.setItems(observableQuestions);

    }

    public void setTeacherId(String teacherId) {
        this.teacher_name = teacherId;
    }

    /**
     * Adds a question to the database.
     * Retrieves the question, options, answer, score, and type from the text fields.
     * Creates a new Question object with the specified attributes and adds it to the database.
     * Displays a message to the user indicating that the question has been added successfully.
     * Clears the text fields.
     * If any of the fields are empty, displays a message to the user indicating that all fields must be filled.
     */
    @FXML
    public void add(ActionEvent e){
        Platform.runLater(() -> {
            String ques = question.getText();
            String opa = option_a.getText();
            String opb = option_b.getText();
            String opc = option_c.getText();
            String ans = answer.getText();
            String sco = score.getText();
            String typ = type.getValue();

            if(ques == null || ques.isEmpty() ||opa == null || opa.isEmpty() || opb == null || opb.isEmpty()||
                    ans == null || ans.isEmpty() || sco == null || sco.isEmpty() || typ == null || typ.isEmpty()) {//Question q = new Question(ques, opa, opb, opc, typ, ans, sco, teacher_name);
                MsgSender.showMsg("Please Write information to all Fields");
                return;
            }
            Database<Question> questionlist = new Database<>(Question.class);
            Long uniqueId = System.currentTimeMillis();
            Question q = new Question(uniqueId,ques, opa, opb, opc, typ, ans, sco, teacher_name);
            questionlist.add(q);
            MsgSender.showMsg("Question Added Successfully");
            questionlist = new Database<>(Question.class);
            List<Question> all_questions = questionlist.getAll();
            List<Question> teacher_questions = questionlist.queryByField("teachername", teacher_name);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);

            show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
            show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
            show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
            show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
            show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

            table.setItems(observableQuestions);

            question.clear();
            option_a.clear();
            option_b.clear();
            option_c.clear();
            answer.clear();
            score.clear();
        });
    }

    /**
     * Deletes a question from the database.
     * Retrieves the selected question from the table.
     * If no question is selected, displays a message to the user indicating that a question must be selected.
     * Deletes the question from the database and displays a message to the user indicating that the question has been deleted successfully.
     */
    @FXML
    public void delete(){
        Platform.runLater(() -> {
            Question q = table.getSelectionModel().getSelectedItem();
            if(q == null){
                MsgSender.showMsg("Please Select a Question to Delete");
                return;
            }
            Database<Question> questionlist = new Database<>(Question.class);
            questionlist.delByFiled("Question", q.getQuestion());
            MsgSender.showMsg("Question Deleted Successfully");
            questionlist = new Database<>(Question.class);
            List<Question> all_questions = questionlist.getAll();
            List<Question> teacher_questions = questionlist.queryByField("teachername", teacher_name);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);

            show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
            show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
            show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
            show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
            show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

            table.setItems(observableQuestions);
            return;
        });

    }
    /**
     * Updates a question in the database.
     * Retrieves the selected question from the table.
     * If no question is selected, displays a message to the user indicating that a question must be selected.
     * Retrieves the question, options, answer, score, and type from the text fields.
     * Deletes the selected question from the database.
     * Creates a new Question object with the specified attributes and adds it to the database.
     * Displays a message to the user indicating that the question has been updated successfully.
     * Clears the text fields.
     * If any of the fields are empty, displays a message to the user indicating that all fields must be filled.
     */
    @FXML
    public void update(ActionEvent e){
        Platform.runLater(() -> {;
            Question q = table.getSelectionModel().getSelectedItem();
            if(q == null){
                MsgSender.showMsg("Please Select a Question to Update");
                return;
            }
            Database<Question> questionlist = new Database<>(Question.class);
            Long uniqueID = q.getID();
            questionlist.delByFiled("Question", q.getQuestion());

            String ques = question.getText();
            String opa = option_a.getText();
            String opb = option_b.getText();
            String opc = option_c.getText();
            String ans = answer.getText();
            String sco = score.getText();
            String typ = type.getValue();

            if(ques == null || ques.isEmpty() ||opa == null || opa.isEmpty() || opb == null || opb.isEmpty()||
                    ans == null || ans.isEmpty() || sco == null || sco.isEmpty() || typ == null || typ.isEmpty()) {
                MsgSender.showMsg("Please Write information to all Fields");
                return;
            }
            Question q2 = new Question(uniqueID, ques, opa, opb, opc, typ, ans, sco, teacher_name);
            questionlist.add(q2);
            MsgSender.showMsg("Question Updated Successfully");
            question.clear();
            option_a.clear();
            option_b.clear();
            option_c.clear();
            answer.clear();
            score.clear();

            questionlist = new Database<>(Question.class);
            List<Question> all_questions = questionlist.getAll();
            List<Question> teacher_questions = questionlist.queryByField("teachername", teacher_name);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);

            show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
            show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
            show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
            show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
            show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

            table.setItems(observableQuestions);
            return;
        });
    }

    /**
     * Filters the questions in the table based on the specified criteria.
     * Retrieves the question, score, and type from the text fields.
     * If the question and score fields are empty, retrieves all questions from the database.
     */
    @FXML
    public void filter(){
        Platform.runLater(() -> {
            String ques = input_question.getText();
            String score = input_score.getText();
            String type = input_type.getValue();

            // Done
            if ((ques == null || ques.isEmpty()) && (score == null || score.isEmpty())) {
                Database<Question> ques_database = new Database<>(Question.class);
                List<Question> all_questions = ques_database.getAll();
                if(type.equals("All")){
                    List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
                    ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);
                    show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                    show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
                    show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
                    show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
                    show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
                    show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                    show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                    table.setItems(observableQuestions);
//                return;
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

                    show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                    show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
                    show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
                    show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
                    show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
                    show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                    show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                    table.setItems(observableQuestions);
//                return;
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
                show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
                show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
                show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
                show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
                show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                table.setItems(observableQuestions);
//            return;
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
                show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
                show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
                show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
                show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
                show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                table.setItems(observableQuestions);
//            return;
            }
            // Not Done
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
                show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
                show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
                show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
                show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
                show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
                show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
                show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

                table.setItems(observableQuestions);
//            return;
            }
        });
    }

    /**
     * Resets the text fields and displays all questions in the table.
     * Retrieves all questions from the database and displays them in the table.
     */
    @FXML
    public void reset(){
        Platform.runLater(() -> {
            input_question.clear();
            input_score.clear();

            Database<Question> ques_database = new Database<>(Question.class);
            List<Question> all_questions = ques_database.getAll();

            //Get the Question list from the specified teacher
            List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);
//        System.out.println(all_questions.size());

            show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
            show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
            show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
            show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
            show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

            table.setItems(observableQuestions);
        });
    }

    /**
     * Refreshes the table to display the updated list of questions.
     * Retrieves the list of questions from the database and displays them in the table.
     */
    @FXML
    public void refresh1(){
        Platform.runLater(() ->{
            Database<Question> ques_database = new Database<>(Question.class);
            List<Question> all_questions = ques_database.getAll();

            //Get the Question list from the specified teacher
            List<Question> teacher_questions = ques_database.queryByField("teachername", teacher_name);
            ObservableList<Question> observableQuestions = FXCollections.observableArrayList(teacher_questions);

            show_ques.setCellValueFactory(new PropertyValueFactory<Question,String>("Question"));
            show_opa.setCellValueFactory(new PropertyValueFactory<Question,String>("Option1"));
            show_opb.setCellValueFactory(new PropertyValueFactory<Question,String>("Option2"));
            show_opc.setCellValueFactory(new PropertyValueFactory<Question,String>("Option3"));
            show_ans.setCellValueFactory(new PropertyValueFactory<Question,String>("answer"));
            show_score.setCellValueFactory(new PropertyValueFactory<Question,String>("score"));
            show_type.setCellValueFactory(new PropertyValueFactory<Question,String>("type"));

            table.setItems(observableQuestions);
            input_question.clear();
            input_score.clear();
        });
    }
}
