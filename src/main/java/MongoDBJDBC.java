import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoDBJDBC {

    private static final String host = "localhost";
    private static final int port = 27017;

    private DBCollection user;
    private DBCollection question;
    private DBCollection answer;
    private DBCollection comment;

    public MongoDBJDBC() {
        Logger mongoLogger = Logger.getLogger("org.mongodb");
        mongoLogger.setLevel(Level.OFF);
        MongoClient mongoClient = new MongoClient(host, port);
        DB db = mongoClient.getDB("StackOverFlow");
        user = db.getCollection("User");
        question = db.getCollection("Question");
        answer = db.getCollection("Answer");
        comment = db.getCollection("Comment");
    }

    public ArrayList<Question> searchQuestion(String question) {
        String[] token = question.split("\\s");
        Cursor cursor = this.question.find(new BasicDBObject("KeyWords", new BasicDBObject("$in", token)));
        ArrayList<Question> res = new ArrayList<Question>();
        ArrayList<Question> out = new ArrayList<Question>();
        try {
            while (cursor.hasNext()) {
                Question q = new Question(cursor.next());
                res.add(q);
            }
        } finally {
            cursor.close();
        }
        Question new_q;
        while (res.size() > 0) {
            new_q = res.get(0);
            for (int i = 0; i < res.size(); i++)
                if (matchSize(res.get(i), token) > matchSize(new_q, token)) {
                    new_q = res.get(i);
                }
            res.remove(new_q);
            out.add(new_q);
        }
        return out;
    }

    public ArrayList<Question> show(User user) {
        String searchInterests = "";
        for (String s : user.keyWords) {
            searchInterests += s; //s.substring(s.indexOf('"') + 1, s.lastIndexOf('"')) + " ";
        }
        ArrayList<Question> res = searchQuestion(searchInterests);
        Cursor cursor = this.question.find();
        while (cursor.hasNext()) {
            Question q = new Question(cursor.next());
            if (!res.contains(q))
                res.add(q);
        }
        cursor.close();
        return res;
    }

    private int matchSize(Question question, String[] keywords) {
        int size = 0;
        for (String s : question.keywords) {
            for (int i = 0; i < keywords.length; i++) {
                if (keywords[i].equals(s)) {
                    size++;
                    break;
                }
            }
        }
        return size;
    }

    public boolean registerUser(User user) {
        if (this.user.find(new BasicDBObject("Username", user.username)).hasNext() ||
                this.user.find(new BasicDBObject("Email", user.email)).hasNext())
            return false;

        this.user.insert(new BasicDBObject("Name", user.name)
                .append("Username", user.username)
                .append("Pass", user.pass)
                .append("Email", user.email)
                .append("Score", 0)
                .append("isHidden", user.isHidden)
                .append("KeyWords", user.keyWords)
        );
        return true;
    }

    public User getUser(User user) {
        User u = null;
        Cursor c1 = this.user.find(new BasicDBObject("Username", user.username).append("Pass", user.pass));
        Cursor c2 = this.user.find(new BasicDBObject("Email", user.email).append("Pass", user.pass));
        if ((user.email == null && c1.hasNext())) {
            u = new User(c1.next());
        } else if (c2.hasNext()) {
            u = new User(c2.next());
        }
        return u;
    }

    public void askQuestion(Question question, User cUser) { //Kiana
        DBObject user = findUser(cUser);
        this.question.insert(new BasicDBObject("Content", question.content)
                .append("KeyWords", question.keywords)
                .append("Date", question.date)
                .append("User", user.get("_id"))
        );
    }

    public boolean answer(Answer answer, User cUser) {
        DBObject user = findUser(cUser);
        //TODO benazram raveshe khoobi vase peida kardane soal nist!shayad do ta soal bashe ke daghighan matnesh yeki bashe!
        DBObject question = this.question.find(new BasicDBObject("Content", answer.question_id)).next();
        this.answer.insert(new BasicDBObject("Content", answer.content)
                .append("User", user.get("_id"))
                .append("Question", question.get("_id"))
                .append("Date", answer.date)
                .append("Score", answer.score)

        );
        return true;

    }

    public void comment(Comment comment) {
        DBObject answer = this.answer.find(new BasicDBObject("Content", comment.answer_id)).next();
        this.comment.insert(new BasicDBObject("Content", comment.content)
                .append("User", new BasicDBObject("ObjectId", comment.user_id))
                .append("Answer", answer.get("_id"))
                .append("Date", comment.date)
        );

    }

    public DBObject findUser(User user) {
        Cursor cursor = this.user.find(new BasicDBObject("Username", user.username).append("Pass", user.pass));
        if (!cursor.hasNext())
            return null;
        return cursor.next();
    }

    public void deleteUser(User user) {
        this.user.remove(new BasicDBObject("Username", user.username));
    }

    public void chooseBestAnswer(String best) {
        DBObject answer = this.answer.find(new BasicDBObject("Content", best)).next();
        //TODO !!!! hatman bayad check konim ghablan best nashode bashe!
        DBObject user = this.user.find(new BasicDBObject("_id", answer.get("User"))).next();
        int answerScore = Integer.parseInt(answer.get("Score").toString()) + Main.SCORE;
        int userScore = Integer.parseInt(user.get("Score").toString()) + Main.SCORE;
        BasicDBObject updateAnswer = new BasicDBObject("Best Answer", "true")
                .append("Score", answerScore);
        BasicDBObject updateUser = new BasicDBObject("Score", userScore);
        this.answer.update(answer, new BasicDBObject("$set", updateAnswer));
        this.user.update(user, new BasicDBObject("$set", updateUser));
        DBObject question = this.question.find(new BasicDBObject("_id", answer.get("Question"))).next();
        BasicDBObject updateQuestion = new BasicDBObject("Has Best Answer", "true");
        this.question.update(question, new BasicDBObject("$set", updateQuestion));
    }

    public void printQuestionsAskedByUser(User user) {
        Cursor cursor = this.question.find(new BasicDBObject("User", findUser(user).get("_id")));
        try {
            while (cursor.hasNext())
                //TODO etela'ate bishtari ham mitoone bashe!
                System.out.println(cursor.next().get("Content"));

        } finally {
            cursor.close();
        }
    }

    public void printAnswerOfAQuestion(String question) {
        DBObject q = this.question.find(new BasicDBObject("Content", question)).next();
        Cursor cursor = this.answer.find(new BasicDBObject("Question", q.get("_id")));
        try {
            while (cursor.hasNext()) {
                //TODO etela'ate bishtari ham mitoone bashe!
                Answer answer = new Answer(cursor.next());
                System.out.println(answer.toString());
                printCommentOfAnswer(answer.content);
            }
        } finally {
            cursor.close();
        }
    }

    public void printCommentOfQuestion(String question) {
        DBObject q = this.question.find(new BasicDBObject("Content", question)).next();
        Cursor cursor = this.comment.find(new BasicDBObject("Question", q.get("_id")));
        try {
            while (cursor.hasNext())
                //TODO etela'ate bishtari ham mitoone bashe!
                System.out.println(new Comment(Comment.QUESTION, cursor.next()).toString());

        } finally {
            cursor.close();
        }
    }

    public void printCommentOfAnswer(String question) {
        DBObject q = this.answer.find(new BasicDBObject("Content", question)).next();
        Cursor cursor = this.comment.find(new BasicDBObject("Question", q.get("_id")));
        try {
            while (cursor.hasNext())
                //TODO etela'ate bishtari ham mitoone bashe!
                System.out.println(new Comment(Comment.ANSWER, cursor.next()).toString());
        } finally {
            cursor.close();
        }
    }

    public boolean isQuestionHasBest(String question) {
        DBObject q = this.question.find(new BasicDBObject("Content", question)).next();
        if (q.get("Has Best Answer") == null)
            return false;
        else if (q.get("Has Best Answer").toString().equals("true"))
            return true;
        return false;
    }

    public void giveScoreToAnswer(String answer, int score, User u) {
        DBObject actingUser = findUser(u);
        //TODO alan ye user mitoone chanbar ye answer ro like ya unlike kone!
        DBObject a = this.answer.find(new BasicDBObject("Content", answer)).next();
        DBObject user = this.user.find(new BasicDBObject("_id", a.get("User"))).next();
        int newScore = Integer.parseInt(a.get("Score").toString()) + score;
        int newUserScore = Integer.parseInt(user.get("Score").toString()) + score;
        BasicDBObject updateAnswer;
        if (score > 0) {
            Cursor c = this.answer.find(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("LikedByUsers", new BasicDBObject("$elemMatch", new BasicDBObject("_id", actingUser.get("_id")))));
            if (c.hasNext() && c.next().get("LikedByUsers") != null) {
                System.out.println("You liked that answer before!");
                return;
            }
            c = this.answer.find(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("unLikedByUsers", new BasicDBObject("$elemMatch", new BasicDBObject("_id", actingUser.get("_id")))));
            if (c.hasNext() && c.next().get("unLikedByUsers") != null) {
                newScore++;
                newUserScore++;
                this.answer.update(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("$pull", new
                        BasicDBObject("unLikedByUsers", new BasicDBObject("_id", actingUser.get("_id")))));
            }

            updateAnswer = new BasicDBObject("Score", newScore);
            this.answer.update(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("$set", updateAnswer));
            this.answer.update(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("$push", new BasicDBObject("LikedByUsers", new BasicDBObject("_id", actingUser.get("_id")))));

        } else {

            Cursor c = this.answer.find(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("unLikedByUsers", new BasicDBObject("$elemMatch", new BasicDBObject("_id", actingUser.get("_id")))));
            if (c.hasNext() && c.next().get("unLikedByUsers") != null) {
                System.out.println("You unliked that answer before!");
                return;
            }
            c = this.answer.find(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("LikedByUsers", new BasicDBObject("$elemMatch", new BasicDBObject("_id", actingUser.get("_id")))));
            if (c.hasNext() && c.next().get("LikedByUsers") != null) {
                newScore--;
                newUserScore--;
                this.answer.update(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("$pull", new
                        BasicDBObject("LikedByUsers", new BasicDBObject("_id", actingUser.get("_id")))));


            }

            updateAnswer = new BasicDBObject("Score", newScore);
            this.answer.update(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("$set", updateAnswer));
            this.answer.update(new BasicDBObject("_id", a.get("_id")), new BasicDBObject("$push", new BasicDBObject("unLikedByUsers", new BasicDBObject("_id", actingUser.get("_id")))));


        }
        BasicDBObject updateUser = new BasicDBObject("Score", newUserScore);
        this.user.update(user, new BasicDBObject("$set", updateUser));

    }

    public void deleteQuestion(String q, User u) {
        DBObject user = findUser(u);
        DBObject question = this.question.find(new BasicDBObject("Content", q)).next();
        if (!question.get("User").toString().equals(user.get("_id").toString())) {
            System.out.println("You can't delete this question because you didn't ask it!");
            return;
        }
        DBObject answer = this.answer.find(new BasicDBObject("Question", question.get("_id"))).next();
        //TODO chon felean hich commenti add nakardim.
        //TODO chan ta answer

        //  DBObject comment = this.answer.find(new BasicDBObject("Comment", answer.get("_id"))).next();
        this.question.remove(question);
        this.answer.remove(answer);
        // this.comment.remove(comment);
    }


    public boolean editUser(User user, User new_user) {
        if ((user.username != new_user.username && (this.user.find(new BasicDBObject("Username", new_user.username)).hasNext())) ||
                (user.email != new_user.email && (this.user.find(new BasicDBObject("Email", new_user.email)).hasNext())))
            return false;
        DBObject userC = findUser(user);

        this.user.update(userC, new BasicDBObject("Name", new_user.name)
                .append("Username", new_user.username)
                .append("Pass", new_user.pass)
                .append("Email", new_user.email)
                .append("Score", user.score)
                .append("isHidden", new_user.isHidden)
                .append("KeyWords", new_user.keyWords)
        );
        return true;
    }

    //GUI Methods!
    public User findUserByID(String id) {
        ObjectId objectId = new ObjectId(id);
        Cursor cursor = this.user.find(new BasicDBObject("_id", objectId));
        if (!cursor.hasNext())
            return null;
        return new User(cursor.next());
    }
}