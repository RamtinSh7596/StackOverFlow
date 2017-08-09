import com.mongodb.DBObject;

import java.util.ArrayList;

public class User {

    String id;
    String name;
    String username;
    String email;
    String pass;
    int score;
    ArrayList<String> keyWords;
    boolean isHidden;

    public User() {
    }

    public User(DBObject dbObject) {
        DBObject kw = (DBObject) dbObject.get("KeyWords");
        keyWords = new ArrayList<String>();
        int i = 0;
        if (kw != null)
            while (kw.get("" + i) != null) {
                keyWords.add(kw.get("" + i).toString());
                i++;
            }
        this.id = dbObject.get("_id").toString();
        this.name = dbObject.get("Name").toString();
        this.username = dbObject.get("Username").toString();
        this.email = dbObject.get("Email").toString();
        this.pass = dbObject.get("Pass").toString();
        this.score = Integer.parseInt(dbObject.get("Score").toString());
        this.isHidden = dbObject.get("isHidden").toString().equals("true");
    }

    public User(String id, String name, String username, String email, String pass, int score, ArrayList keyWords, boolean isHidden) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.pass = pass;
        this.score = score;
        this.keyWords = keyWords;
        this.isHidden = isHidden;
    }
}