/*
 * This program identifies the unique body parts in the CSV and outputs them as a list in uniqueBodyPart.txt.
 * The CSV file to be read can be changed in the BufferedReader line.
 *
 * CSV must be in this format: Date,Name,Medical record number,Accession number,SUID (Study UID),Current age,Sex,Modality,Description,Body part,Teaching file keywords,Specialty,Report author,Reading physician
 *
 */

import java.io.*;
import java.util.HashSet;

public class UniqueBodyPart {

    public static void main(String[] args) throws Exception {
        // can change CSV file path
        BufferedReader br = new BufferedReader(new FileReader(new File("All_TF_Cases_July_8_2022_NO_PHI.xlsx - All Specialties - NO PHI.csv")));
        br.readLine();
        String str = br.readLine();

        // output file
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("uniqueBodyPart.txt")));
        // set to store unique body parts
        HashSet<String> bodyPart = new HashSet<>();

        // loops through all lines of CSV
        while (str != null) {
            String[] arr = str.split(",");
            int ind = 0;
            for (int i = 0; i < 9 && ind < arr.length; i++) {
                if (arr[ind].startsWith("\"")) {
                    ind = ignore(ind, arr); // skips to index of the end of current field (by detecting quotation)
                    // this is for fields with commas that are enclosed by quotations
                }
                ind++;
            }
            if (ind < arr.length && !arr[ind].equals(""))
                if (arr[ind].startsWith("\"")) {
                    String res = combine(ind, arr); // strings all body parts in the field together
                    bodyPart.add(res.substring(1, res.length()-1)); // adds to bodypart list
                } else {
                    bodyPart.add(arr[ind]); // adds to bodypart list
                }

            str = br.readLine();
        }

        for (String s : bodyPart) {
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

    // strings the components of the body part cell together (components that are separated by commas)
    public static String combine(int index, String[] arr) {
        if (index == arr.length - 1 || arr[index].endsWith("\"")) {
            return arr[index];
        }
        return arr[index] + "," + combine(index + 1, arr);
    }

}
