import com.mongodb.DBObject;

import java.util.Date;

public class Comment {

    int type;
    String user_id;
    String answer_id;
    String question_id;
    String content;
    Date date;

    public static final int ANSWER = 0;
    public static final int QUESTION = 1;
    public static final int STH_ELSE = 2;

    public Comment() {
    }

    public Comment(int type, DBObject dbObject) {
        this(
                type,
                dbObject.get("User").toString(),
                dbObject.get("Answer").toString(),
                dbObject.get("Question").toString(),
                dbObject.get("Content").toString(),
                (Date) dbObject.get("Date")
        );
    }

    public Comment(int type, String user_id, String id, String content, Date date) {
        this(type, user_id, id, id, content, date);
    }

    public Comment(int type, String user_id, String answer_id, String question_id, String content, Date date) {
        if (type == ANSWER) {
            this.user_id = user_id;
            this.answer_id = answer_id;
            this.content = content;
            this.date = date;
        } else if (type == QUESTION) {
            this.user_id = user_id;
            this.question_id = question_id;
            this.content = content;
            this.date = date;
        }
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == ANSWER)
            return "\t\t" + " ---------- " + "\n" +
                    "\t\t" + "replying to " + answer_id + "\n" +
                    "\t\t" + user_id + ":\t" + "(" + date + ")" + "\n" +
                    "\t\t" + "\"" + content + "\"" + "\n" +
                    "\t\t" + " ---------- ";
        else if (type == QUESTION)
            return "\t\t" + " ---------- " + "\n" +
                    "\t\t" + "replying to " + question_id + "\n" +
                    "\t\t" + user_id + ":\t" + "(" + date + ")" + "\n" +
                    "\t\t" + "\"" + content + "\"" + "\n" +
                    "\t\t" + " ---------- ";

        return "";
    }
}