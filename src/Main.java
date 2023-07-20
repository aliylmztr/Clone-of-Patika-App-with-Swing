import com.patikadev.helper.Helper;
import com.patikadev.model.User;
import com.patikadev.view.LoginGUI;

public class Main {
    public static void main(String[] args) {

        Helper.setLayout();
        if (User.fetchByUsername("admin") == null) {
            User.add("Administrator", "admin", "admin", "operator");
        }
        LoginGUI loginGUI = new LoginGUI();
    }
}
