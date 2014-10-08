/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.to;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import persistence.entities.DecisionMode;
import persistence.entities.ItemType;
import persistence.entities.PollState;

/**
 *
 * @author nico
 */
public class PollTO extends AbstractEntityTO {

    /**
     *
     */
    private static final long serialVersionUID = -1056699865005638974L;
    private long versionId;
    private String title;
    private String description;
    private PollState state;
    private Date start;
    private Date end;
    private Date reminderDate;
    private boolean participationTracking;
    private OrganizerTO creator;
    private List<OrganizerTO> organizers = new ArrayList<>();
    private List<ItemTO> items = new ArrayList<>();
    private List<ParticipantTO> participants = new ArrayList<>();

    public PollTO() {
        
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

    /**
     * @return the state
     */
    public PollState getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(PollState state) {
        this.state = state;
    }

    public boolean isPrepared() {
        return state == PollState.PREPARED;
    }
    
    public boolean isStarted() {
        return state == PollState.STARTED;
    }

    public boolean isRunning() {
        return state == PollState.RUNNING;
    }

    public boolean isFinished() {
        return state == PollState.FINISHED;
    }
    
    public boolean isProhibited() {
        return state == PollState.PROHIBITED;
    }

    public boolean isPublished() {
        return state == PollState.PUBLISHED;
    }

    public boolean isViewAble() {
        return state == PollState.FINISHED || state == PollState.PUBLISHED;
    }

    public boolean isRestartAble() {
        if (state == PollState.PROHIBITED) {
            return true;
        } else {
            for (ItemTO item : getItems()) {
                if (item.getWinner() == null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @return the start
     */
    public Date getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Date start) {
        this.start = start;
    }

    public boolean isOutOfDate() {
        return (start.compareTo(new Date()) < 0);
    }
    
    /**
     * @return the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * @return the reminderDate
     */
    public Date getReminderDate() {
        return reminderDate;
    }

    /**
     * @param reminderDate the reminderDate to set
     */
    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }

    public boolean hasReminder() {
        return this.reminderDate != null;
    }

    /**
     * @return the participationTracking
     */
    public boolean isParticipationTracking() {
        return participationTracking;
    }

    /**
     * @param participationTracking the participationTracking to set
     */
    public void setParticipationTracking(boolean participationTracking) {
        this.participationTracking = participationTracking;
    }

    /**
     * @return the organizers
     */
    public List<OrganizerTO> getOrganizers() {
        return organizers;
    }

    /**
     * @param organizers the organizers to set
     */
    public void setOrganizers(List<OrganizerTO> organizers) {
        this.organizers = organizers;
    }

    public void addOrganizer(OrganizerTO organizer) {
        if (!organizers.contains(organizer)) {
            organizers.add(organizer);
        }
    }

    public void removeOrganizer(OrganizerTO organizer) {
        organizers.remove(organizer);
    }

    /**
     * @return the items
     */
    public List<ItemTO> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<ItemTO> items) {
        this.items = items;
    }

    public void createItem() {
        ItemTO item = new ItemTO();
        item.setType(ItemType.YES_NO);
        item.setDecisionMode(DecisionMode.SIMPLE_MAJORITY);
        item.getOptions().clear();
        item.createOption();
        item.createOption();
        items.add(item);
    }

    public void removeItem(ItemTO item) {
        items.remove(item);
    }

    /**
     * @return the participants
     */
    public List<ParticipantTO> getParticipants() {
        return participants;
    }

    /**
     * @param participants the participants to set
     */
    public void setParticipants(List<ParticipantTO> participants) {
        this.participants = participants;
    }

    public void addParticipant(ParticipantTO participant) {
        if (!participants.contains(participant)) {
            participants.add(participant);
        }
    }

    public void removeParticipant(ParticipantTO participant) {
        participants.remove(participant);
    }

    /**
     * @return the creator
     */
    public OrganizerTO getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(OrganizerTO creator) {
        this.creator = creator;
        if (!this.getOrganizers().contains(creator)) {
            this.getOrganizers().add(creator);
        }
    }

    /**
     * @return the versionId
     */
    public long getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }
}
