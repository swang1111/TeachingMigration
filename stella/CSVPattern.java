/*
 * This program identifies the all RIDs in a CSV file and outputs them as a list in csvIDs.txt.
 * The CSV file to be read can be changed in the BufferedReader line.
 *
 */

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVPattern {
    public static void main(String[] args) throws Exception {
        // can change JSON template file path
        BufferedReader br = new BufferedReader(new FileReader(new File("All_TF_Cases_June_2022_NO_PHI.xlsx - All No PHI.csv")));
        String str = br.readLine();

        // output file
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("csvIDs.txt")));

        Pattern pattern = Pattern.compile("RID\\d+");
        Matcher matcher;

        // map to store RIDs and their corresponding frequencies
        TreeMap<String, Integer> map = new TreeMap<>();
        // map to store RIDs and their names
        HashMap<String, String> names = new HashMap<>();
        int max = 0;
        int maxName = 0;

        // looping through each line of file
        while (str != null) {
            // grabs RIDs
            matcher = pattern.matcher(str);
            // looping through each RID in line
            while (matcher.find()) {
                // retrieve next ID
                String ID = matcher.group();
                max = Math.max(ID.length(), max);
                // add 1 to the frequency of the ID
                map.put(ID, map.getOrDefault(ID, 0) + 1);

                // check if name of the ID has already been stored, if not, add
                if (!names.containsKey(ID)) {
                    int endIndex = matcher.start() - 1;
                    int startIndex = str.lastIndexOf(',', endIndex) + 1;
                    String name = str.substring(startIndex, endIndex);
                    name = name.trim(); // clears out white space
                    if (name.startsWith("\"")) {
                        name = name.substring(1); // removes extra quotation at start (if present)
                    }
                    names.put(ID, name);
                    maxName = Math.max(name.length(), maxName);
                }
            }
            str = br.readLine();
        }


        // output data into file
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            out.print(entry.getKey());
            for (int i = 0; i <= (max - entry.getKey().length()) + 1; i++) {
                out.print(" ");
            }
            String entryName = names.get(entry.getKey());
            out.print(entryName);
            for (int i = 0; i <= (maxName - entryName.length()) + 1; i++) {
                out.print(" ");
            }
            out.println(entry.getValue());
        }

        out.close();
        br.close();
    }
}