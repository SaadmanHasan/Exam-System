package comp3111.examsystem.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations such as reading and writing text files.
 */
public class FileUtil {

    /**
     * Writes the given content to a text file.
     *
     * @param content  the content to write
     * @param fileName the file to write to
     * @return true if the write operation was successful, false otherwise
     * @throws Exception if an I/O error occurs
     */
    public static boolean writeTxtFile(String content, File fileName) throws Exception {
        boolean flag = false;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(content.getBytes("UTF-8"));
            fileOutputStream.close();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * Reads the content of a text file line by line.
     *
     * @param fileName the name of the file to read
     * @return a list of strings, each representing a line in the file
     */
    public static List<String> readFileByLines(String fileName) {
        File file = new File(fileName);
        List<String> list = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                list.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }
}
