import DAO.Exceptions.DAOException;
import DAO.ScriptExecutor;
import DAO.StudentsRepository;

public class Main {
    public static void main(String[] args) throws DAOException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        //scriptExecutor.executeScript("CreateTables.sql");

        //DataCreator dataCreator = new DataCreator();
        //dataCreator.createTestData();

        StudentsRepository studentsRepository = new StudentsRepository();
        System.out.println(studentsRepository.getStudentById(1).toString());

    }
}
