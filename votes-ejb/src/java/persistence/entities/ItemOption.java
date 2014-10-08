package persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import logic.to.ItemOptionTO;

@Entity
public class ItemOption extends AbstractEntity<ItemOption, ItemOptionTO> implements Comparable<ItemOption> {

    /**
     *
     */
    private static final long serialVersionUID = -1210387107582692302L;
    private String shortName;
    private String description;
    private Integer votes;
    private Item item;
    private Item winningItem;

    public ItemOption() {
        votes = 0;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    @Lob
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
    
    @OneToOne
    public Item getWinningItem() {
        return winningItem;
    }

    public void setWinningItem(Item item) {
        this.winningItem = item;
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

    @Override
    public ItemOptionTO createTO() {
        ItemOptionTO itemOptionTO = new ItemOptionTO();
        itemOptionTO.setId(getId());
        itemOptionTO.setDescription(getDescription());
        itemOptionTO.setShortName(getShortName());

        if (item.getPoll().getPollState() == PollState.FINISHED
                || item.getPoll().getPollState() == PollState.PUBLISHED) {
            itemOptionTO.setVotes(getVotes());
        }

        return itemOptionTO;
    }

    @Override
    public int compareTo(ItemOption o) {
        return Integer.compare(getVotes(), o.getVotes());
    }

}
