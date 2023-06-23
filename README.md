# TeachingMigration

Code to assist with Stanford teaching file data migration. 

## aimmigrator
* **index.js** - program that converts CSV rows into AIM files based on the teaching file template data. 

## stella
* **CSVPattern.java** - program that identifies the all RIDs in a CSV file and outputs them as a list in csvIDs.txt
* **JSONPattern.java** - program that identifies the all codeValues in a template file and outputs them as a list in jsonIDs.txt
* **CompareIDs.java** - combination of CSVPattern.java & JSONPattern.java -- the CSV and JSON sets are compared to generate a list of missing IDs in the JSON template, outputted in diff.txt
* **UniqueBodyPart.java** - program that identifies the unique body parts in the CSV and outputs them as a list in uniqueBodyPart.txt
* **UniqueSpecialty.java** - program that identifies the unique specialties in the CSV and outputs them as a list in uniqueSpecialty.txt
* **CSVCheckEmptyCells.java** - program that generates a file, missingFields.txt, that lists out the missing fields in each row, and outputs total counts at the end
