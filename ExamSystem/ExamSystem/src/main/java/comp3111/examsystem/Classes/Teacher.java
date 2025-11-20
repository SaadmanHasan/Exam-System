package comp3111.examsystem.Classes;

import comp3111.examsystem.Utils.Entity;

/**
 * This class represents a Teacher entity.
 * It extends the Entity class and includes additional attributes specific to a teacher.
 */
public class Teacher extends Entity {

    private String username;
    private String password;
    private String firstName;
    private String gender;
    private String department;
    private String age;
    private String position;

    /**
     * Default constructor for creating a Teacher object.
     */
    public Teacher() {}

    /**
     * Constructor for creating a Teacher object with specified attributes.
     *
     * @param id the unique identifier for the teacher
     * @param username the username of the teacher
     * @param password the password of the teacher
     * @param firstName the first name of the teacher
     * @param age the age of the teacher
     * @param department the department of the teacher
     * @param gender the gender of the teacher
     * @param position the position of the teacher
     */
    public Teacher(Long id, String username, String password, String firstName, String age, String department, String gender, String position) {
        super(id);
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.gender = gender;
        this.department = department;
        this.age = age;
        this.position = position;
        System.out.println("Teacher created");
    }

    /**
     * Gets the username of the teacher.
     *
     * @return the username of the teacher
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the password of the teacher.
     *
     * @return the password of the teacher
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets the first name of the teacher.
     *
     * @return the first name of the teacher
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Gets the gender of the teacher.
     *
     * @return the gender of the teacher
     */
    public String getGender() {
        return this.gender;
    }

    /**
     * Gets the department of the teacher.
     *
     * @return the department of the teacher
     */
    public String getDepartment() {
        return this.department;
    }

    /**
     * Gets the age of the teacher.
     *
     * @return the age of the teacher
     */
    public String getAge() {
        return this.age;
    }

    /**
     * Gets the position of the teacher.
     *
     * @return the position of the teacher
     */
    public String getPosition() {
        return this.position;
    }

    /**
     * Gets the unique identifier of the teacher.
     *
     * @return the unique identifier of the teacher
     */
    public Long id() {
        return this.id;
    }
}
