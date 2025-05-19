import io_interface.IOInterface;
import operation.AdminOperation;
public class Main {
    public static void main(String[] args) {
        AdminOperation adminOperation = AdminOperation.getInstance();
        adminOperation.registerAdmin();
        IOInterface io = IOInterface.getInstance();
        io.mainMenu();
    }
}
