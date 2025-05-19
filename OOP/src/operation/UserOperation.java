package operation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import user.Admin;
import user.Customer;
import user.User;

import org.json.JSONObject;


public class UserOperation {
    private static UserOperation instance = null;
    private static long id = 1000000000;
    private UserOperation() {}

    public String generateUniqueUserId() {
        String rsId = "u_" + id;
        id += 1;
        return rsId;
        
    }

    public String encryptPassword(String userPassword) {
        String encryptedPassword = ""; 
        for (int i = 0; i < userPassword.length(); i++) {
            Random rand = new Random();
            int[] num = new int[3];
            num[0] = rand.nextInt(48, 58);
            num[1] = rand.nextInt(65, 91);
            num[2] = rand.nextInt(97, 123);
            int idx1 = rand.nextInt(0, 3);
            int idx2 = rand.nextInt(0, 3);
            char c1 = (char) num[idx1];
            char c2 = (char) num[idx2];
            encryptedPassword +=  "" + c1 + c2 + userPassword.substring(i, i + 1);
            
        }
        return "^^" + encryptedPassword + "$$";
    }

    public String decryptPassword(String encryptedPassword) {
        int idx = 4;
        String password = "";
        while (idx < encryptedPassword.length()) {
            password += encryptedPassword.substring(idx, idx + 1);
            idx += 3;
        }
        return password;
    }

    public boolean checkUserNameExist(String userName) {
        ArrayList<String> nameL = User.userNameL;
        for (String name : nameL) {
            if (name.equals(userName)) {
                return true;
            }
        } 
        return false;
    }

    public boolean validateUsername(String userName) {
        if (userName.length() < 5) return false;
        for(int i = 0; i < userName.length(); i++) {
            char c = userName.charAt(i);
            int ascii = (int) c;
            if (!( (ascii >= 65 && ascii <= 90) ||
                    ascii == 95 ||
                    (ascii >= 97 && ascii <= 122)  ))
            {
                return false;
            }
        }
        return true;
    }

    public boolean validatePassword(String userPassword) {
        if (userPassword.length() < 5) return false;
        boolean uCheck = false;
        boolean lCheck = false;
        boolean nCheck = false;
        for (int i = 0; i < userPassword.length(); i++) {
            char c = userPassword.charAt(i);
            int ascii = (int) c;
            if (ascii >= 65 && ascii <= 90) uCheck = true;
            else if (ascii >= 97 && ascii <= 122) lCheck = true;
            else if (ascii >= 48 && ascii <= 57) nCheck = true;
            if (uCheck && lCheck && nCheck) return true;
        }
       
        
        return false;
    }

    public User login(String userName, String userPassword) {
        // ArrayList<String> nameL = User.userNameL;
        User user = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("data/users.txt"))) {
            String currentLine = "";
            while(true){
                currentLine = reader.readLine();
                if (currentLine == null) break;
                
                JSONObject obj = new JSONObject(currentLine);
                String password = obj.getString("user_password");
                String name = obj.getString("user_name");
                if (name.equals(userName) && password.equals(userPassword)) {
                    String userRole = obj.getString("user_role");
                    if (userRole.equals("admin")) {
                        user = new Admin();
                        return user;
                    }

                    else if (userRole.equals("customer")) {
                        
                        User customer = new Customer();
                        return customer;
                    }

                }
                
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return user;
    }

    public void deleteUsers() {
        File file = new File("data/users.txt");
        file.delete();
    }

    public static UserOperation getInstance() {
        if (instance == null) {
            instance = new UserOperation();
        }
        return instance;
    }
}
