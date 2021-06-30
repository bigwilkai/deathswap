package com.wilkei.deathswap.util;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Utilities related to Strings.
 */
public class StringUtils {

    /**
     * Calculates the Edit/Levenshtein Distance between two Strings.
     * This being the amount of edits (Insertions, Replacements and Deletions) that would be required
     * to make String 1 equal to String 2.
     * @param source The String to compare.
     * @param target The String to compare it to.
     * @return The amount of edits required to make String 1 equal String 2.
     */
    public static int stringDistance(@NotNull String source, @NotNull String target) { // Adapted from a Rosetta Code example (https://rosettacode.org/wiki/Levenshtein_distance#Java)
        if(source.equals(target)) return 0;

        // We don't care about capitalisation because this might make Strings appear far more distant from each other than they should be.
        source = source.toLowerCase(Locale.ROOT);
        target = target.toLowerCase(Locale.ROOT);

        int[] costs = new int[target.length() + 1];

        for(int i = 0; i < costs.length; i++) {
            costs[i] = i;
        }

        for (int j = 1; j <= source.length(); j++) {
            costs[0] = j;
            int nw = j - 1;

            for (int k = 1; k <= target.length(); k++) { // what
                int cj = Math.min(1 + Math.min(costs[k], costs[k - 1]), source.charAt(j - 1) == target.charAt(k - 1) ? nw : nw + 1);
                nw = costs[k];
                costs[k] = cj;
            }
        }

        return costs[target.length()];
    }

    /**
     * Finds the closest match in an array to a given String.
     * @param string The String to match.
     * @param comparisons The Strings to match it to.
     * @return The closest matching String.
     */
    public static String closestMatch(@NotNull String string, @NotNull String[] comparisons) {
        String closestString = comparisons[0];
        int closestDistance = stringDistance(string, closestString);

        String comparison; // The current Comparison.
        int distance; // The distance between the Comparison String and the current Comparison.

        // Start at index 1 because we always set the initial closest match to comparisons[0] (Because this makes everything easier).
        for(int i = 1; i < comparisons.length; i++) {
            comparison = comparisons[i];
            distance = stringDistance(string, comparison);

            if(distance < closestDistance) { // If this String is closer to the Comparison String than the current Closest Match.
                closestString = comparison;
                closestDistance = distance;
            }
        }

        return closestString;
    }

}
