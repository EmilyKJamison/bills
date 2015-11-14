package de.tudarmstadt.ukp.experiments.ej.bills.billcomponents;

public class Sponsor
{
    public String name; //name of the politician
    public String partyAffiliation;
    public String sponsorType;
    
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getPartyAffiliation()
    {
        return partyAffiliation;
    }
    public void setPartyAffiliation(String partyAffiliation)
    {
        this.partyAffiliation = partyAffiliation;
    }
    public String getSponsorType()
    {
        return sponsorType;
    }
    public void setSponsorType(String sponsorType)
    {
        this.sponsorType = sponsorType;
    }

}
