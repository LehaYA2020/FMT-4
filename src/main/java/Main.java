import dao.Exceptions.DAOException;
import dao.ScriptExecutor;
import dao.StudentRepository;

public class Main {
    public static void main(String[] args) throws DAOException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        //scriptExecutor.executeScript("CreateTables.sql");

        //DataCreator dataCreator = new DataCreator();
        //dataCreator.createTestData();

        StudentRepository studentRepository = new StudentRepository();
        System.out.println(studentRepository.getStudentById(1).toString());

    }
}
