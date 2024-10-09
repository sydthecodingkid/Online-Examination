import java.util.*;

public class MCQExamDemo {
    private static ExamSystem examSystem = new ExamSystem();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeSystem();

        while (true) {
            System.out.println("\n1. Login");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                if (login()) {
                    userMenu();
                }
            } else if (choice == 2) {
                System.out.println("Goodbye!");
                break;
            }
        }
    }

    private static void initializeSystem() {
        examSystem.addUser(new User("john", "password123", "john@example.com"));
        List<MCQQuestion> questions = new ArrayList<>();
        questions.add(new MCQQuestion("What is 2 + 2?", Arrays.asList("3", "4", "5", "6"), 1));
        questions.add(new MCQQuestion("Who wrote 'Romeo and Juliet'?", Arrays.asList("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"), 1));
        examSystem.setExamQuestions(questions);
    }

    private static boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        return examSystem.login(username, password);
    }

    private static void userMenu() {
        while (true) {
            System.out.println("\n1. Update Profile");
            System.out.println("2. Start Exam");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    updateProfile();
                    break;
                case 2:
                    startExam();
                    break;
                case 3:
                    examSystem.logout();
                    return;
            }
        }
    }

    private static void updateProfile() {
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();
        examSystem.updateUserProfile(newEmail);
        System.out.println("Profile updated successfully!");
    }

    private static void startExam() {
        System.out.println("Starting exam. You have 60 seconds to complete.");
        examSystem.startExam(60);

        List<MCQQuestion> questions = examSystem.getExamQuestions();
        for (int i = 0; i < questions.size(); i++) {
            MCQQuestion question = questions.get(i);
            System.out.println("\nQuestion " + (i + 1) + ": " + question.getQuestion());
            List<String> options = question.getOptions();
            for (int j = 0; j < options.size(); j++) {
                System.out.println((j + 1) + ". " + options.get(j));
            }
            System.out.print("Your answer (1-" + options.size() + "): ");
            int answer = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            examSystem.selectAnswer(i, answer - 1);
        }

        examSystem.submitExam();
        System.out.println("Exam submitted. Your score: " + examSystem.getLastExamScore());
    }
}

// Updated ExamSystem class to support the demo
class ExamSystem {
    private Map<String, User> users = new HashMap<>();
    private User currentUser;
    private Exam currentExam;
    private List<MCQQuestion> examQuestions;

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Login successful. Welcome, " + username + "!");
            return true;
        }
        System.out.println("Login failed. Please try again.");
        return false;
    }

    public void logout() {
        currentUser = null;
        currentExam = null;
        System.out.println("Logged out successfully.");
    }

    public void updateUserProfile(String newEmail) {
        if (currentUser != null) {
            currentUser.updateProfile(newEmail);
        }
    }

    public void setExamQuestions(List<MCQQuestion> questions) {
        this.examQuestions = questions;
    }

    public List<MCQQuestion> getExamQuestions() {
        return examQuestions;
    }

    public void startExam(int durationInSeconds) {
        currentExam = new Exam(examQuestions, durationInSeconds);
        currentExam.startExam();
    }

    public void selectAnswer(int questionIndex, int answerIndex) {
        if (currentExam != null) {
            currentExam.selectAnswer(questionIndex, answerIndex);
        }
    }

    public void submitExam() {
        if (currentExam != null) {
            currentExam.submitExam();
        }
    }

    public int getLastExamScore() {
        return currentExam != null ? currentExam.getScore() : 0;
    }
}

// Updated Exam class to support scoring
class Exam {
    private List<MCQQuestion> questions;
    private List<Integer> userAnswers;
    private Timer timer;
    private int durationInSeconds;
    private int score;

    public Exam(List<MCQQuestion> questions, int durationInSeconds) {
        this.questions = questions;
        this.userAnswers = new ArrayList<>(Collections.nCopies(questions.size(), -1));
        this.durationInSeconds = durationInSeconds;
    }

    public void startExam() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                submitExam();
            }
        }, durationInSeconds * 1000);
    }

    public void selectAnswer(int questionIndex, int answerIndex) {
        userAnswers.set(questionIndex, answerIndex);
    }

    public void submitExam() {
        timer.cancel();
        calculateScore();
    }

    private void calculateScore() {
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers.get(i) == questions.get(i).getCorrectOptionIndex()) {
                score++;
            }
        }
    }

    public int getScore() {
        return score;
    }
}

// Simplified User and MCQQuestion classes for the demo
class User {
    private String username;
    private String password;
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void updateProfile(String newEmail) { }
}

class MCQQuestion {
    private String question;
    private List<String> options;
    private int correctOptionIndex;

    public MCQQuestion(String question, List<String> options, int correctOptionIndex) {
        this.question = question;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestion() { return question; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
}
