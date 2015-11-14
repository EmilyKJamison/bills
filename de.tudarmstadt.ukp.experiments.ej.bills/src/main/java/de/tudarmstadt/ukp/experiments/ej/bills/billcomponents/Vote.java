package de.tudarmstadt.ukp.experiments.ej.bills.billcomponents;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Vote
{
    public Date date;
    public Boolean passed;
    public String location; //chamber lower or chamber upper.  Note that this is in the data but breaks the schema.
    public Map<String, VoteType> voterAndVote; // i.e. {Smith,YES_VOTE}

    public enum VoteType {YES_VOTE, NO_VOTE, OTHER_VOTE}
    
    public Vote(){
        voterAndVote = new HashMap<String, VoteType>();
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public Boolean getPassed()
    {
        return passed;
    }

    public void setPassed(Boolean passed)
    {
        this.passed = passed;
    }

    public Map<String, VoteType> getVoterAndVote()
    {
        return voterAndVote;
    }

    public void setVoterAndVote(Map<String, VoteType> voterAndVote)
    {
        this.voterAndVote = voterAndVote;
    };
    public void addVoterAndVote(String voter, VoteType vote){
        this.voterAndVote.put(voter, vote);
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
}
