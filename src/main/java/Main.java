import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {

    static final int SCORE = 10;

    public static void main(String[] args) {
        boolean keepGoing = true;
        boolean isLogin = false;
        User currentUser = null;
        String toDo;
        Scanner scanner = new Scanner(System.in);
        MongoDBJDBC mongoDBJDBC = new MongoDBJDBC();
        MainFrame mf = new MainFrame(mongoDBJDBC);
        mf.setVisible(true);

        while (keepGoing) {
            toDo = scanner.nextLine();
            if (toDo.matches("^[s+S]earch")) {
                System.out.print("Your Question:\t");
                String search = scanner.nextLine();
                ArrayList<Question> res = mongoDBJDBC.searchQuestion(search);
                if (res.size() == 0) {
                    System.out.println("No such question");
                    continue;
                }
                for (Question q : res) {
                    System.out.println(q.toString());
                    mongoDBJDBC.printCommentOfQuestion(q.content);
                    mongoDBJDBC.printAnswerOfAQuestion(q.content);
                }
                mf.questionSearch(res);
            } else if (toDo.matches("^[r+R]egister")) {
                if (isLogin) {
                    System.out.println("You have already registered!");
                    continue;
                }
                User user = new User();
                System.out.print("Name:\t");
                user.name = scanner.nextLine();
                System.out.print("UserName:\t");
                user.username = scanner.nextLine();
                System.out.print("E-Mail:\t");
                user.email = scanner.nextLine();
                if (!user.email.matches("(.*)@(.*)")) {
                    System.err.println("not a valid email!");
                    continue;
                }
                System.out.print("Pass:\t");
                user.pass = scanner.nextLine();
                System.out.print("KeyWords[split: ',']:\t");
                user.keyWords = new ArrayList<String>();
                for (String s : scanner.nextLine().split(",")) {
                    user.keyWords.add(s);
                }
                System.out.print("Hidden?[yes/no]:\t");
                user.isHidden = scanner.nextLine().equals("yes");
                if (mongoDBJDBC.registerUser(user))
                    System.out.println("Successful");
                else
                    System.out.println("Unsuccessful");
            } else if (toDo.matches("^[l+L]ogin")) {
                if (isLogin) {
                    System.out.println("You have already signed in!");
                    continue;
                }
                User user = new User();
                System.out.print("UserName or E-mail:\t");
                user.email = scanner.nextLine();
                if (!user.email.matches("(.*)@(.*)")) {
                    user.username = user.email;
                    user.email = null;
                }
                System.out.print("Pass:\t");
                user.pass = scanner.nextLine();
                currentUser = mongoDBJDBC.getUser(user);
                if (currentUser != null) {
                    isLogin = true;
                    System.out.println("Login successfully");
                } else {
                    System.out.println("Something goes wrong!");
                }
            } else if (toDo.matches("^[l+L]ogout")) {
                if (isLogin) {
                    isLogin = false;
                    System.out.println("You're logged out");
                } else {
                    System.out.println("You're not login..");
                }
            } else if (toDo.matches("^[e+E]xit")) {
                keepGoing = false;
            } else if (toDo.matches("^[a+A]sk")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                System.out.print("Ask your question:\t");
                String question = scanner.nextLine();
                System.out.print("Enter your keywords[split: ',']:\t");
                String kw = scanner.nextLine();
                ArrayList<String> keyWords = new ArrayList<String>();
                for (String s : kw.split(",")) {
                    keyWords.add(s);
                }
                mongoDBJDBC.askQuestion(new Question("", currentUser.id, question, keyWords, null, new Date()), currentUser);
            } else if (toDo.matches("^[a+A]nswer")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                System.out.println("Which question do you want to answer?");
                String questionToAnswer = scanner.nextLine();
                //TODO aya kafiye ke faghat soorat soal ro bedim?
                System.out.println("What is your answer to this question?");
                String answerContent = scanner.nextLine();
                if (mongoDBJDBC.answer(new Answer(currentUser.id, questionToAnswer, answerContent, 0, new Date(), false), currentUser))
                    System.out.println("Your answer is submitted successfully");
                else
                    System.out.println("Something goes wrong!");
            } else if (toDo.matches("^[c+C]omment")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                System.out.print("comment on answers or questions?[q or a]\t");
                int type;
                String typeString = scanner.nextLine();
                if (typeString.matches("[a+A]"))
                    type = Comment.ANSWER;
                else if (typeString.matches("[q+Q]"))
                    type = Comment.QUESTION;
                else
                    type = Comment.STH_ELSE;
                System.out.println("Which answer do you want to comment?");
                String sthToComment = scanner.nextLine();
                System.out.println("What is your comment?");
                String commentContent = scanner.nextLine();
                mongoDBJDBC.comment(new Comment(type, currentUser.id, sthToComment, commentContent, new Date()));
            } else if (toDo.matches("^[C+c]hoose [b+B]est")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                System.out.println("There are questions that you have asked:\t");
                mongoDBJDBC.printQuestionsAskedByUser(currentUser);
                System.out.println("Answers of which question do you want to see?");
                String question = scanner.nextLine();
                mongoDBJDBC.printAnswerOfAQuestion(question);
                System.out.println("Which one was helpful for you? if no one is helpful enter cancel");
                String best = scanner.nextLine();
                if (!best.equals("cancel"))
                    mongoDBJDBC.chooseBestAnswer(best);
                else
                    System.out.println("Canceled");
            } else if (toDo.matches("^[g+G]ive score")) {
                if (isLogin) {
                    System.out.println("Which answer do you want to give score?");
                    String answerContent = scanner.nextLine();
                    System.out.println("Do you like it or not[like or unlike]");
                    String liking = scanner.nextLine();
                    int score;
                    if (liking.equals("like"))
                        score = 1;
                    else
                        score = -1;
                    mongoDBJDBC.giveScoreToAnswer(answerContent, score, currentUser);

                }
            } else if (toDo.matches("^[d+D]elete question")) {
                System.out.println("There are the questions that you can ask:");
                mongoDBJDBC.printQuestionsAskedByUser(currentUser);
                String question = scanner.nextLine();
                mongoDBJDBC.deleteQuestion(question, currentUser);
            } else if (toDo.matches("^[s+S]how")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                ArrayList<Question> res = mongoDBJDBC.show(currentUser);
                if (res.size() == 0) {
                    System.out.println("No relative questions to your hobbies");
                    continue;
                }
                for (Question q : res) {
                    System.out.println(q.toString());
                }
            } else if (toDo.matches("^[s+S]how by category")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                System.out.print("Which category?\t");
                String category = scanner.nextLine();
                ArrayList<Question> res = mongoDBJDBC.searchQuestion(category);
                if (res.size() == 0) {
                    System.out.println("No relative questions to the category");
                    continue;
                }
                for (Question q : res) {
                    System.out.println(q.toString());
                    mongoDBJDBC.printCommentOfQuestion(q.content);
                    mongoDBJDBC.printAnswerOfAQuestion(q.content);
                }
            } else if (toDo.matches("^[e+E]dit profile")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                User user = new User();
                String get;
                System.out.print("Name:\t");
                get = scanner.nextLine();
                user.name = get.matches("[s+S]ame") ? currentUser.name : get;
                System.out.print("UserName:\t");
                get = scanner.nextLine();
                user.username = get.matches("[s+S]ame") ? currentUser.username : get;
                System.out.print("E-Mail:\t");
                get = scanner.nextLine();
                if (!get.matches("(.*)@(.*)") && !get.matches("[s+S]ame")) {
                    System.err.println("not a valid email!");
                    continue;
                }
                user.email = get.matches("[s+S]ame") ? currentUser.email : get;
                System.out.print("Pass:\t");
                get = scanner.nextLine();
                user.pass = get.matches("[s+S]ame") ? currentUser.pass : get;
                System.out.print("KeyWords[split: ',']:\t");
                get = scanner.nextLine();
                if (get.matches("[s+S]ame"))
                    user.keyWords = currentUser.keyWords;
                else {
                    user.keyWords = new ArrayList<String>();
                    for (String s : get.split(",")) {
                        user.keyWords.add(s);
                    }
                }
                System.out.print("Hidden?[yes/no]:\t");
                get = scanner.nextLine();
                user.isHidden = get.matches("[s+S]ame") ? currentUser.isHidden : get.equals("yes");
                if (mongoDBJDBC.editUser(currentUser, user))
                    System.out.println("Successful");
                else
                    System.out.println("Unsuccessful");
            } else if (toDo.matches("^[d+D]elete account")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                mongoDBJDBC.deleteUser(currentUser);
                isLogin = false;
                //TODO delete every thing!
                System.out.println("Done");
            } else if (toDo.matches("^[e+E]dit Comment")) {
                if (!isLogin) {
                    System.out.println("You're not logged in");
                    continue;
                }
                System.out.println("Which one do you want to edit?\t");
                String commentForEdit = scanner.nextLine();
                //TODO complete plz!
            }
        }
    }
}