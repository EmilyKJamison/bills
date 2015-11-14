package de.tudarmstadt.ukp.experiments.ej.bills.billcomponents;

import java.util.Date;

public class Action
{
    public Date date;
    public ActionType actionType;
    
    public enum ActionType {VETOED, 
                            RESOLUTION_PASSED, 
                            PASSED_LOWER, 
                            FAILED, 
                            PASSED_UPPER, 
                            INTRODUCED, ENACTED
                            }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public ActionType getActionType()
    {
        return actionType;
    }

    public void setActionType(ActionType actionType)
    {
        this.actionType = actionType;
    };
                            
                            
}
