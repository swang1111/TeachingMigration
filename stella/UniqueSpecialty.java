/*
 * This program identifies the unique specialties in the CSV and outputs them as a list in uniqueSpecialty.txt.
 * The CSV file to be read can be changed in the BufferedReader line.
 *
 * CSV must be in this format: Date,Name,Medical record number,Accession number,SUID (Study UID),Current age,Sex,Modality,Description,Body part,Teaching file keywords,Specialty,Report author,Reading physician
 *
 */

import java.io.*;
import java.util.HashSet;

public class UniqueSpecialty {

    public static void main(String[] args) throws Exception {

        // can change CSV file path
        BufferedReader br = new BufferedReader(new FileReader(new File("All_TF_Cases_July_8_2022_NO_PHI.xlsx - All Specialties - NO PHI.csv")));
        br.readLine();
        String str = br.readLine();

        // output file
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("uniqueSpecialty.txt")));
        // set to store unique specialties
        HashSet<String> specialty = new HashSet<>();

        // loops through all lines of CSV
        while (str != null) {
            String[] arr = str.split(",");
            int ind = 0;
            for (int i = 0; i < 11 && ind < arr.length; i++) {
                if (arr[ind].startsWith("\"")) {
                    ind = ignore(ind, arr); // skips to index of the end of current field (by detecting quotation)
                    // this is for fields with commas that are enclosed by quotations
                }
                ind++;
            }
            // once the specialty field index is reached, adds the specialty to the hashset
            if (ind < arr.length && !arr[ind].equals(""))
                specialty.add(arr[ind]);

            str = br.readLine();
        }

        for (String s : specialty) {
            out.println(s);
        }

        out.close();
        br.close();

    }

    // method to find the ending quotation of a CSV field in arr
    public static int ignore(int index, String[] arr) {
        if (arr[index].endsWith("\"")) {
            return index;
        }
        return ignore(index + 1, arr);
    }

}
