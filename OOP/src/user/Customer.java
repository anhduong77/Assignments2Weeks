package user;

public class Customer extends User {
    private String userEmail;
    private String userMobile;
    public Customer(String userId, String userName, String userPassword, String userRegisterTime, String userRole, String userEmail, String userMobile) {
        super(userId, userName, userPassword, userRegisterTime, userRole);
        this.userEmail = userEmail;
        this.userMobile = userMobile;
    }

    public Customer() {}

    public void setEmail(String email) {this.userEmail = email;}
    public void setMobile(String mobile) {this.userMobile = mobile;}

    

    @Override
    public String toString() {
        return super.toString().substring(0, super.toString().length() - 1) + ",\"user_email\":\"" + userEmail + "\",\"user_mobile\":\"" + userMobile + "\"}";
    }

    
}
