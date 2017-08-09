import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class QuestionComponent extends JLabel {

    public static final int WIDTH = 600, HEIGHT = 180;
    public Font nameFont = new Font(Font.SERIF, Font.BOLD, 24);
    public Font usernameFont = new Font(Font.SERIF, Font.BOLD, 20);
    public Font contentFont = new Font(Font.SERIF, Font.BOLD, 30);

    public QuestionComponent(final Question question, User user) {
        setSize(WIDTH, HEIGHT);

        JLabel name = new JLabel(user.name);
        name.setSize(WIDTH / 4, HEIGHT / 6);
        name.setLocation(0, 0);
        name.setFont(nameFont);
        add(name);

        JLabel username = new JLabel(user.username);
        username.setSize(WIDTH / 4, HEIGHT / 6);
        username.setLocation(WIDTH / 4, 0);
        username.setFont(usernameFont);
        add(username);

        JLabel profilePic = new JLabel("!!!");
        profilePic.setSize(WIDTH / 4, 5 * HEIGHT / 6);
        profilePic.setLocation(0, HEIGHT / 6);
        add(profilePic);

        JLabel content = new JLabel(question.content);
        content.setSize(3 * WIDTH / 4, 4 * HEIGHT / 9);
        content.setLocation(WIDTH / 4, HEIGHT / 6);
        content.setFont(contentFont);
        add(content);

        JLabel date = new JLabel(question.date.toString());
        date.setSize(WIDTH / 3, HEIGHT / 6);
        date.setLocation(2 * WIDTH / 3, 0);
        add(date);

        JLabel keywords = new JLabel(keyWords(question.keywords));
        keywords.setSize(WIDTH, HEIGHT / 6);
        keywords.setLocation(0, 5 * HEIGHT / 6);
        add(keywords);
    }

    private String keyWords(ArrayList<String> keywords) {
        if (keywords == null)
            return "";
        String s = "";
        for (String kw : keywords) {
            s += ", " + kw;
        }
        return s.substring(2);
    }
}
