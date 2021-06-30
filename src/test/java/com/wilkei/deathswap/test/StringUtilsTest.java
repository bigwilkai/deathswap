package com.wilkei.deathswap.test;

import com.wilkei.deathswap.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class StringUtilsTest {

    @Test
    public void stringDistanceTest() {
        int expectedDistance = 4;
        String source = "Wilky Bar";
        String target = "Wilkei Star";

        int distance = StringUtils.stringDistance(source, target);

        System.out.printf("Testing Distance Between \"%s\" and \"%s\" (Expecting %d).\n", source, target, expectedDistance);

        if(distance == expectedDistance) { // Test Passed, All is Good.
            System.out.println("Test Succeeded! Expected Distance is equal to Given Distance.");
        }
        else { // Something went wrong :(
            System.out.printf("Test Failed! Expected Distance to be %d but instead got %d.", expectedDistance, distance);
            Assert.fail();
        }
    }

    @Test
    public void closestMatchTest() {
        String expectedResult = "latte";
        String comparison = "late";
        String[] comparisons = new String[] {"espresso", "latte", "lungo", "americano"}; // Big coffee fan not going to lie.

        String result = StringUtils.closestMatch(comparison, comparisons);
        int distance = StringUtils.stringDistance(comparison, result);

        System.out.printf("Testing Closest Match to \"%s\" in %s (Expecting \"%s\").\n", comparison, Arrays.toString(comparisons), expectedResult);

        if(result.equals(expectedResult)) {
            System.out.printf("Test Succeeded! Result is equal to Expected Result (Distance between them is %d).", distance);
        }
        else {
            System.out.printf("Test Failed! Expected Result to be %s but instead got %s (Distance between them is %d).", expectedResult, result, distance);
            Assert.fail();
        }
    }
}
