package de.tudarmstadt.ukp.experiments.ej.bills.billcomponents;

public class Committee
{
    public String committeeName;
    public CommitteeType committeeType;
    
    public enum CommitteeType {COMMITTEE_UPPER, COMMITTEE_LOWER}
    
    
    public String getCommitteeName()
    {
        return committeeName;
    }
    public void setCommitteeName(String committeeName)
    {
        this.committeeName = committeeName;
    }
    public CommitteeType getCommitteeType()
    {
        return committeeType;
    }
    public void setCommitteeType(CommitteeType committeeType)
    {
        this.committeeType = committeeType;
    };

}
