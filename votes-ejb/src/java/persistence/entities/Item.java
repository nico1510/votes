package persistence.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import logic.to.ItemTO;

@Entity
public class Item extends AbstractEntity<Item, ItemTO> {

    /**
     *
     */
    private static final long serialVersionUID = 3594380960135005101L;
    private String title;
    private ItemType type;
    private DecisionMode decisionMode;
    private Integer m;
    private Integer abstentions;
    private Poll poll;
    private List<ItemOption> options;
    private ItemOption winner;

    public Item() {
        abstentions = 0;
        options = new ArrayList<ItemOption>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Enumerated(EnumType.STRING)
    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
    

    /**
     * @return the decisionMode
     */
    @Enumerated(EnumType.STRING)
    public DecisionMode getDecisionMode() {
        return decisionMode;
    }

    /**
     * @param decisionMode the decisionMode to set
     */
    public void setDecisionMode(DecisionMode decisionMode) {
        this.decisionMode = decisionMode;
    }
    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    /**
     * @return the abstentions
     */
    public Integer getAbstentions() {
        return abstentions;
    }

    /**
     * @param abstentions the abstentions to set
     */
    public void setAbstentions(Integer abstentions) {
        this.abstentions = abstentions;
    }

    @ManyToOne
    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ItemOption> getOptions() {
        return options;
    }

    public void setOptions(List<ItemOption> options) {
        this.options = options;
    }
    
    /**
     * @return the winner
     */
    @OneToOne(mappedBy = "winningItem", cascade = CascadeType.ALL)
    public ItemOption getWinner() {
        return winner;
    }

    /**
     * @param winner the winner to set
     */
    public void setWinner(ItemOption winner) {
        this.winner = winner;
    }

    @Override
    public ItemTO createTO() {
        ItemTO itemTO = new ItemTO();
        itemTO.setId(getId());
        itemTO.setM(m);
        itemTO.setOptions(createTransferList(getOptions()));
        itemTO.setTitle(getTitle());
        itemTO.setType(getType());
        itemTO.setDecisionMode(decisionMode);

        if (getPoll().getPollState() == PollState.FINISHED
                || getPoll().getPollState() == PollState.PUBLISHED) {
            itemTO.setAbstentions(getAbstentions());
            if(getWinner()!=null) {
                itemTO.setWinner(getWinner().createTO());
            } else {
                itemTO.setWinner(null);
            }
        }

        return itemTO;

    }

}
