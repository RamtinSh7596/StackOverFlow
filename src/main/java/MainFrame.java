import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;

public class MainFrame extends JFrame {

    public static final int WIDTH = 1000, HEIGHT = 1000;
    MongoDBJDBC mongoDBJDBC;

    public MainFrame(MongoDBJDBC mongoDBJDBC) {
        this.mongoDBJDBC = mongoDBJDBC;
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void questionSearch(ArrayList<Question> questions) {
        int i = 0;
        for (Question question : questions) {
            QuestionComponent questionComponent = new QuestionComponent(question, mongoDBJDBC.findUserByID(question.user.id));
            questionComponent.setLocation(0, i * QuestionComponent.HEIGHT);
            getContentPane().add(new JButton("Salaam"));
            i++;
        }
        repaint();
    }

    public static void main(String[] args) {
        MainFrame mf = new MainFrame(new MongoDBJDBC());
        mf.setVisible(true);
    }
}
