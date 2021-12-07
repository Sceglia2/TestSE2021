
import it.unisa.diem.Gruppo20.Model.Calculator;
import it.unisa.diem.Gruppo20.Model.Complex;
import it.unisa.diem.Gruppo20.Model.UserCommand;
import it.unisa.diem.Gruppo20.Model.Operations;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Team 20
 */
public class OperationsTest {

    private Operations userOp;
    private Calculator c;
    private File testFile;

    public OperationsTest() {
    }

    @Before
    public void setUp() {
        c = new Calculator();
        userOp = new Operations(c);
        testFile = new File("media/testFile.txt");
        testFile.deleteOnExit();
    }

    @Test(expected = RuntimeException.class)
    public void testParseOperationsExceptionName() {
        userOp.parseOperations("   <a :  clear  4 8 + ");
    }
    
    @Test(expected = RuntimeException.class)
    public void testParseOperationsExceptionEmptyDef() {
        userOp.parseOperations("   test :    ");
    }

    @Test
    public void testParseOperations() {
        userOp.parseOperations("   test :  clear  4 8 + ");
        assertNotNull(userOp.getOperationsCommand("test")); //check if user operation named test exist

        List<String> result = userOp.getOperationsNames("test"); //get the operations' sequence defined by user
        List<String> expected = List.of("clear", "4", "8", "+"); //expected sequence

        assertEquals(expected.size(), result.size()); //checking if they have same size
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), result.get(i)); //check if operations are equals
        }
    }

    @Test
    public void testGetOperationsCommand() {
        userOp.parseOperations("   test :  clear  4 8 + ");
        assertNotNull(userOp.getOperationsCommand("test")); //check if user operation named test exist

        assertNull(userOp.getOperationsCommand("notincluded"));
    }

    @Test
    public void testGetOperationsNames() {
        userOp.parseOperations("   test :  clear  4 8 + ");
        assertNotNull(userOp.getOperationsNames("test")); //check if user operation named test exist
    }

    @Test
    public void testExecuteOperation() {
        userOp.parseOperations("   test :  clear  4 8 + ");
        userOp.executeOperation("test");
        assertComplexEquals(new Complex(12.0, 0.0), c.getData().element());
    }

    @Test
    public void testExecuteOperationWithAnotherOperation() {
        userOp.parseOperations("   test :  clear  4+10j 5-4j + ");
        userOp.parseOperations("   test1 :  test  -4 + ");

        userOp.executeOperation("test1");

        assertComplexEquals(new Complex(5.0, 6.0), c.getData().element());
    }

    @Test
    public void testUserOperationsNames() {
        userOp.parseOperations("   test :  clear  4 8 + ");
        userOp.parseOperations(" test1 :  clear  4 8 + ");
        Set<String> result = userOp.userOperationsNames();
        Set<String> expected = Set.of("test", "test1");

        assertNotNull(result);
        assertEquals(expected.size(), result.size());

        for (String s : expected) {
            assertTrue(result.contains(s));
        }
    }

    @Test
    public void testRemoveOperation() {
        userOp.parseOperations("   test :  clear  4 8 + ");
        userOp.removeOperations("test");
        assertFalse(userOp.userOperationsNames().contains("test"));
        UserCommand uc = (UserCommand) userOp.getOperationsCommand("test");
        assertNotNull(uc);
        assertFalse(uc.isExecutable());
    }

    @Test
    public void testSaveOnFile() throws IOException {
        String expected_1 = "test_1: + - * / +- sqrt";
        String expected_2 = "test: clear drop dup swap over + - * / +- sqrt";
        userOp.parseOperations("    test_1 :       + -  * / +- sqrt   ");
        userOp.parseOperations("    test :   clear   drop  dup swap over     + -  * / +- sqrt   ");

        testFile.setWritable(true);
        userOp.saveOnFile(testFile);

        String actual = read(testFile);
        String expected = expected_1 + "\n" + expected_2 + "\n";
        assertEquals(expected, actual);
    }

    @Test(expected = IOException.class)
    public void testSaveOnFileException() throws IOException {
        testFile.createNewFile();
        testFile.setReadOnly();
        userOp.saveOnFile(testFile);
    }

    @Test
    public void testLoadFromFile() throws IOException {
        String expected_1 = "test_1: + - * / +- sqrt";
        String expected_2 = "test: clear drop dup swap over + - * / +- sqrt";
        String expected_3 = "test_3: 1+1j sqrt +- >a test_1 <a";
        String expected = expected_1 + "\n" + expected_2 + "\n" + expected_3 + "\n";

        userOp.parseOperations("    test_1 :       + -  * / +- sqrt   ");
        userOp.parseOperations("    test :   clear   drop  dup swap over     + -  * / +- sqrt   ");
        userOp.parseOperations("test_3:  1+1j   sqrt +-  >a   test_1  <a   ");

        write(testFile);
        userOp.loadFromFile(testFile);

        String actual = "";
        for (String i : userOp.userOperationsNames()) {
            actual += userOp.operationToString(i) + "\n";
        }
        assertEquals(expected, actual);
    }

    @Test(expected = IOException.class)
    public void testLoadFromFileException() throws IOException {
        testFile.delete();
        userOp.loadFromFile(testFile);
    }

    private String read(File file) {
        String str = "";
        try (Scanner in = new Scanner(new BufferedReader(new FileReader(file)))) {
            in.useLocale(Locale.US);
            in.useDelimiter("\n+|\n\r");
            while (in.hasNext()) {
                str += in.next() + "\n";
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return str;
    }

    private void write(File file) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            for (String i : userOp.userOperationsNames()) {
                out.write(userOp.operationToString(i) + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void assertComplexEquals(Complex expected, Complex actual) {
        assertEquals(expected.getReal(), actual.getReal(), 0.00000001);
        assertEquals(expected.getImaginary(), actual.getImaginary(), 0.00000001);
    }

}