package view_controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CalculatorTest {

    @Test
    void testAdd() {
        Assertions.assertEquals(2, Calculator.add(1, 1));
        Assertions.assertEquals(0, Calculator.add(0, 0));
        Assertions.assertEquals(-3, Calculator.add(-1, -2));
    }

    @Test
    void testSubtract() {
        Assertions.assertEquals(1, Calculator.subtract(2, 1));
        Assertions.assertEquals(0, Calculator.subtract(0, 0));
        Assertions.assertEquals(-1, Calculator.subtract(-2, -1));
    }

    @Test
    void testMultiply() {
        Assertions.assertEquals(2, Calculator.multiply(1, 2));
        Assertions.assertEquals(0, Calculator.multiply(0, 0));
        Assertions.assertEquals(6, Calculator.multiply(-2, -3));
    }

    @Test
    void testDivide() {
        Assertions.assertEquals(2.0, Calculator.divide(4.0, 2.0), 0.01);
        Assertions.assertEquals(0.0, Calculator.divide(0.0, 4.0), 0.01);
        Assertions.assertThrows(IllegalArgumentException.class, () -> Calculator.divide(4.0, 0.0));
    }
}