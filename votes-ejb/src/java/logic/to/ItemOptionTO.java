/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.to;

/**
 *
 * @author nico
 */
public class ItemOptionTO extends AbstractEntityTO implements Comparable<ItemOptionTO> {

    private String shortName = "";
    private String description = "";
    private Integer votes;
    private boolean voted;

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabel() {
        return description.isEmpty() ? shortName : shortName + " (" + description + ")";
    }
    
    /**
     * @return the votes
     */
    public Integer getVotes() {
        return votes;
    }

    /**
     * @param votes the votes to set
     */
    public void setVotes(Integer votes) {
        this.votes = votes;
    }
    
        /**
     * @return the voted
     */
    public boolean isVoted() {
        return voted;
    }

    /**
     * @param voted the voted to set
     */
    public void setVoted(boolean voted) {
        this.voted = voted;
    }
    
    @Override
    public int compareTo(ItemOptionTO otherOption) {
        return Integer.compare(getVotes(), otherOption.getVotes());
    }
    
}
