package de.tudarmstadt.ukp.experiments.ej.bills.billcomponents;

import java.util.ArrayList;
import java.util.List;

public class Bill
{
    public String title; // text of the title
    public Integer id; // unique identifier of the bill
    public String description; // text with a more detailed description of the bill contents
    public List<Document> documents; // list of document versions for this bill, containing the text of the body of the bill

    public State state; // which state legislature the bill is from
    public BillType billType; // resolution or bill
    
    public List<Sponsor> sponsors; // list of legislators sponsoring the bill, with name, party affiliation, and sponsor type
    public List<Committee> committees; // committees bill is assigned to in either chamber
    public String chamber; // lower or upper chamber of legislative body sponsoring the bill
    public String session; // legislative session in which the bill was proposed
    public List<Vote> votes; // list of votes taken on the this bill in either chamber
    public List<Action> actions; // landmark actions associated with passage of this bill
    
    public enum State {MD, CA};
    public enum BillType {BILL, RESOLUTION}
    
    public Bill(){
        documents = new ArrayList<Document>();
        sponsors = new ArrayList<Sponsor>();
        committees = new ArrayList<Committee>();
        votes = new ArrayList<Vote>();
        actions = new ArrayList<Action>();
    }
    
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public Integer getId()
    {
        return id;
    }
    public void setId(Integer id)
    {
        this.id = id;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public List<Document> getDocuments()
    {
        return documents;
    }
    public void setDocuments(List<Document> documents)
    {
        this.documents = documents;
    }
    public void addDocument(Document document){
        this.documents.add(document);
    }
    public State getState()
    {
        return state;
    }
    public void setState(State state)
    {
        this.state = state;
    }
    public BillType getBillType()
    {
        return billType;
    }
    public void setBillType(BillType billType)
    {
        this.billType = billType;
    }
    public List<Sponsor> getSponsors()
    {
        return sponsors;
    }
    public void setSponsors(List<Sponsor> sponsors)
    {
        this.sponsors = sponsors;
    }
    public void addSponsor(Sponsor sponsor){
        this.sponsors.add(sponsor);
    }
    public List<Committee> getCommittees()
    {
        return committees;
    }
    public void setCommittees(List<Committee> committees)
    {
        this.committees = committees;
    }
    public void addCommittee(Committee committee){
        this.committees.add(committee);
    }
    public String getChamber()
    {
        return chamber;
    }
    public void setChamber(String chamber)
    {
        this.chamber = chamber;
    }
    public String getSession()
    {
        return session;
    }
    public void setSession(String session)
    {
        this.session = session;
    }
    public List<Vote> getVotes()
    {
        return votes;
    }
    public void setVotes(List<Vote> votes)
    {
        this.votes = votes;
    }
    public void addVote(Vote vote){
        this.votes.add(vote);
    }
    public List<Action> getActions()
    {
        return actions;
    }
    public void setActions(List<Action> actions)
    {
        this.actions = actions;
    };
    public void addAction(Action action){
        this.actions.add(action);
    }

}
