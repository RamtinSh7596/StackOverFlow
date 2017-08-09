import com.mongodb.DBObject;

import java.util.Date;

public class Answer {

    String user_id;
    String question_id;
    String content;
    int score = 0;
    Date date;
    boolean isBest = false;

    public Answer() {
    }

    public Answer(DBObject dbObject) {
        this(
                dbObject.get("User").toString(),
                dbObject.get("Question").toString(),
                dbObject.get("Content").toString(),
                Integer.parseInt(dbObject.get("Score").toString()),
                (Date) (dbObject.get("Date")),
                dbObject.get("Content").toString().equals("true")
        );
    }

    public Answer(String user_id, String question_id, String content, int score, Date date, boolean isBest) {
        this.user_id = user_id;
        this.content = content;
        this.date = date;
        this.question_id = question_id;
        this.score = score;
        this.isBest = isBest;
    }

    @Override
    public String toString() {
        if (isBest)
            return "\t" + " -----***----- " + "\n" +
                    "\t" + user_id + " :" + "(" + date + ") -> " + "'" + score + "'" + "\n" +
                    "\t" + "replying to " + question_id + "\n" +
                    "\t" + "\"" + content + "\"" + "\n" +
                    "\t" + "\n" +
                    "\t" + " -----***----- ";
        return "\t" + " ---------- " + "\n" +
                "\t" + user_id + " :" + "(" + date + ") -> " + "'" + score + "'" + "\n" +
                "\t" + "replying to " + question_id + "\n" +
                "\t" + "\"" + content + "\"" + "\n" +
                "\t" + " ---------- ";
    }

}