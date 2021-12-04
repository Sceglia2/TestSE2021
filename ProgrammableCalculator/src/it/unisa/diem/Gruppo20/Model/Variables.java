package it.unisa.diem.Gruppo20.Model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class allows the Variable managment.
 *
 * @author Team 20
 */
public class Variables {

    private Map<Character, Complex> data;
    private Deque<Map<Character, Complex>> backupsStack;

    public Variables(Map<Character, Complex> data, Deque<Map<Character, Complex>> backupsStack) {
        this.data = data;
        this.backupsStack = backupsStack;
    }

    /**
     * Create an object of this class. An HashMap is used to save Complex
     * numbers corresponding to a letter in the range [a-z]. Instead, an
     * ArrayDeque is used to save and restore the map.
     */
    public Variables() {
        this(new HashMap<>(), new ArrayDeque<>());
    }

    public Map<Character, Complex> getCurrentValues() {
        return data;
    }

    public Deque<Map<Character, Complex>> getBackupsStack() {
        return backupsStack;
    }

    /**
     * This method get the Complex number corresponding to the key c inside the
     * map.
     *
     * @param c Is the key of the map.
     * @return Complex Object corresponding to the key c.
     * @throws RuntimeException if the key is not valid or is not contained
     * inside the map.
     */
    public Complex getVariable(char c) throws RuntimeException {
        Complex value = data.get(checkKey(c));
        if (value == null) {
            throw new RuntimeException("There isn't a Complex number associated to the the key " + c);
        }
        return value;
    }

    /**
     * This method set the Complex number passed as param as value of the
     * variable c.
     *
     * @param c Is the key of the map.
     * @param number Is the Complex Object that we want to insert in the
     * variable c.
     * @throws RuntimeException if the key is not valid.
     */
    public void setVariable(char c, Complex number) throws RuntimeException {
        data.put(checkKey(c), number);
    }

    /**
     * This method sums the Complex number passed as param to the value of the
     * variable c and stores the result of the sum as the value of variable c.
     *
     * @param c Is the key of the map.
     * @param number Is the Complex Object that we want to sum to the variable
     * c.
     * @throws RuntimeException if the key is not valid or is not contained
     * inside the map.
     */
    public void sumVariable(char c, Complex number) throws RuntimeException {
        Complex actual = getVariable(c);
        Complex result = actual.plus(number);
        setVariable(c, result);
    }

    /**
     * This method subtracts the Complex number passed as param to the value of
     * the variable c and stores the result of subtraction as value of the
     * variable c.
     *
     * @param c Is the key of the map.
     * @param number Is the Complex Object that we want to subtract to the
     * variable c.
     * @throws RuntimeException if the key is not valid or is not contained
     * inside the map.
     */
    public void subVariable(char c, Complex number) throws RuntimeException {
        Complex actual = getVariable(c);
        Complex result = actual.minus(number);
        setVariable(c, result);
    }

    /**
     * This method needs to backup the variables in the map data into
     * backupStack.
     *
     * @throws RuntimeException if there aren't elements contained in the map.
     */
    public void backup() throws NoSuchElementException {
        if (data.isEmpty()) {
            throw new NoSuchElementException("There aren't elements to save");
        }
        Map<Character, Complex> map = new HashMap<>();
        map.putAll(data);
        backupsStack.push(map);
    }

    /**
     * This method needs to restore the variables in backupStack into the map
     * data.
     *
     * @throws RuntimeException if there aren't elements contained in the Deque.
     */
    public void restore() throws NoSuchElementException {
        if (backupsStack.isEmpty()) {
            throw new NoSuchElementException("There aren't elements to restore");
        }
        data.clear();
        data.putAll(backupsStack.pop());
    }

    private char checkKey(char c) throws RuntimeException {
        char key = Character.toLowerCase(c);
        if (key < 'a' || key > 'z') {
            throw new RuntimeException("Error generated by an operation on variable with key" + key);
        }
        return key;
    }
}
