package operation;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;
import user.*;
import list_result.*;

public class CustomerOperation {
    private static CustomerOperation instance = null;

    private CustomerOperation() {}

    public static CustomerOperation getInstance() {
        if (instance == null) {
            instance = new CustomerOperation();
        }

        return instance;
    }

    public boolean validateEmail(String userEmail) {

        for (int i = 0; i < userEmail.length(); i++) {
            if (i > 0 && userEmail.charAt(i) == '@') {
                String cSubStr = userEmail.substring(i + 1, userEmail.length());
                for (int j = 0; j < cSubStr.length(); j++) {
                    if (cSubStr.charAt(j) == '.') {
                        if (cSubStr.substring(0, j).length() >= 1 && cSubStr.substring(j + 1).length() >= 1)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean validateMobile(String userMobile) {
        if (! (userMobile.substring(0, 2).equals("03")|| 
            userMobile.substring(0, 2).equals("04")) ||
            userMobile.length() != 10) return false;
        String rsPhoneNum = userMobile.substring(2);
        
        for (int i = 0; i < rsPhoneNum.length(); i++) {
            int ascii = (int) rsPhoneNum.charAt(i);
            if (ascii < 48 || ascii > 57) return false;
        }
        
        return true;
    }

    public boolean registerCustomer(String userName, String userPassword, String userEmail, String userMobile) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-mm-yyyy_HH:mm:ss");
        String userRegisterTime = now.format(formatter);
        UserOperation instance = UserOperation.getInstance();
        String userId = instance.generateUniqueUserId();

        if ( !(instance.validateUsername(userName) && instance.validatePassword(userPassword) && validateEmail(userEmail) && validateMobile(userMobile ) ) )
        {
            return false; 
        }

        Customer customer = new Customer(userId, userName, userPassword, userRegisterTime, "customer", userEmail, userMobile);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt", true))) {
            writer.write(customer.toString());
            writer.newLine();
        } catch (IOException e) {
            
            e.printStackTrace();
        } 
        return true;
    }

    public boolean updateProfile(String attributeName, String value, Customer customerObject) {
        attributeName = attributeName.toLowerCase();
        switch (attributeName) {
            case "name": customerObject.setName(value); break;
            case "password": customerObject.setPassword(value); break;
            case "email": customerObject.setEmail(value); break;
            case "mobile": customerObject.setMobile(value); break;
        
        }
        return true;
    }

    public boolean deleteCustomer(String customerId) {
        boolean state = false;
        File originalFile = new File("data/users.txt");
        File tempFile = new File("data/tempFile.txt");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine = "";
            while (true) {
                currentLine = reader.readLine();
                if (currentLine == null) break;
                JSONObject jsObject = new JSONObject(currentLine);
                String curId = jsObject.getString("user_id");
                if (curId.equals(customerId)) {
                    System.out.println("Yes");
                    continue;

                }
                writer.write(currentLine);
                writer.newLine();

            }
            writer.close();
            reader.close();
            if (originalFile.delete()) {
                state = tempFile.renameTo(originalFile);
            } else {
                System.out.println("Could not delete original file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
    
        return state;
    }

    public CustomerListResult getCustomerList(int pageNumber) {
        CustomerListResult customerList = new CustomerListResult(pageNumber);
        return customerList;
    }

    public void deleteAllCustomer() {
    
        File originalFile = new File("data/users.txt");
        File tempFile = new File("data/temp.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine = "";
            while ( (currentLine = reader.readLine()) != null) {
                JSONObject jsObj = new JSONObject(currentLine);
                String curRole = jsObj.getString("user_role");
                if (curRole.equals("customer")) continue;
                writer.write(currentLine);
            }

            reader.close();
            writer.close();

            if(originalFile.delete()) {
                tempFile.renameTo(originalFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCustomerId(String userName, String userPassword) {
        String customerId = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("data/users.txt"))) {
            String currentLine = "";
            while(true){
                currentLine = reader.readLine();
                if (currentLine == null) break;
                
                JSONObject obj = new JSONObject(currentLine);
                String password = obj.getString("user_password");
                String name = obj.getString("user_name");
                if (name.equals(userName) && password.equals(userPassword)) {
                    customerId = obj.getString("user_id");
                    break;
                }
                
            }
        
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customerId;
    }
}