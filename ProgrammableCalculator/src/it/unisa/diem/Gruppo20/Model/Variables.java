package it.unisa.diem.Gruppo20.Model;

import it.unisa.diem.Gruppo20.Model.Exception.VariableKeyException;
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

    private final Map<Character, Complex> data;
    private final Deque<Map<Character, Complex>> backupsStack;

    /**
     * Creates an object of this class. An HashMap is used to save Complex
     * numbers corresponding to a letter in the range [a-z]. Instead, an
     * ArrayDeque is used to save and restore the map.
     */
    public Variables() {
        data = new HashMap<>();
        backupsStack = new ArrayDeque<>();
    }

    public Map<Character, Complex> getCurrentValues() {
        return data;
    }

    public Deque<Map<Character, Complex>> getBackupsStack() {
        return backupsStack;
    }

    /**
     * Gets the Complex number corresponding to the key c inside the map.
     *
     * @param c Is the key of the map.
     * @return Complex Object corresponding to the key c.
     * @throws VariableKeyException if the key is not valid or is not contained
     * inside the map.
     */
    public Complex getVariable(char c) throws VariableKeyException {
        Complex value = data.get(checkKey(c));
        if (value == null) {
            throw new VariableKeyException("There isn't a Complex number associated to the the key '" + c + "'.");
        }
        return value;
    }

    /**
     * Sets the Complex number passed as param as value of the variable c.
     *
     * @param c Is the key of the map.
     * @param number Is the Complex Object that we want to insert in the
     * variable c.
     */
    public void setVariable(char c, Complex number) {
        data.put(checkKey(c), number);
    }

    /**
     * Sums the Complex number passed as param to the value of the variable c
     * and stores the result of the sum as the value of variable c.
     *
     * @param c Is the key of the variable.
     * @param number Is the Complex Object that we want to sum to the variable
     * c.
     */
    public void sumVariable(char c, Complex number) {
        Complex actual = getVariable(c);
        Complex result = actual.plus(number);
        setVariable(c, result);
    }

    /**
     * Subtracts the Complex number passed as param to the value of the variable
     * c and stores the result of subtraction as value of the variable c.
     *
     * @param c Is the key of the variable.
     * @param number Is the Complex Object that we want to subtract to the
     * variable c.
     */
    public void subVariable(char c, Complex number) {
        Complex actual = getVariable(c);
        Complex result = actual.minus(number);
        setVariable(c, result);
    }

    /**
     * Backups the variables of current map into a backup stack.
     *
     * @throws NoSuchElementException if there aren't variables to be saved.
     */
    public void backup() throws NoSuchElementException {
        if (data.isEmpty()) {
            throw new NoSuchElementException("There aren't elements to save.");
        }
        Map<Character, Complex> map = new HashMap<>();
        map.putAll(data);
        backupsStack.push(map);
    }

    /**
     * Restores the variables with the latest backup contained from the stack,
     * removing it.
     *
     * @throws NoSuchElementException if there aren't elements contained in the
     * backup stack.
     */
    public void restore() throws NoSuchElementException {
        if (backupsStack.isEmpty()) {
            throw new NoSuchElementException("There aren't elements to restore.");
        }
        data.clear();
        data.putAll(backupsStack.pop());
    }

    /**
     * Private method used to check if the param c is valid entry in the map.
     *
     * @param c Is the character to check if is acceptable key for the map.
     * @return Key of the map that is a char in the range [a-z].
     * @throws VariableKeyException if the param c not represent a valid entry.
     */
    private char checkKey(char c) throws VariableKeyException {
        char key = Character.toLowerCase(c);
        if (key < 'a' || key > 'z') {
            throw new VariableKeyException("Error generated by an operation on variable with key '" + key + "'.");
        }
        return key;
    }
}
