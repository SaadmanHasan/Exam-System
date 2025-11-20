package comp3111.examsystem.Classes;

import comp3111.examsystem.Utils.Entity;

/**
 * This class represents a Question entity.
 * It extends the Entity class and includes attributes specific to a question.
 */
public class Question extends Entity {
    private String Question;
    private String option1;
    private String option2;
    private String option3;
    private String type;
    private String answer;
    private String score;
    private String teachername;

    /**
     * Default constructor for creating a Question object.
     */
    public Question() {}

    /**
     * Constructor for creating a Question object with specified attributes.
     *
     * @param id the unique identifier for the question
     * @param Question the text of the question
     * @param option1 the first option for the question
     * @param option2 the second option for the question
     * @param option3 the third option for the question
     * @param type the type of the question
     * @param answer the answer to the question
     * @param score the score for the question
     * @param teachername the name of the teacher who created the question
     */
    public Question(Long id, String Question, String option1, String option2, String option3, String type, String answer, String score, String teachername) {
        super(id);
        this.Question = Question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.type = type;
        this.answer = answer;
        this.score = score;
        this.teachername = teachername;
    }

    /**
     * Gets the text of the question.
     *
     * @return the text of the question
     */
    public String getQuestion() {
        return Question;
    }

    /**
     * Gets the first option for the question.
     *
     * @return the first option
     */
    public String getOption1() {
        return option1;
    }

    /**
     * Gets the second option for the question.
     *
     * @return the second option
     */
    public String getOption2() {
        return option2;
    }

    /**
     * Gets the third option for the question.
     *
     * @return the third option
     */
    public String getOption3() {
        return option3;
    }

    /**
     * Gets the type of the question.
     *
     * @return the type of the question
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the answer to the question.
     *
     * @return the answer to the question
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Gets the score for the question.
     *
     * @return the score for the question
     */
    public String getScore() {
        return score;
    }

    /**
     * Gets the name of the teacher who created the question.
     *
     * @return the name of the teacher
     */
    public String getTeachername() {
        return teachername;
    }

    /**
     * Sets the name of the teacher who created the question.
     *
     * @param teachername the name of the teacher
     */
    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }

    /**
     * Gets the unique identifier of the question.
     *
     * @return the unique identifier of the question
     */
    public Long getID() {
        return this.id;
    }
}