// note: output folder must be present for files to be created in the folder

// change templateData to path of template file
const fs = require("fs");
const { parse } = require("csv-parse");
const readline = require('readline-sync');
const templateData = require('./data/99EPAD_947_2.25.182468981370271895711046628549377576999.json');


// Radiology Specialty
// templateData.TemplateContainer.Template[0].Component[0].AllowedTerm

// Anatomy Core
// templateData.TemplateContainer.Template[0].Component[1].AllowedTerm

// Findings and Diagnosis
// templateData.TemplateContainer.Template[0].Component[2].AllowedTerm

// Anatomy Detail
// templateData.TemplateContainer.Template[0].Component[5].AllowedTerm





const { Aim } = require("aimapi");

const enumAimType = {
    imageAnnotation: 1,
    seriesAnnotation: 2,
    studyAnnotation: 3,
};

function generateUid() {
    let uid = "2.25." + Math.floor(1 + Math.random() * 9);
    for (let index = 0; index < 38; index++) {
        uid = uid + Math.floor(Math.random() * 10);
    }
    return uid;
}

// metadata should have a series and it should have modality, description, instanceNumber and number (series number)
// for teaching we do not have series so it should be something like {series: {modality: modalityFromData, instanceNumber:'', number:'',description:'' }}
// for teaching tempModality also should be modalityFromData
function getTemplateAnswers(metadata, annotationName, tempModality, comment) {
    if (metadata.series) {
        const { number, description, instanceNumber } = metadata.series;
        const seriesModality = metadata.series.modality;
        const modality = { value: tempModality };
        const name = { value: annotationName };
        // template info
        const typeCode = [
            {
                code: '99EPAD_947',
                codeSystemName: '99EPAD',
                'iso:displayName': { 'xmlns:iso': 'uri:iso.org:21090', value: 'Teaching file' },
            },
        ];
        return { comment, modality, name, typeCode };
    }
}

// generates an annotation object for the AIM file
function generateCollectionItem(code, codeSystemName, codeMeaning, label) {
    return {
        "typeCode": [ 
            {
                "code": code, 
                "codeSystemName": codeSystemName,
                "iso:displayName": {
                    "value": codeMeaning, 
                    "xmlns:iso": "uri:iso.org:21090" 
                }
            }
        ],
        "annotatorConfidence": { "value": 0 }, 
        "label": { "value": label }, 
        "uniqueIdentifier": {
            "root": generateUid()
        }
    };
}

// generates a single AIM file based on data in csvRow object
function generateAIM(csvRow, rowNum) {

    let fileName = generateUid() + ".json";

    // CSV Data
    let date = csvRow["Date"]; // csv Date
    let name = csvRow["Name"]; // csv Name
    let patientId = csvRow["Medical record number"]; // csv Medical record number
    let accessionNumber = csvRow["Accession number"]; // csv Accession number
    let suid = csvRow["SUID (Study UID)"]; // csv SUID
    let age = csvRow["Current age"]; // csv Current age (or deceased)
    let sex = csvRow["Sex"]; // csv Sex
    let modality = csvRow["Modality"]; // csv Modality
    let description = csvRow["Description"]; // csv Description
    let bodyPart = csvRow["Body part"]; // csv Body part
    let keywords = csvRow["Teaching file keywords"]; // csv Teaching file keywords
    let specialty = csvRow["Specialty"]; // csv Specialty
    let reportAuthor = csvRow["Report author"]; // csv Report author
    let readingPhysician = csvRow["Reading physician"]; // csv Reading physician

    console.log("Row:", rowNum, ", Medical record number:", patientId, ", SUID:", suid);
    console.log(fileName);


    // generate keywordsArray, tracking the RIDs in the teaching file keywords
    let keywordsArray = [];
    let keywordsIndex = 0;
    let keywordExists = keywords.indexOf("(", keywordsIndex);
    while (keywordExists != -1) {
        keywordsIndex = keywords.indexOf(")", keywordExists);
        keywordsArray.push(keywords.substring(keywordExists + 1, keywordsIndex));
        keywordExists = keywords.indexOf("(", keywordsIndex);
    }






    // generate comment, NN-year old (or deceased) female/male
    let comment = { value: ' ' };
    if (age.toLowerCase() === "deceased") {
        comment.value = age + " ";
    } else {
        age = age.substring(0, age.length - 6); // format age
        comment.value = age + "-year-old ";
    }

    if (sex === 'F') {
        comment.value += "female";
    } else if (sex === 'M') {
        comment.value += "male";
    }









    // anatomies =['RID230', 'RIS10']; // coming from "Anatomy Detail" in template
    // diagnosis =['RIS1122', 'RID3455']; // coming from "Findings and Diagnosis" in template
    // use a map of codevalue and codemeaning from template
    // codevalue is the RID
    // codemeaning is the displayname

    let createdPhysicalEntityCollection = []; // anatomy core
    let createdObservationEntityCollection = []; // specialty + findings and diagnosis + anatomy detail

    // adding specialty
    if (specialtyMap.has(specialty)) {
        let specialtyItem = specialtyMap.get(specialty);
        createdObservationEntityCollection.push(generateCollectionItem(specialtyItem.code, specialtyItem.codeSystemName, specialtyItem.codeMeaning, "Radiology Specialty"));
    } else {
        console.log("template missing specialty", specialty);
    }

    // adding body parts
    let bodyPartArray = bodyPart.split(",");
    for (let i = 0; i < bodyPartArray.length; i++) {
        if (bodyPartMap.has(bodyPartArray[i])) {
            let bodyPartItem = bodyPartMap.get(bodyPartArray[i]);
            createdPhysicalEntityCollection.push(generateCollectionItem(bodyPartItem.code, bodyPartItem.codeSystemName, bodyPartItem.codeMeaning, "Anatomy Core"))
        } else {
            console.log("template missing body part", bodyPartArray[i]);
        }
    }

    // adding findings and diagnosis + anatomy detail
    for (let i = 0; i < keywordsArray.length; i++) {
        if (anatomyMap.has(keywordsArray[i])) {
            let anatomyItem = anatomyMap.get(keywordsArray[i]);
            createdObservationEntityCollection.push(generateCollectionItem(keywordsArray[i], anatomyItem.codeSystemName, anatomyItem.codeMeaning, "Anatomy Detail"));
        } else if (diagnosisMap.has(keywordsArray[i])) {
            let diagnosisItem = diagnosisMap.get(keywordsArray[i]);
            createdObservationEntityCollection.push(generateCollectionItem(keywordsArray[i], diagnosisItem.codeSystemName, diagnosisItem.codeMeaning, "Findings and Diagnosis"));
        } else {
            console.log("template missing keyword", keywordsArray[i]);
        }
    }






    // fill in the seed data
    const seedData = {};
    seedData.aim = {};
    seedData.study = {};
    seedData.series = {};
    seedData.equipment = {};
    seedData.person = {};
    seedData.image = [];
    seedData.aim.studyInstanceUid = suid; // csv SUID
    seedData.study.startTime = ""; // empty
    seedData.study.instanceUid = suid; // csv SUID
    let dateArray = date.split("/");
    if (dateArray[0].length == 1) { // month
        dateArray[0] = "0" + dateArray[0];
    }
    if (dateArray[1].length == 1) { // day
        dateArray[1] = "0" + dateArray[1];
    }
    seedData.study.startDate = dateArray[2] + dateArray[0] + dateArray[1]; // csv Date (reformatted)
    seedData.study.accessionNumber = accessionNumber; // csv accession
    seedData.study.examTypes = modality;
    seedData.series.instanceUid = ""; // empty
    seedData.series.modality = modality;
    seedData.series.number = ""; // empty
    seedData.series.description = description; // csv description
    seedData.series.instanceNumber = ""; // empty
    seedData.equipment.manufacturerName = ""; // empty
    seedData.equipment.manufacturerModelName = ""; // empty
    seedData.equipment.softwareVersion = ""; // empty
    seedData.person.sex = sex; // csv sex
    let nameArray = name.split(", ");
    seedData.person.name = nameArray[1] + " " + nameArray[0]; // csv name (reformatted)
    seedData.person.patientId = patientId; // csv Medical record number
    if (age.toLowerCase() === "deceased") {
        seedData.person.birthDate = age;
    } else {
        seedData.person.birthDate = (2022 - parseInt(age, 10)) + "0101"; // csv calculated date 66 years
    }
    const sopClassUid = "";
    const sopInstanceUid = "";



    // only adds physical and observation collections if there are keywords present
    if (createdPhysicalEntityCollection.length > 0)
        seedData.aim.imagingPhysicalEntityCollection = { "ImagingPhysicalEntity": createdPhysicalEntityCollection };

    if (createdObservationEntityCollection.length > 0)
        seedData.aim.imagingObservationEntityCollection = { "ImagingObservationEntity": createdObservationEntityCollection };



    seedData.image.push({ sopClassUid, sopInstanceUid });


    const answers = getTemplateAnswers(seedData, 'nodule1', '', comment);
    const merged = { ...seedData.aim, ...answers };
    seedData.aim = merged;
    seedData.user = { loginName: 'admin', name: 'Full name' }

    const aim = new Aim(seedData, enumAimType.studyAnnotation);

    // writes new AIM file to output folder
    fs.writeFileSync("./output/" + fileName, JSON.stringify(aim.getAimJSON()));
    console.log();

}





// Specialty Map Setup, eventually map to templateData.TemplateContainer.Template[0].Component[0].AllowedTerm
const specialtyMap = new Map();
specialtyMap.set("CT BODY", { "code": "RID50608", "codeMeaning": "Abdominal Radiology", "codeSystemName": "Radlex" });


// Body Part Map Setup, eventually map to templateData.TemplateContainer.Template[0].Component[1].AllowedTerm
const bodyPartMap = new Map();
// bodyPartMap.set("CT BODY", { "code": "RID50608", "codeMeaning": "Abdominal Radiology", "codeSystemName": "Radlex" });


// Anatomy Detail Map Setup
const anatomyMap = new Map();
let anatomyTerms = templateData.TemplateContainer.Template[0].Component[5].AllowedTerm;
for (let i = 0; i < anatomyTerms.length; i++) {
    anatomyMap.set(anatomyTerms[i].codeValue, { "codeMeaning": anatomyTerms[i].codeMeaning, "codeSystemName": anatomyTerms[i].codingSchemeDesignator });
}


// Findings and Diagnosis Map Setup
const diagnosisMap = new Map();
let diagnosisTerms = templateData.TemplateContainer.Template[0].Component[2].AllowedTerm;
for (let i = 0; i < diagnosisTerms.length; i++) {
    diagnosisMap.set(diagnosisTerms[i].codeValue, { "codeMeaning": diagnosisTerms[i].codeMeaning, "codeSystemName": diagnosisTerms[i].codingSchemeDesignator });
}








let csvFilename = readline.question("Enter CSV file name: ");

// reads data from CSV file and generates AIMs
let csvData = [];
fs.createReadStream("./data/" + csvFilename) // edit to match CSV file path
    .pipe(
        parse({
            delimiter: ",",
            columns: true,
            ltrim: true,
        })
    )
    .on("data", function (row) {
        csvData.push(row);
    })
    .on("end", function () {
        for (let i = 0; i < csvData.length; i++) {
            generateAIM(csvData[i], i+2);
        }
    });


