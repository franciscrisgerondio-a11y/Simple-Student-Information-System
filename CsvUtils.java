import java.io.*;
import java.util.*;

public class CsvUtils {

    private static final String DATA_FOLDER = "data/";
    private static final String STUDENT_FILE = DATA_FOLDER + "students.csv";
    private static final String COLLEGE_FILE = DATA_FOLDER + "colleges.csv";
    private static final String PROGRAM_FILE = DATA_FOLDER + "program.csv";

    private static List<String[]> readFile(String path, boolean skipHeader) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(path);

        if (!file.exists()) {
            createFile(path, null);
            return rows;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {

                if (skipHeader && firstLine) {
                    firstLine = false;
                    continue;
                }

                rows.add(line.split(",", -1));
                firstLine = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return rows;
    }

    private static void writeFile(String path, List<String[]> rows, String header) {

        createFile(path, header);

        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {

            if (header != null) {
                pw.println(header);
            }

            for (String[] row : rows) {
                pw.println(String.join(",", row));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readAll() {
        return readFile(STUDENT_FILE, true);
    }

    public static void writeAll(List<String[]> rows) {

        String header = "ID,First Name,Last Name,Program,Year,Gender";

        writeFile(STUDENT_FILE, rows, header);
    }

    public static List<String[]> readAllColleges() {
        return readFile(COLLEGE_FILE, false);
    }

    public static void writeAllColleges(List<String[]> rows) {
        writeFile(COLLEGE_FILE, rows, null);
    }

    public static List<String[]> readAllPrograms() {
        return readFile(PROGRAM_FILE, false);
    }

    public static void writeAllPrograms(List<String[]> rows) {
        writeFile(PROGRAM_FILE, rows, null);
    }

    private static void createFile(String path, String header) {

        try {

            File file = new File(path);
            file.getParentFile().mkdirs();

            if (!file.exists()) {

                file.createNewFile();

                if (header != null) {
                    try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                        pw.println(header);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}