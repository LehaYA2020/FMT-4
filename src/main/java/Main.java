import dao.exceptions.DAOException;
import dao.ScriptExecutor;
import dao.StudentRepository;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws DAOException {
        List<String> s = new ArrayList<>();
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        //scriptExecutor.executeScript("CreateTables.sql");

        //DataCreator dataCreator = new DataCreator();
        //dataCreator.createTestData();

        StudentRepository studentRepository = new StudentRepository();
        System.out.println(studentRepository.getStudentById(1).toString());

    }
}
