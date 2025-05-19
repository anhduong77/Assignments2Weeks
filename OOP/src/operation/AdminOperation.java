package operation;
import java.io.*;
import user.Admin;

public class AdminOperation {
    private static AdminOperation instance = null;
    private AdminOperation() {}
    public static AdminOperation getInstance() {
        if (instance == null) {
            instance = new AdminOperation();
        }
        return instance;
    }


    public void registerAdmin() {
        UserOperation userInstance = UserOperation.getInstance();
        Admin admin = new Admin(userInstance.generateUniqueUserId(), "Admin", "Admin123", "2023-10-01", "admin");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt", true));
            writer.write(admin.toString());
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}
