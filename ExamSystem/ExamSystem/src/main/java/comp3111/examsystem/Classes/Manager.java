package comp3111.examsystem.Classes;

import comp3111.examsystem.Utils.Entity;

public class Manager extends Entity  {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    public Manager() {
        super(0L);
    }
    //Constructor --> setting data members values
    public Manager(Long id, String username, String password, String firstName, String lastName) {
        super(id);
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;

        System.out.println("Manager created");
    }
    public boolean checkEntry(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    public String getUsername() {
        return this.username;
    }
}
