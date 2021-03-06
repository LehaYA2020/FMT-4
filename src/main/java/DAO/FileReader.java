package dao;

import dao.exceptions.DAOException;
import dao.exceptions.MessagesConstants;
import models.Course;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;
import static java.nio.file.Paths.get;
import static java.util.stream.Collectors.toList;

public class FileReader {
    private static FileReader instance;
    private File file;

    private FileReader() {
    }

    public static synchronized FileReader getInstance() {
        if (instance == null) {
            instance = new FileReader();
        }
        return instance;
    }

    public DbAccess getAccess(String fileName) throws DAOException {
        Properties properties = new Properties();
        isNull(fileName);
        file = getFileFromResources(fileName);
        checkFile();
        try (InputStream in = Files.newInputStream(get(file.getAbsolutePath()))) {
            properties.load(in);
        } catch (IOException ioException) {
            throw new DAOException(ioException.getMessage(), ioException);
        }
        return new DbAccess(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
    }

    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);

        if (resource == null) {
            throw new IllegalArgumentException(MessagesConstants.FILE_NOT_FOUND_MESSAGE + fileName);
        } else {
            return new File(resource.getFile());
        }
    }

    private List<String> getData() throws DAOException {
        List<String> list;
        try (Stream<String> stream = lines(get(file.getAbsolutePath()))) {
            list = stream.collect(toList());
        } catch (Exception e) {
            throw new DAOException(MessagesConstants.CANNOT_READ_FILE + file.getAbsolutePath(), e);
        }
        return list;
    }

    public String[] getScript(String fileName) throws DAOException {
        isNull(fileName);
        file = getFileFromResources(fileName);
        checkFile();
        return buildScripts(getData());
    }

    public List<String> getNames(String fileName) throws DAOException {
        isNull(fileName);
        file = getFileFromResources(fileName);
        checkFile();
        return getData();
    }

    public List<Course> getCourses(String fileName) throws DAOException {
        isNull(fileName);
        file = getFileFromResources(fileName);
        checkFile();
        List<Course> courses;
        try (Stream<String> stream = lines(get(file.getAbsolutePath()))) {
            courses = stream.map(this::createCourse).collect(toList());
        } catch (Exception e) {
            throw new DAOException(MessagesConstants.CANNOT_READ_FILE + file.getAbsolutePath(), e);
        }
        return courses;
    }

    private void checkFile() throws DAOException {
        checkForExistence();
        checkForEmptiness();
    }

    private void checkForExistence() throws DAOException {
        if (!(file.exists())) {
            throw new DAOException(MessagesConstants.FILE_NOT_FOUND_MESSAGE + file.getAbsolutePath());
        }
    }

    private void checkForEmptiness() throws DAOException {
        if (file.length() == 0) {
            throw new DAOException(MessagesConstants.FILE_IS_EMPTY_MESSAGE + file.getAbsolutePath());
        }
    }

    private Course createCourse(String string) {
        String[] courseString = string.split("_");
        return new Course(courseString[0], courseString[1]);
    }

    private String[] buildScripts(List<String> list) {
        StringBuilder script = new StringBuilder();
        for (String line : list) {
            script.append(line);
        }
        return script.toString().split(";");
    }

    private void isNull(String line) {
        if (line == null) {
            throw new IllegalArgumentException(MessagesConstants.FILE_NAME_NULL);
        }
    }
}
