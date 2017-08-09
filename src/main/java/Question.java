import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Date;

public class Question {

    String id;
    User user;
    String content;
    ArrayList<String> keywords;
    ArrayList<String> lastVersions;
    Date date;

    public Question() {
    }

    public Question(DBObject dbObject) {
        DBObject kw = (DBObject) dbObject.get("KeyWords");
        keywords = new ArrayList<String>();
        int i = 0;
        if (kw != null)
            while (kw.get("" + i) != null) {
                keywords.add(kw.get("" + i).toString());
                i++;
            }
        DBObject lv = (DBObject) dbObject.get("lastVersions");
        lastVersions = new ArrayList<String>();
        i = 0;
        if (lv != null)
            while (lv.get("" + i) != null) {
                lastVersions.add(kw.get("" + i).toString());
                i++;
            }
        this.id = dbObject.get("_id").toString();
        this.user = new User();
        this.user.id = dbObject.get("User").toString();
        this.content = dbObject.get("Content").toString();
        this.date = (Date) (dbObject.get("Date"));
    }

    public Question(String id, String user_id, String content, ArrayList keywords, ArrayList lastVersions, Date date) {
        this.id = id;
        this.user = new User();
        this.user.id = user_id;
        this.content = content;
        this.lastVersions = lastVersions;
        this.keywords = keywords;
        this.date = date;
    }

    @Override
    public String toString() {
        String lastVersions = "";
        String keywords = "";
        for (String s : this.lastVersions)
            lastVersions += "pre. " + s + "\n";
        for (String s : this.keywords)
            keywords += "key. " + s + "\n";
        return " ---------- " + "\n" +
                user.id + ":\t" + "(" + date + ")" + "\n" +
                "\"" + content + "\"" + "\n" +
                lastVersions +
                keywords +
                " ---------- ";
    }
}