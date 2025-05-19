package user;
import java.util.ArrayList;
import operation.UserOperation;

public class User {
    private String userId;
    private String userName;
    private String userPassword;
    private String userRegisterTime;
    private String userRole;
    public static ArrayList<String> userNameL = new ArrayList<String>();
    
    public User(String userId, String userName, String userPassword, String userRegisterTime, String userRole) {
        this.userId = userId;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userRole = userRole;
        this.userRegisterTime = userRegisterTime;
      
        userNameL.add(userName);

    }

    public User() {

    }

    public void setName(String name) {this.userName = name; }
    public void setPassword(String password){
        UserOperation instance = UserOperation.getInstance();
        if (instance.validatePassword(password)) this.userPassword = password;
    }

    public String getId() { return userId; }



    @Override
    public String toString() {
        return "{\"user_id\":\"" 
                + userId
                + "\",\"user_name\":\""
                + userName
                + "\",\"user_password\":\""
                + userPassword
                + "\",\"user_register_time\":\""
                + userRegisterTime
                + "\",\"user_role\":\""
                + "customer\"}";
    }
}
