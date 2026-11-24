import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a Course entity with validation and formatting.
 */
class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseCode;
    private String courseTitle;
    private int units;

    public Course(String courseCode, String courseTitle, int units) {
        this.courseCode = courseCode.trim().toUpperCase();
        this.courseTitle = courseTitle.trim();
        this.units = units;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public int getUnits() {
        return units;
    }

    @Override
    public String toString() {
        return String.format("%-15s | %-35s | %-5d", courseCode, courseTitle, units);
    }
}

/**
 * Main class managing the Student Course System.
 */
public class CourseManager {
    private static final String FILE_NAME = "courses.dat";
    private final List<Course> courseList;
    private final Scanner scanner;

    public CourseManager() {
        this.courseList = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        CourseManager manager = new CourseManager();
        manager.runMenu();
    }

    /**
     * Main program loop presenting the text-based menu.
     */
    public void runMenu() {
        boolean exit = false;
        System.out.println("=================================================");
        System.out.println("   Welcome to Student Course Management System   ");
        System.out.println("=================================================");

        while (!exit) {
            displayMenu();
            int choice = getValidIntegerInput("Choose an option (1-7): ");
            System.out.println("-------------------------------------------------");

            switch (choice) {
                case 1:
                    addCourse();
                    break;
                case 2:
                    viewAllCourses();
                    break;
                case 3:
                    searchCourse();
                    break;
                case 4:
                    computeTotalUnits();
                    break;
                case 5:
                    saveToFile();
                    break;
                case 6:
                    loadFromFile();
                    break;
                case 7:
                    System.out.println("Exiting program. Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("[Error] Invalid choice! Please select a number between 1 and 7.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n>>> MAIN MENU <<<");
        System.out.println("1. Add Course");
        System.out.println("2. View All Courses");
        System.out.println("3. Search Course by Code");
        System.out.println("4. Compute Total Units");
        System.out.println("5. Save to File");
        System.out.println("6. Load from File");
        System.out.println("7. Exit Program");
    }

    /**
     * Operation 1: Add a new course with input validation.
     */
    private void addCourse() {
        System.out.println("[Adding New Course]");
        
        String code;
        while (true) {
            System.out.print("Enter Course Code (e.g., CSC301): ");
            code = scanner.nextLine().trim().toUpperCase();
            if (code.isEmpty()) {
                System.out.println("[Error] Course code cannot be empty.");
            } else {
                break;
            }
        }

        String title;
        while (true) {
            System.out.print("Enter Course Title: ");
            title = scanner.nextLine().trim();
            if (title.isEmpty()) {
                System.out.println("[Error] Course title cannot be empty.");
            } else {
                break;
            }
        }

        int units = -1;
        while (units < 0 || units > 6) { // Standard university course unit constraints (0 to 6)
            units = getValidIntegerInput("Enter Credit Units (0 - 6): ");
            if (units < 0 || units > 6) {
                System.out.println("[Error] Units must be a reasonable number between 0 and 6.");
            }
        }

        Course newCourse = new Course(code, title, units);
        courseList.add(newCourse);
        System.out.println("\n[Success] Course added successfully!");
    }

    /**
     * Operation 2: Display all registered courses.
     */
    private void viewAllCourses() {
        if (courseList.isEmpty()) {
            System.out.println("[Info] No courses registered yet.");
            return;
        }

        System.out.println(String.format("\n%-15s | %-35s | %-5s", "Course Code", "Course Title", "Units"));
        System.out.println("----------------------------------------------------------------------");
        for (Course course : courseList) {
            System.out.println(course);
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Total Courses: " + courseList.size());
    }

    /**
     * Operation 3: Search course by code.
     * Initiates the recursive search method.
     */
    private void searchCourse() {
        if (courseList.isEmpty()) {
            System.out.println("[Info] No courses available to search. Please add or load courses first.");
            return;
        }

        System.out.print("Enter Course Code to search: ");
        String searchCode = scanner.nextLine().trim().toUpperCase();

        // Calling the recursive search helper starting at index 0
        int index = recursiveSearch(searchCode, 0);

        if (index != -1) {
            System.out.println("\n[Success] Course Found!");
            System.out.println("----------------------------------------------------------------------");
            System.out.println(String.format("%-15s | %-35s | %-5s", "Course Code", "Course Title", "Units"));
            System.out.println("----------------------------------------------------------------------");
            System.out.println(courseList.get(index));
            System.out.println("----------------------------------------------------------------------");
        } else {
            System.out.println("\n[Result] Course " + searchCode + " not found.");
        }
    }

    /**
     * Recursive implementation of the linear search algorithm.
     * Satisfies: "Use of a simple recursive feature."
     */
    private int recursiveSearch(String targetCode, int currentIndex) {
        // Base Case 1: Reached end of list without finding the course
        if (currentIndex >= courseList.size()) {
            return -1;
        }
        // Base Case 2: Target found
        if (courseList.get(currentIndex).getCourseCode().equals(targetCode)) {
            return currentIndex;
        }
        // Recursive Step: Look in the remaining list
        return recursiveSearch(targetCode, currentIndex + 1);
    }

    /**
     * Operation 4: Computes the sum of all credit units.
     */
    private void computeTotalUnits() {
        if (courseList.isEmpty()) {
            System.out.println("[Info] Total Credit Units: 0 (No courses registered)");
            return;
        }

        int totalUnits = 0;
        for (Course course : courseList) {
            totalUnits += course.getUnits();
        }

        System.out.println("[Result] Total registered credit units: " + totalUnits + " Unit(s).");
    }

    /**
     * Operation 5: Saves the current list to disk.
     */
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(courseList);
            System.out.println("[Success] All courses saved to '" + FILE_NAME + "' successfully!");
        } catch (IOException e) {
            System.out.println("[Error] Failed to save courses: " + e.getMessage());
        }
    }

    /**
     * Operation 6: Loads the course list from disk.
     */
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("[Info] No saved course file found. Create and save courses first!");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            List<Course> loadedList = (List<Course>) ois.readObject();
            courseList.clear();
            courseList.addAll(loadedList);
            System.out.println("[Success] " + courseList.size() + " course(s) successfully loaded from '" + FILE_NAME + "'.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Error] Failed to load file: " + e.getMessage());
        }
    }

    /**
     * Helper method: Handles exceptions on user numeric input.
     * Ensures program doesn't crash on invalid input types (e.g. typing text in menu).
     */
    private int getValidIntegerInput(String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = scanner.nextInt();
                scanner.nextLine(); // Clear scanner buffer
                break;
            } catch (InputMismatchException e) {
                System.out.println("[Error] Invalid input type. Please enter a valid number.");
                scanner.nextLine(); // Clear scanner buffer to discard invalid token
            }
        }
        return value;
    }
}
