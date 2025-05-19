package io_interface;
import java.util.*;

import list_result.*;
import operation.*;
import user.*;

public class IOInterface {
    private static IOInterface instance = null;
    private IOInterface() {}
    public static IOInterface getInstance() {
        if (instance == null) {
            instance = new IOInterface();
        }
        return instance;
    }

    @SuppressWarnings("resource")
    public String[] getUserInput(String message, int numOfArgs) {
        String[] args = new String[numOfArgs];
        System.out.print(message);
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < numOfArgs; i++) {
            args[i] = scanner.next();
        }
        return args;
    }

    public void mainMenu() {
        String messages = """
                =======Main Menu=======
                1. Login
                2. Register
                3. Exit
                =========================
                """;
        printMessage(messages);
        String[] input = getUserInput("Enter your choice: ", 1);
        int choice = Integer.parseInt(input[0]);
        switch (choice) {
            case 1:
                String[] loginInput = getUserInput("Enter your username and password: ", 2);
                UserOperation userInstance = UserOperation.getInstance();
                User user = userInstance.login(loginInput[0], loginInput
                [1]);
                if (user == null) {
                    printErrorMessage("mainMenu", "Login failed. Please try again.");
                    break;
                }
                else if (user instanceof Customer) {
                    getUserInput("Login successful. Welcome, Customer", 0);
                
                }
                else if (user instanceof Admin) {
                    printMessage("Login successful. Welcome, Admin");
                    adminMenu();
                }
            case 2:
                printMessage("Registering...");
                break;
            case 3:
                printMessage("Exiting...");
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
        
    }

    public void adminMenu() {
        String messanges = """
                =======Admin Menu=======
                1. Show products
                2. Add customers
                3. Show customers
                4. Show orders
                5. Generate test data
                6. Generate all statistical figures
                7. Delete all data
                8. Logout
                =========================
                """;
        printMessage(messanges);
        String[] input = getUserInput("Enter your choice: ", 1);
        int choice = Integer.parseInt(input[0]);

        // Create instances of the operations
        ProductOperation productInstance = ProductOperation.getInstance();
        OrderOperation orderInstance = OrderOperation.getInstance();
        CustomerOperation customerInstance = CustomerOperation.getInstance();
        UserOperation userInstance = UserOperation.getInstance();
    
        
        if  (choice == 1) {
            ProductListResult productList = productInstance.getProductList(0);
            ArrayList<ArrayList<String>> productPages = productList.getAllPages();
            showList("Admin", "Product", productPages, 1, productPages.size());
            adminMenu();
           
        } 
        else if (choice == 2){
            String[] customerInput = getUserInput("Enter customer name, password, email, and mobile: ", 4);
            boolean isRegisted = customerInstance.registerCustomer(customerInput[0], customerInput[1], customerInput[2], customerInput[3]);
            if (isRegisted) {
                printMessage("Customer registration successful.");
                adminMenu();
            }
            else {
                printErrorMessage("adminMenu", "Customer registration failed. Please try again.");
            }
        }
        else if (choice == 3) {
            
            CustomerListResult customerList = customerInstance.getCustomerList(0);
            ArrayList<ArrayList<String>> customerPages = customerList.getAllPages();
            showList("Admin", "Customer", customerPages, 1, customerPages.size());
            adminMenu();
        } 
        else if (choice == 4) {
            OrderListResult orderList = orderInstance.getOrderList(0);
            ArrayList<ArrayList<String>> orderPages = orderList.getAllPages();
            showList("Admin", "Order", orderPages, 1, orderPages.size());
            adminMenu();
        }

        else if (choice == 5) {
            System.out.println("Generating test data...");
            orderInstance.generateTestOrderData();
            System.out.println("Test data generated successfully.");
            adminMenu();
        }
        else if (choice == 6) {
            System.out.println("Generating all statistical figures...");
            productInstance.generateDiscountFigure();
            productInstance.generateLikesCountFigure();
            productInstance.generateDiscountLikesCountFigure();
            productInstance.generateCategoryFigure();

            orderInstance.generateAllCustomerConsumptionFigure();
            orderInstance.generateAllTop10BestSellersFigure();
            
            System.out.println("All statistical figures generated successfully.");
            adminMenu();
        }
        else if (choice == 7) {
            System.out.println("Deleting all data...");
            productInstance.deleteAllProducts();
            customerInstance.deleteAllCustomer();
            orderInstance.deleteAllOrders();
            userInstance.deleteUsers();
            orderInstance.deleteAllFigures();
            System.out.println("All data deleted successfully.");
            adminMenu();
        }
        else if (choice == 8) {
            System.out.println("Logging out...");
            mainMenu();
        } else {
            printErrorMessage("adminMenu", "Invalid choice. Please try again.");
            mainMenu();
        }
           
    }

    public void showList(String userRole, String listType, ArrayList<ArrayList<String>> list, int page, int totalPages) {
        page = page - 1;
        
        
        while (true) {
            System.out.println("======= "+ listType +" List (Page " + (page + 1) + "/" + totalPages +")=======");
            ArrayList<String> curPage = list.get(page);
            ListIterator<String> iter = curPage.listIterator();
            while (iter.hasNext()) {
                System.out.println(iter.nextIndex() + 1 + ": " + iter.next());
                
            }
             String[] nextInput = getUserInput("Enter 'n' for next page, 'p' for previous page, or 'b' to go back: ", 1);
                if (nextInput[0].equals("n")) {
                    if (page == list.size() - 1) {
                        System.out.println("This is the last page.");
                        continue;
                    }
                    page++;
                } else if (nextInput[0].equals("p")) {
                    if (page == 0) {
                        System.out.println("This is the first page.");
                        continue;
                    }
                    page--;
                } else if (nextInput[0].equals("b")) {
                    break;
                
                } else {
                    System.out.println("Invalid input. Please try again.");
                    break;
                }
        }
        
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printErrorMessage(String errSource, String message) {
        System.err.println("Error in " + errSource + ": " + message);
        
    }

}
