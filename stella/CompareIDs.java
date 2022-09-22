/*
 * This program is a combination of CSVPattern.java & JSONPattern.java. When the program is run, the CSV
 * and JSON sets will be created and populated. After generating both the CSV and JSON sets,
 * they are compared to generate a list of missing IDs in the JSON template, outputted in diff.txt.
 * The JSON template & CSV files to be read can be changed in the BufferedReader lines.
 *
 */

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareIDs {

    public static void main(String[] args) throws Exception {

        /* JSON Code */

        BufferedReader br = new BufferedReader(new FileReader(new File("99EPAD_947_2.25.182468981370271895711046628549377576999.json")));
        String str = br.readLine();


        Pattern pattern = Pattern.compile("\"codeValue\":\"\\w+\"");
        Matcher matcher = pattern.matcher(str);

        TreeSet<String> jsonSet = new TreeSet<>();

        while (matcher.find()) {
            String[] arr = matcher.group().split("\"");
            jsonSet.add(arr[3]);
        }

        br.close();




        /* CSV Code */


        BufferedReader br2 = new BufferedReader(new FileReader(new File("All_TF_Cases_June_2022_NO_PHI.xlsx - All No PHI.csv")));
        String str2 = br2.readLine();


        Pattern pattern2 = Pattern.compile("RID\\d+");
        Matcher matcher2;

        TreeMap<String, Integer> map = new TreeMap<>();
        HashMap<String, String> names = new HashMap<>();
        int max = 0;
        int maxName = 0;

        while (str2 != null) {
            matcher2 = pattern2.matcher(str2);
            while (matcher2.find()) {
                String ID = matcher2.group();
                max = Math.max(ID.length(), max);
                map.put(ID, map.getOrDefault(ID, 0) + 1);


                if (!names.containsKey(ID)) {
                    int endIndex = matcher2.start() - 1;
                    int startIndex = str2.lastIndexOf(',', endIndex) + 1;
                    String name = str2.substring(startIndex, endIndex);
                    name = name.trim();
                    if (name.startsWith("\"")) {
                        name = name.substring(1);
                    }
                    names.put(ID, name);
                    maxName = Math.max(name.length(), maxName);
                }
            }
            str2 = br2.readLine();
        }




        /* Check for difference & output */

        Set<String> csvSet = map.keySet();
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("diff.txt")));

        for (String csvKey : csvSet) {
            if (!jsonSet.contains(csvKey)) {
                out.print(csvKey);
                for (int i = 0; i <= (max - csvKey.length()) + 1; i++) {
                    out.print(" ");
                }
                out.println(names.get(csvKey));
            }
        }





        out.close();
        br2.close();
    }

}
