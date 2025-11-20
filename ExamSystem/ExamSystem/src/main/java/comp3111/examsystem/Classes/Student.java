package comp3111.examsystem.Classes;

import comp3111.examsystem.Utils.Entity;

public class Student extends Entity {

     private String username;
     private String password;
     private String firstName;
     private String gender;
     private String department;
     private String age;

     //Default Constructor
     public Student() {}


    /**
     * Constructs a new Student with the specified details.
     *
     * @param id the unique identifier of the student
     * @param username the username of the student
     * @param password the password of the student
     * @param firstName the first name of the student
     * @param age the age of the student
     * @param department the department of the student
     * @param gender the gender of the student
     */
     public Student(Long id, String username, String password, String firstName, String age, String department, String gender) {
        super(id);
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.gender = gender;
        this.department = department;
        this.age = age;

        System.out.println("Student" + username + "Created");
    }


    //getters to access private data members. Will be useful for login/registration system

    /**
     * Gets the username of the student.
     *
     * @return the username of the student
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the password of the student.
     *
     * @return the password of the student
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets the first name of the student.
     *
     * @return the first name of the student
     */
    public String getFirstName() {
        return this.firstName;
    }


    /**
     * Gets the gender of the student.
     *
     * @return the gender of the student
     */
    public String getGender() {
        return this.gender;
    }


    /**
     * Gets the department of the student.
     *
     * @return the department of the student
     */
    public String getDepartment() {
        return this.department;
    }

    /**
     * Gets the age of the student.
     *
     * @return the age of the student
     */
    public String getAge() {
        return this.age;
    }


    /**
     * Gets the unique identifier of the student.
     *
     * @return the unique identifier of the student
     */
    public Long id() {
        return this.id;
    }

}
