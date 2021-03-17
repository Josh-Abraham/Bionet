package com.application.projecttbh;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.mitre.jet.ebts.Ebts;
import org.mitre.jet.ebts.field.Field;
import org.mitre.jet.ebts.field.Occurrence;
import org.mitre.jet.ebts.field.SubField;
import org.mitre.jet.ebts.records.GenericRecord;
import org.mitre.jet.ebts.records.LogicalRecord;
import org.mitre.jet.exceptions.EbtsBuildingException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// EBTSMaker Information structured based on: https://www.fbibiospecs.cjis.gov/ebts/Approved
public class EBTSMaker {

    public static void createRecord(JSONObject userData, Context context) throws JSONException, EbtsBuildingException, IOException {
        Ebts ebts = new Ebts();
        GenericRecord type2Record = new GenericRecord(2); // General Info
        GenericRecord type10Record = new GenericRecord(10); // Facial Scan
        GenericRecord type17Record = new GenericRecord(17); // Iris Scan

        type2Record.setField(15, new Field((String) userData.get("PassportNumber"))); // SID An identification of a person based on a country issued ID card
        type2Record.setField(18, new Field((String) OnboardData.getInstance().getFullName())); // Field 18 - Name tag
        type2Record.setField(22, new Field((String) userData.get("DOB"))); // Field 22 - DOB
        type2Record.setField(41, new Field((String) userData.get("StreetAddress"))); // Field 22 - Address
        type2Record.setField(42, new Field((String) userData.get("City"))); // Field 22 - Address
        type2Record.setField(43, new Field((String) userData.get("Province"))); // Field 22 - Address
        type2Record.setField(44, new Field((String) userData.get("Country"))); // Field 22 - Address
        type2Record.setField(45, new Field((String) userData.get("PostalCode"))); // Field 22 - Address
        type2Record.setField(33, new Field((String) userData.get("PassportNumber"))); // In this implementation, the finger print set is saved under the passport id with identifiers _FP_0..3

        type10Record.setField(2, new Field((String) userData.get("FACE")));

        type17Record.setField(2, new Field((String) userData.get("IRIS_L")));
        type17Record.setField(3, new Field((String) userData.get("IRIS_R")));

        ebts.addRecord(type2Record);
        ebts.addRecord(type10Record);
        ebts.addRecord(type17Record);
        List<Integer> recordTypes = new ArrayList<Integer>();

        recordTypes.add(2);
        recordTypes.add(10);
        recordTypes.add(17);

        String ebtsData = "<Ebts>\n";

        for (int a=0; a<recordTypes.size(); a++){
            if (ebts.containsRecord(recordTypes.get(a))){
                List <LogicalRecord> records = ebts.getRecordsByType(recordTypes.get(a));
                //Records
                for (int h = 0; h<records.size(); h++){
                    ebtsData = ebtsData + "\t <Record>\n";
                    ebtsData = ebtsData +  "\t\t <RecordType>" + recordTypes.get(a)+" </RecordType>\n";
                    Map<Integer, Field> fields = records.get(h).getFields();

                    //Fields
                    for (Map.Entry<Integer, Field> entry : fields.entrySet()){
                        String fieldNumber = Integer.toString(entry.getKey());
                        if (!fieldNumber.equals("999") && !(recordTypes.get(a)==4 && fieldNumber.equals("9"))){
                            ebtsData = ebtsData + "\t\t\t <FieldValue> " +fieldNumber+ " </FieldValue>\n";
                            List<Occurrence> occurrences = entry.getValue().getOccurrences();

                            for(Occurrence occurrence : occurrences){
                                List<SubField> subfields = occurrence.getSubFields();
                                for(SubField subfield : subfields){
                                    ebtsData = ebtsData + "\t\t\t\t <FieldContents> " +subfield+ " </FieldContents>\n";
                                }
                            }
                        }
                    }
                    ebtsData = ebtsData + "\t </Record>\n";
                }
            }
        }
        ebtsData = ebtsData + "</Ebts>";
        String fileName = "ebts_file.txt";
        File dir = new File(context.getFilesDir(), "EBTS");
        if(!dir.exists()){
            dir.mkdir();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(context.getFilesDir() + "/EBTS/" + fileName));
        writer.write(ebtsData);

        writer.close();
        File file = new File(dir, fileName);

        S3Client.uploadEBTS(file, context);
    }
}
