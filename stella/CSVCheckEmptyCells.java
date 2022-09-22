/*
 * This program generates a file, missingFields.txt, that lists out the missing fields in each row, and outputs total
 * counts at the end. The CSV file to be read can be changed in the BufferedReader line.
 */

import java.io.*;

public class CSVCheckEmptyCells {

    public static void main(String[] args) throws Exception {
        // can change CSV file path
        BufferedReader br = new BufferedReader(new FileReader(new File("All_TF_Cases_July_8_2022_NO_PHI.xlsx - All Specialties - NO PHI.csv")));
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("missingFields.txt")));

        String[] fieldNames = br.readLine().split(",");
        int numFields = fieldNames.length;
        int[] totalCounts = new int[numFields]; // keeps track of total missing fields
        String input = br.readLine();
        int rowCount = 2;
        while (input != null) { // while there are still lines in the CSV
            String output = "";
            int fieldIndex = 0; // should be always < numFields
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == ',') {
                    fieldIndex++; // counting number of fields, separated based on comma
                    if (i == 0) {
                        output += fieldNames[0] + ", ";
                        totalCounts[0]++;
                    }
                    // prints field name if it is missing in the row
                    if ((fieldIndex < numFields - 1 && input.indexOf(',', i + 1) - i == 1) || i == input.length() - 1) {
                        output += fieldNames[fieldIndex] + ", ";
                        totalCounts[fieldIndex]++;
                    }
                } else if (input.charAt(i) == '\"') { // tracking quotes to indicate commas within a cell that should be ignored
                    i = input.indexOf('\"', i+1); // fast forward to end of cell by searching for ending quotation
                }
            }
            // print out current row's missing fields
            if (!output.isEmpty()) {
                out.println("Row " + rowCount + ": " + output.substring(0, output.length()-2));
            }
            input = br.readLine();
            rowCount++;
        }
        out.println();
        // print out total missing fields
        out.print("Total missing counts -- ");
        for (int i = 0; i < numFields; i++) {
            if (totalCounts[i] > 0) {
                out.print(fieldNames[i] + ": " + totalCounts[i]);
                if (i != numFields - 1) out.print(", ");
            }
        }
        br.close();
        out.close();
    }

}
