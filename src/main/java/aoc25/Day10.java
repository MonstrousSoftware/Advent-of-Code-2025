package aoc25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day10 {

    boolean debug = false;
    long target;
    long[] buttons;

    public static class Matrix {
        int n;
        int m;
        float[][] values;

        /**
         * n rows, m columns
         */
        public Matrix(int n, int m) {
            this.n = n;
            this.m = m;
            values = new float[n][m];
        }

        public void set(int i, int j, int val) {
            values[i][j] = val;
        }

        public float get(int i, int j) {
            return values[i][j];
        }

        public void swapRows(int y1, int y2) {
            float[] row1 = values[y1];
            float[] row2 = values[y2];
            values[y1]=row2;
            values[y2]=row1;
        }
        public void mulRow(int y, float scalar){
            for(int x = 0; x < m; x++)
                values[y][x] *= scalar;
        }
        /** add row y2 multiplied by scalar to row y */
        public void addScaledRow(int y, int y2, float scalar){
            for(int x = 0; x < m; x++)
                values[y][x] += values[y2][x] * scalar;
        }

        public int firstCol(int y) {
            for (int x = 0; x < m; x++) {
                if (Math.abs(values[y][x]) > 0.001f) {
                    return x;
                }
            }
            return m;   // row is all zeroes
        }

    }


    public Day10() {
        System.out.println("Day 10");
        final long startTime = System.currentTimeMillis();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("data/day10.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        long sum = 0;
        long sum2 = 0;
        int lineNr = 0;
        for(String line : lines ) {
            lineNr++;
            // [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
            String[] words = line.split(" ");

            String maskString = words[0];
            target = 0;
            for (int i = maskString.length() - 2; i > 0; i--) {
                target = target << 1;
                if (maskString.charAt(i) == '#')
                    target += 1;
            }

            buttons = new long[words.length - 2];
            for (int b = 1; b < words.length - 1; b++) {
                String button = words[b].substring(1, words[b].length() - 1);
                String[] bits = button.split(",");
                if(bits.length > 63)
                    throw new RuntimeException("Doesn't fit in a long");
                long bitMask = 0;
                for (String bit : bits) {
                    int n = Integer.parseInt(bit);
                    long bitValue = (int) Math.pow(2.0, n);
                    bitMask += bitValue;
                }
                buttons[b - 1] = bitMask;
            }

            String joltageString = words[words.length - 1];
            String[] joltageValues = joltageString.substring(1, joltageString.length() - 1).split(",");
            int[] joltageTargets = new int[joltageValues.length];
            int i = 0;
            for (String joltageValue : joltageValues) {
                joltageTargets[i] = Integer.parseInt(joltageValue);
                i++;
            }

            // part 1

            for (int numPresses = 1; numPresses < 20; numPresses++) {
                int count = makePress(0L, 0, numPresses);
                if (count < Integer.MAX_VALUE) {
                    sum += count;
                    break;
                }
            }

            // part 2

            if(debug) System.out.println(lineNr + " : "+line);

            mat = findLinearEquations(buttons, joltageTargets);

            if(debug) printMatrix(mat);
            int steps = solve(mat);

            if (debug) {
                System.out.println("Button presses: " + steps);
                System.out.print("combo : ");

                int pressCount = 0;
                for (int j = 0; j < buttons.length; j++) {
                    System.out.print(bestValues[j] + " ");
                    if (bestValues[j] != -1)
                        pressCount += bestValues[j];
                }
                System.out.println("  button presses: " + pressCount);
                testSolution(bestValues, joltageTargets, mat);
            }
            sum2 += steps;
        } // line

        System.out.println("Part 1: " + sum);
        System.out.println("Part 2: " + sum2);

        final long endTime = System.currentTimeMillis();
        System.out.println("\nExecution time : " + (endTime - startTime) + " ms");
    }


    boolean testSolution(int[] vals, int [] targets, Matrix mat ){
        int numBits = targets.length;
        int[] totals = new int[numBits];
        for(int i = 0; i < buttons.length; i++){
            long button = buttons[i];
            int times = vals[i];
            for(int bit = 0; bit <numBits; bit++){
                if(hasBit(button, bit))
                    totals[bit] += times;
            }
        }

        int errors = 0;
        System.out.print("Joltages: ");
        for(int bit = 0; bit < numBits; bit++){
            if(totals[bit] != targets[bit]){
                System.out.print("*** ERR:");
                errors++;
            }
            System.out.print(totals[bit]+" ");
        }
        System.out.println();

        for(int y = 0; y < mat.n; y++){
            float sum = 0;
            for(int x = 0; x < buttons.length; x++){
                sum += mat.get(y, x)*vals[x];
            }
            float b = mat.get(y, mat.m-1);
            if(Math.abs(sum -= b)> 0.001f)
                System.out.println("Error sum != b "+sum+" vs "+b);
        }

        return errors == 0;
    }

    Matrix mat;
    int[] values;
    int[] values2;
    int[] bestValues;
    int[][] indices;
    int[] indexCounts;
    int[][] presets;
    int[] presetCounts;
    int fewestButtons;


    // Ax = b
    // with x is vector if button presses
    // b is target joltages
    // A is composed of bitmask per button

    // Create an augmented matrix with the b vector in the last column
    private Matrix findLinearEquations(long[] buttons, int[] targetJoltages){
        int m = targetJoltages.length;
        int n = buttons.length;
        //int s = Math.max(n,m);
        Matrix mat = new Matrix(m, n+1);
        for(int y = 0; y < m; y++){
            for(int x = 0; x < n; x++){
                int coeff = 0;
                if(hasBit(buttons[x], y))
                    coeff = 1;
                mat.set(y, x, coeff);
//                System.out.print(coeff);
//                System.out.print(" ");

            }
            mat.set(y,n, targetJoltages[y]);
//            System.out.print(" = ");
//            System.out.println(targetJoltages[y]);
        }
        return mat;
    }

    private boolean isZero(float f){
        return Math.abs(f) < 0.0001f;
    }

    List<Integer> freeVars = new ArrayList<>();


    private int solve(Matrix mat){

        for(int y = 0; y < mat.n-1; y++) {
            sortRows(mat);
            int firstCol = mat.firstCol(y);
            if(firstCol == mat.m)   // empty row
                continue;
            for(int y2 = y+1; y2 < mat.n; y2++){
                int col = mat.firstCol(y2);
                if(col == mat.m)
                    continue;
                if(col < firstCol)
                    throw new RuntimeException("Mat not sorted");
                if(col == firstCol){
                    float c0 = mat.get(y, firstCol);
                    float c1 = mat.get(y2, col);
                    float factor = -c1/c0;
                    mat.addScaledRow(y2, y, factor);
                    //printMatrix(mat);
                }
            }
        }

        // normalize first non-zero value per row
        for(int y = 0; y < mat.n; y++) {
            int firstCol = mat.firstCol(y);
            if(firstCol == mat.m)
                continue;
            float c0 = mat.get(y, firstCol);
            float factor = 1f/c0;
            mat.mulRow(y, factor);
        }


        // The matrix is now in row echelon form (also called triangular form)
        //printMatrix(mat);

        for(int y = mat.n-1; y >= 1; y--) {
            int firstCol = mat.firstCol(y);
            if(firstCol == mat.m)
                continue;
            float c0 = mat.get(y, firstCol);
            for(int y2 = y-1; y2 >= 0; y2--){

                    float c1 = mat.get(y2, firstCol);
                    if(!isZero(c1)) {
                        float factor = -c1 / c0;
                        mat.addScaledRow(y2, y, factor);
                        //printMatrix(mat);
                    }
            }
        }
        // The matrix is now in reduced row echelon form
        if (debug) printMatrix(mat);

        // find free variables, i.e. non-pivots
        freeVars.clear();
        for(int y = 0; y < mat.n; y++) {
            int firstCol = mat.firstCol(y);
            if(firstCol == mat.m)
                continue;
            for(int x = firstCol+1; x < mat.m-1; x++) {
                float c0 = mat.get(y,x);
                if(!isZero(c0) && !freeVars.contains(x))
                    freeVars.add(x);
            }
        }

        if(debug) {
            System.out.println("Free vars:");
            for (Integer f : freeVars)
                System.out.println(f);
        }

        // now we should vary the free variables with non-negative integers
        // to find solutions

        fewestButtons = Integer.MAX_VALUE;      // best (lowest) score so far
        values = new int[buttons.length];
        for(int x= 0; x < buttons.length; x++)
            values[x] = -1;
        bestValues = new int[buttons.length];

        indexCounts = new int[mat.n];
        indices = new int[mat.n][buttons.length];
        presetCounts = new int[mat.n];
        presets = new int[mat.n][buttons.length];

        for(Integer f : freeVars)
            values[f] = 1;
        for(int y = 0; y < mat.n; y++) {
            float[] row = mat.values[y];

            int indexCount = 0;
            int presetCount = 0;

            for (int x = 0; x < buttons.length; x++) {

                if (!isZero(row[x])){
                    if(values[x] == -1) {
                        indices[y][indexCount++] = x;
                        values[x] = 1;
                    } else {
                        presets[y][presetCount++] = x;
                    }
                }
            }
            indexCounts[y] = indexCount;
            presetCounts[y] = presetCount;
        }

        values2 = new int[buttons.length];
        int max = 2000;
        if(freeVars.isEmpty())
            max = 1;
        for(int total = 0; total < max; total++){
            if(total > fewestButtons)
                break;
            for(int x= 0; x < buttons.length; x++)
                values2[x] = -1;
            int n = iter(0, total);
        }
        return fewestButtons;
    }

    private int iter(int freeVarsIndex, int maxPresses){

        if(freeVars.isEmpty()){
            for(int x= 0; x < buttons.length; x++)  // make a copy so it can be clobbered up
                values[x] = values2[x];
            boolean ok = testValid(mat, values);
            return 1;
        }
        else if( freeVarsIndex == freeVars.size()-1){
            values2[freeVars.get(freeVarsIndex)] = maxPresses;
//            if(debug) {
//                System.out.print("Combo : ");
//                for(int i = 0; i < freeVars.size(); i++) {
//                    System.out.print(values2[freeVars.get(i)]);
//                    System.out.print(" ");
//                }
//                System.out.println();
//            }
            for(int x= 0; x < buttons.length; x++)  // make a copy so it can be clobbered up
                values[x] = values2[x];
            boolean ok = testValid(mat, values);
            return 1;
        } else {
            int count = 0;
            for (int presses = 0; presses <= maxPresses; presses++) {
                values2[freeVars.get(freeVarsIndex)] = presses;
                count += iter(freeVarsIndex + 1, maxPresses - presses);
            }
            return count;
        }
    }

    private boolean testValid(Matrix mat, int[] values){
        int y = 0;
        float remainder = mat.get(y, mat.m-1);
        for(int f : freeVars){
            remainder -= values[f] * mat.get(y, f);
        }
        if(remainder < -0.01f)
            return false;
        int count = iterate2(y, remainder, 0, indexCounts[y]);
        return count > 0;
    }

    private int iterate2(int y, float remainder, int k, int indexCount){

        if(indexCount == 0){
            if(!isZero(remainder)) {
                System.out.println("** Indexcount is zero with non-zero remainder");
                return 0;
            }
        }
        else {
            int column = indices[y][k];
            float weight = mat.get(y, column);
            int maxPresses = Math.round(remainder / weight);
            float frac = remainder-weight*maxPresses;
            if(Math.abs(frac) > 0.1f) {
                return 0;
                //System.out.println("rounding away "+frac);
            }
            values[column] = maxPresses;
        }

        if(indexCount == 0 || k == indexCount-1){  // last unknown in this equation
                   // must use the remainder for the last unknown of this equation

            if(y < mat.n-1) {   // this was not the last equation?

                // next equation
                float rem = mat.get(y+1, mat.m-1);
                for (int i = 0; i < presetCounts[y + 1]; i++) {
                    float v = values[presets[y + 1][i]] * mat.get(y+1, presets[y + 1][i]);
                    rem -= v;
                }
                if(rem < -0.001f)   // allow for f.p. error
                    return 0;
                int count = iterate2(y+1, rem, 0, indexCounts[y+1]);
                return count;
            }

            if(debug) System.out.print("combo : ");
            int pressCount = 0;
            for(int i = 0; i < buttons.length; i++) {
                if(debug) System.out.print(values[i] + " ");
                if(values[i] != -1)
                    pressCount+=values[i];
            }
            if(debug) System.out.println("  button presses: "+pressCount);
            if(pressCount < fewestButtons) {
                fewestButtons = pressCount;
                for(int i = 0; i < buttons.length; i++)
                    bestValues[i] = values[i];
            }
            return 1;
        }



        // try all possible values for unknown k and resolve recursively
        int column = indices[y][k];
        float weight = mat.get(y, column);
        int maxPresses = Math.round(remainder / weight);
        //System.out.println("rounding "+(remainder/weight)+" to "+maxPresses);

        int count = 0;
        for(int presses = 0; presses <= maxPresses; presses++){
            values[column] = presses;
            count += iterate2(y, remainder - weight*presses, k+1, indexCount);
        }
        return count;
    }

    /** sort rows so that the rows with the left most values are at the top. */
    private void sortRows(Matrix mat){
        int[] firstCol = new int[mat.n];
        for(int y = 0; y < mat.n; y++)
            firstCol[y] = mat.firstCol(y);

        // sort in ascending order
        boolean swap = true;
        while(swap) {
            swap = false;
            for (int y = 0; y < mat.n - 1; y++) {
                if (firstCol[y] > firstCol[y + 1]) {
                    int tmp = firstCol[y + 1];
                    firstCol[y + 1] = firstCol[y];
                    firstCol[y] = tmp;
                    mat.swapRows(y, y+1);
                    swap = true;
                }
            }
        }
//        System.out.print("sorted : ");
//        for (int y = 0; y < mat.n; y++) {
//            System.out.print(firstCol[y]);
//            System.out.print(' ');
//
//        }
//        System.out.println();
//        printMatrix(mat);

    }

    private void printMatrix(Matrix mat){
        for(int y = 0; y < mat.n; y++){
            for(int x = 0; x < mat.m; x++){
                System.out.printf("%8.3f", mat.get(y,x));
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    boolean hasBit(long value, int bit){
        long mask = (long) Math.pow(2,bit);
        return ((value & mask) != 0);
    }


    long[] pressed = new long[100];

    int makePress(long state, int level, int depth){

        for (long button : buttons) {
            pressed[level] = button;
            long newState = state ^ button;
            if (newState == target) {
//                System.out.println("found target: ");
//                for(int i = 0; i <= level; i++){
//                    System.out.print(pressed[i]);
//                    System.out.print(" ");
//                }
//                System.out.println();
                return 1;
            }
            if(depth > 1) {
                int val = makePress(newState, level + 1, depth - 1);
                if (val < Integer.MAX_VALUE)
                    return val + 1;
            }
        }
        return Integer.MAX_VALUE;
    }



}
