/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.to;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import persistence.entities.DecisionMode;
import persistence.entities.ItemType;

/**
 *
 * @author nico
 */
public class ItemTO extends AbstractEntityTO {

    /**
     *
     */
    private static final long serialVersionUID = 9020834185724467524L;
    private String title;
    private ItemType type;
    private DecisionMode decisionMode;
    private Integer m = 1;
    private List<ItemOptionTO> options = new ArrayList<>();
    private List<ItemOptionTO> ownOptions = new ArrayList<>();
    private boolean allowOwnOptions;
    private ItemOptionTO winner;
    private Integer abstentions;
    private boolean abstainFlag;
    private String[] decisions;
    private String decision;

    public ItemTO() {
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the type
     */
    public ItemType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(ItemType type) {
        this.type = type;
    }
    
    /**
     * @return the decisionMode
     */
    public DecisionMode getDecisionMode() {
        return decisionMode;
    }

    /**
     * @param decisionMode the decisionMode to set
     */
    public void setDecisionMode(DecisionMode decisionMode) {
        this.decisionMode = decisionMode;
    }
    
    /**
     * @return the m
     */
    public Integer getM() {
        return m;
    }

    /**
     * @param m the m to set
     */
    public void setM(Integer m) {
        this.m = m;
        while (options.size() < m) {
            createOption();
        }
    }
    
    public boolean isMofN() {
        return this.type == ItemType.M_OF_N;
    }

    public boolean isYesNo() {
        return this.type == ItemType.YES_NO;
    }
    
    /**
     * @return the options
     */
    public List<ItemOptionTO> getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(List<ItemOptionTO> options) {
        this.options = options;
    }
    
    public void createOption() {
        ItemOptionTO option = new ItemOptionTO();
        options.add(option);
    }
    
    public void removeOption(ItemOptionTO option) {
        options.remove(option);
    }
    
    /**
     * @return the options
     */
    public List<ItemOptionTO> getOwnOptions() {
        return ownOptions;
    }

    /**
     * @param options the options to set
     */
    public void setOwnOptions(List<ItemOptionTO> options) {
        this.ownOptions = options;
    }
    
    public void createOwnOption(String name, String description) {
        ItemOptionTO option = new ItemOptionTO();
        option.setShortName(name);
        option.setDescription(description);
        if (!ownOptions.contains(option)) {
            ownOptions.add(option);
        }
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
    
    /**
     * @return the abstainFlag
     */
    public boolean getAbstainFlag() {
        return abstainFlag;
    }

    /**
     * @param abstainFlag the abstainFlag to set
     */
    public void setAbstainFlag(boolean abstainFlag) {
        this.abstainFlag = abstainFlag;
    }

    /**
     * @param winner the winner to set
     */
    public void setWinner(ItemOptionTO winner) {
        this.winner = winner;
    }

    /**
     * @return the winner
     */
    public ItemOptionTO getWinner() {
        return winner;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String[] getDecisions() {
        return decisions;
    }

    public void setDecisions(String[] decisions) {
        this.decisions = decisions;
    }

    public boolean isAllowOwnOptions() {
        return allowOwnOptions;
    }

    public void setAllowOwnOptions(boolean allowOwnOptions) {
        this.allowOwnOptions = allowOwnOptions;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.title);
        hash = 59 * hash + Objects.hashCode(this.type);
        hash = 59 * hash + Objects.hashCode(this.decisionMode);
        hash = 59 * hash + Objects.hashCode(this.m);
        hash = 59 * hash + Objects.hashCode(this.options);
        hash = 59 * hash + Objects.hashCode(this.ownOptions);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemTO other = (ItemTO) obj;
        return true;
    }
    
    
}
