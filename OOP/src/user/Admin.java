package user;


public class Admin extends User {
    public Admin(String userId, String userName, String userPassword, String userRegisterTime, String userRole) {
        super(userId, userName, userPassword, userRegisterTime, userRole);
    }

    public Admin() {}
    @Override
    public String toString() {
        return super.toString().substring(0, super.toString().length() - 10) + "admin\"}";
    }
}