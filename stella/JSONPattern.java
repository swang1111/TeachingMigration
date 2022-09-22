/*
 * This program identifies the all codeValues in a template file and outputs them as a list in jsonIDs.txt.
 * The JSON template file to be read can be changed in the BufferedReader line.
 *
 */


import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONPattern {
    public static void main(String[] args) throws Exception {
        // can change JSON template file path
        BufferedReader br = new BufferedReader(new FileReader(new File("99EPAD_947_2.25.182468981370271895711046628549377576999.json")));
        String str = br.readLine();

        // output file
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("jsonIDs.txt")));

        // finding & grabbing codeValues
        Pattern pattern = Pattern.compile("\"codeValue\":\"\\w+\"");
        Matcher matcher = pattern.matcher(str);

        // map to store codeValue + codeMeaning
        TreeMap<String, String> jsonMap = new TreeMap<>();
        // tracking max length RID for spacing
        int maxRID = 0;

        // looping through each ID in line
        while (matcher.find()) {
            // splitting to retrieve only the codeValue (and ignoring all other info)
            String[] arr = matcher.group().split("\"");
            String RID = arr[3];
            maxRID = Math.max(RID.length(), maxRID);

            // code to retrieve the codeMeaning
            int startIndex = matcher.start();
            int endIndex = matcher.end() + 1;
            if (str.charAt(startIndex - 1) == '{') {
                // back, search for next comma
                startIndex = endIndex;
                endIndex = str.indexOf(',', startIndex);
            } else {
                // front
                endIndex = startIndex - 1;
                startIndex = str.lastIndexOf(',', endIndex - 1) + 1;
            }

            // splitting codeMeaning to retrieve only the data
            String[] tempMeaning = str.substring(startIndex, endIndex).split(":");
            String jsonMeaning = tempMeaning[1].substring(1, tempMeaning[1].length()-1);

            jsonMap.put(RID, jsonMeaning);


        }

        // output data into file
        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            out.print(entry.getKey());
            for (int i = 0; i <= (maxRID - entry.getKey().length()) + 1; i++) {
                out.print(" ");
            }
            out.println(entry.getValue());
        }

        out.close();
        br.close();
    }
}