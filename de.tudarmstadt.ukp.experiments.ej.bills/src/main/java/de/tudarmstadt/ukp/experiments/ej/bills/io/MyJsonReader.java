package de.tudarmstadt.ukp.experiments.ej.bills.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.apache.commons.io.FileUtils;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Action;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Bill;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Document;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Sponsor;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Vote;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Action.ActionType;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Bill.BillType;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Bill.State;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Committee.CommitteeType;
import de.tudarmstadt.ukp.experiments.ej.bills.billcomponents.Vote.VoteType;

public class MyJsonReader
{
    public static String dataDirectory;
    public static String dataFileName;
    
    public static void init() throws IOException{
        dataDirectory = DkproContext.getContext().getWorkspace("FiscalNote").getAbsolutePath() + "/fn_data/";
        dataFileName = "data.json";
    }
    public static void navigateTree(JsonValue tree, String key) {
//      initially called by: 
//      navigateTree(jsonst, null); 
        if (key != null)
           System.out.print("Key " + key + ": ");
        switch(tree.getValueType()) {
           case OBJECT:
              System.out.println("OBJECT ");
              JsonObject object = (JsonObject) tree;
              for (String name : object.keySet()){
                 navigateTree(object.get(name), name);
              }
              break;
           case ARRAY:
              System.out.println("ARRAY");
              JsonArray array = (JsonArray) tree;
              for (JsonValue val : array)
                 navigateTree(val, null);
              break;
           case STRING:
              JsonString st = (JsonString) tree;
              System.out.println("STRING " + st.getString());
              break;
           case NUMBER:
              JsonNumber num = (JsonNumber) tree;
              System.out.println("NUMBER " + num.toString());
              break;
           case TRUE:
           case FALSE:
           case NULL:
              System.out.println(tree.getValueType().toString());
              break;
        }
    }

    /**
     * Builds a Bill object from a String, probably a line of an input file.
     * 
     * @param jsonStructureString one bill's JsonStr object.
     * @return the bill with all fields complete
     * @throws IOException
     * @throws ParseException
     */
    public static Bill parseBill(String jsonStructureString) throws IOException, ParseException{

        Reader stringReader = new StringReader(jsonStructureString);
        JsonReader reader = Json.createReader(stringReader);
        JsonStructure jsonStructure = reader.read();

        Bill bill = new Bill();
//        System.out.println("Bill");
        JsonObject wholeBillObject = (JsonObject) jsonStructure;
        
        Integer id = new Integer(wholeBillObject.getInt("id"));
        bill.setId(id);
//        System.out.println("\tId: " + id.toString());
        
        JsonObject voteObject = wholeBillObject.getJsonObject("votes");
        for(String location: voteObject.keySet()){ //location is chamber_lower, chamber_upper,  etc.
        
            JsonObject locationObject = voteObject.getJsonObject(location);
        
            Vote vote = new Vote();
            vote.setLocation(location);
//            System.out.println("\tVote");
            JsonArray yesVotes = locationObject.getJsonArray("yes_vote");
            for(JsonValue voter: yesVotes){
                JsonString voterName = (JsonString) voter;
                vote.addVoterAndVote(voterName.getString(), VoteType.YES_VOTE);
//                System.out.println("\t\tVoter: " + voterName.getString() + " " + "yes_vote");
            }
            JsonArray noVotes = locationObject.getJsonArray("no_vote");
            for(JsonValue voter: noVotes){
                JsonString voterName = (JsonString) voter;
                vote.addVoterAndVote(voterName.getString(), VoteType.NO_VOTE);
    //            System.out.println("\t\tVoter: " + voterName.getString() + " " + "no_vote");
            }
            JsonArray otherVotes = locationObject.getJsonArray("other_vote");
            for(JsonValue voter: otherVotes){
                JsonString voterName = (JsonString) voter;
                vote.addVoterAndVote(voterName.getString(), VoteType.OTHER_VOTE);
    //            System.out.println("\t\tVoter: " + voterName.getString() + " " + "other_vote");
            }
            JsonString date = (JsonString) locationObject.get("date");
            vote.setDate(parseDate(date.getString()));
//            System.out.println("\t\tDate: " + date.getString());
            Date d = parseDate(date.getString());
            
            boolean passed = locationObject.getBoolean("passed");
            vote.setPassed(passed);
    //        System.out.println("\t\tPassed: " + passed);
            bill.addVote(vote);
        }
        
        JsonObject documents = (JsonObject) wholeBillObject.get("documents");
        for(String aId: documents.keySet()){
            Document document = new Document();
            String text = documents.getString(aId);
            document.setText(text);
            document.setId(aId);
//            System.out.println("\tDocument " + id + " " + text);
            bill.addDocument(document);
        }

        
        JsonObject committees = (JsonObject) wholeBillObject.get("committees");
        
        // Section below commented out because the data didn't match 
        // the schema.
//        String committeeUpperName = committees.getString("committee_upper");
//        Committee committeeUpper = new Committee();
//        committeeUpper.setCommitteeName(committeeUpperName);
//        committeeUpper.setCommitteeType(CommitteeType.COMMITTEE_UPPER);
////        System.out.println("\tCommittee Upper: " + committeeUpperName);
//        bill.addCommittee(committeeUpper);

//        String committeeLowerName = committees.getString("committee_lower");
//        Committee committeeLower = new Committee();
//        committeeLower.setCommitteeName(committeeLowerName);
//        committeeLower.setCommitteeType(CommitteeType.COMMITTEE_LOWER);
////        System.out.println("\tCommittee Lower: " + committeeLowerName);
//        bill.addCommittee(committeeLower);
        
        String title = wholeBillObject.getString("title");
        bill.setTitle(title);
//        System.out.println("\tTitle: " + title);
        
        String description = wholeBillObject.getString("description");
        bill.setDescription(description);
//        System.out.println("\tDescription: " + description);
        
        String billType = wholeBillObject.getString("bill_type");
//        bill.setBillType(billType);
        if(billType.equals("bill")){
            bill.setBillType(BillType.BILL);
        }else if(billType.equals("resolution")){
            bill.setBillType(BillType.RESOLUTION);
        }else{
            throw new IOException("bill_type does not have expected value of bill or resolution: " + billType);
        }
//        System.out.println("\tBillType: " + billType);
        
        JsonArray sponsors = wholeBillObject.getJsonArray("sponsors");
//        System.out.println("\tSponsors");
        for(JsonValue sponsorVal: sponsors){
            JsonObject sponsorObj = (JsonObject) sponsorVal;
            String name = sponsorObj.getString("name");
            String type = sponsorObj.getString("type");
//            String party = sponsorObj.getString("party");
            
            Sponsor sponsor = new Sponsor();
            sponsor.setName(name);
            sponsor.setSponsorType(type);
//            sponsor.setPartyAffiliation(party);
            bill.addSponsor(sponsor);
//            System.out.println("\t\t" + name + "  " + party + "  " + type);
        }

        JsonObject actionsObject = wholeBillObject.getJsonObject("actions");
//        System.out.println("\tActions");
        String vetoAction = actionsObject.getString("vetoed");
        Action veto = new Action();
        veto.setActionType(ActionType.VETOED);
        veto.setDate(parseDate(vetoAction));
        bill.addAction(veto);
//        System.out.println("\t\tVetoed: " + vetoAction);
        
        String resPassAction = actionsObject.getString("resolution_passed");
        Action resPass = new Action();
        resPass.setActionType(ActionType.RESOLUTION_PASSED);
        resPass.setDate(parseDate(resPassAction));
        bill.addAction(resPass);
//        System.out.println("\t\tResolution Passed: " + resPassAction);
        
        String passLowAction = actionsObject.getString("passed_lower");
        Action passLow = new Action();
        passLow.setActionType(ActionType.PASSED_LOWER);
        passLow.setDate(parseDate(passLowAction));
        bill.addAction(passLow);
//        System.out.println("\t\tPassed Lower: " + passLowAction);
        
        String failedAction = actionsObject.getString("failed");
        Action failed = new Action();
        failed.setActionType(ActionType.FAILED);
        failed.setDate(parseDate(failedAction));
        bill.addAction(failed);
//        System.out.println("\t\tFailed: " + failedAction);
        
        String passUppAction = actionsObject.getString("passed_upper");
        Action passUpp = new Action();
        passUpp.setActionType(ActionType.PASSED_UPPER);
        passUpp.setDate(parseDate(passUppAction));
        bill.addAction(passUpp);
//        System.out.println("\t\tPassed Upper: " + passUppAction);
        
        String introducedAction = actionsObject.getString("introduced");
        Action introduced = new Action();
        introduced.setActionType(ActionType.INTRODUCED);
        introduced.setDate(parseDate(introducedAction));
        bill.addAction(introduced);
//        System.out.println("\t\tIntroduced: " + introducedAction);
        
        String enactedAction = actionsObject.getString("enacted");
        Action enacted = new Action();
        enacted.setActionType(ActionType.ENACTED);
        enacted.setDate(parseDate(enactedAction));
        bill.addAction(enacted);
//        System.out.println("\t\tEnacted: " + enactedAction);
        

        String chamber = wholeBillObject.getString("chamber");
        bill.setChamber(chamber);
//        System.out.println("\tChamber: " + chamber);
        
        String state = wholeBillObject.getString("state");
        if(state.equals("md")){
            bill.setState(State.MD);
        }else if(state.equals("ca")){
            bill.setState(State.CA);
        }else{
            throw new IOException("State value must be either md or ca, but is: " + state);
        }
//        System.out.println("\tState: " + state);
        
        String session = wholeBillObject.getString("session");
        bill.setSession(session);
//        System.out.println("\tSession: " + session);
        
        
        return bill;
    }
    public static Date parseDate(String dateString) throws ParseException{
        if(dateString.equals("null")){ //the json value is literally '"null"'
            return null;
        }
        //expecting something like 2013-05-13
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        Date date = df.parse(dateString);
//        System.out.println(date);
        return date;
    }

    public static void read() throws FileNotFoundException, IOException, ParseException{
        
        File myFile = new File(dataDirectory + dataFileName);
        int cutoff = 0; //use this to only use the first n bills
        for(String line: FileUtils.readLines(myFile, "UTF-8")){
//            System.out.println("\n\nNew Bill " + cutoff + ":");
            Bill bill = parseBill(line);
            
            if (cutoff == 20000){
                break;
            }
            cutoff++;
        }
        
    }
    public static void main(String[] args) throws Exception{
        System.out.println("Starting now.");
        init();
        read();
    }
}
