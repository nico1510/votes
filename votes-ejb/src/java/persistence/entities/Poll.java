package persistence.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import logic.to.PollTO;

/**
 * @author adaudrich
 */
@Entity
public class Poll extends AbstractEntity<Poll, PollTO> {

    /**
     *
     */
    private static final long serialVersionUID = -5788002176391884277L;
    private long versionId;
    private String title;
    private String description;
    private PollState pollState;
    private Date startDate;
    private Date endDate;
    private Date reminderDate;
    private String masterToken;
    private boolean participationTracking;
    private Organizer creator;
    private List<Organizer> organizers;
    private List<Item> items;
    private List<Participant> participants;
    private List<Token> tokens;

    public Poll() {
        organizers = new ArrayList<Organizer>();
        items = new ArrayList<Item>();
        participants = new ArrayList<Participant>();
        tokens = new ArrayList<Token>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    @Lob
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    public PollState getPollState() {
        return pollState;
    }

    public void setPollState(PollState pollState) {
        this.pollState = pollState;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    /**
     * @return the reminderDate
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getReminderDate() {
        return reminderDate;
    }

    /**
     * @param reminderDate the reminderDate to set
     */
    public void setReminderDate(Date reminderDate) {
        this.reminderDate = reminderDate;
    }
    
    public boolean isParticipationTracking() {
        return participationTracking;
    }

    public void setParticipationTracking(boolean participationTracking) {
        this.participationTracking = participationTracking;
    }
    
    /**
     * @return the masterToken
     */
    public String getMasterToken() {
        return masterToken;
    }

    /**
     * @param masterToken the masterToken to set
     */
    public void setMasterToken(String masterToken) {
        this.masterToken = masterToken;
    }
    
    /**
     * @return the creator
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    public Organizer getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(Organizer creator) {
        this.creator = creator;
    }
    
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "POLL_ORGANIZERS")
    public List<Organizer> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(List<Organizer> organizers) {
        this.organizers = organizers;
    }

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval= true)
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval= true)
    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval= true)
    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * @return the versionId
     */
    @Version
    public long getVersionId() {
        return versionId;
    }

    /**
     * @param versionId the versionId to set
     */
    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    @Override
    public PollTO createTO() {
        PollTO pollTO = new PollTO();
        pollTO.setId(getId());
        pollTO.setDescription(getDescription());
        pollTO.setEnd(getEndDate());
        pollTO.setStart(getStartDate());
        pollTO.setReminderDate(getReminderDate());
        pollTO.setItems(createTransferList(getItems()));
        pollTO.setOrganizers(createTransferList(getOrganizers()));
        pollTO.setCreator(getCreator().createTO());
        pollTO.setParticipants(createTransferList(getParticipants()));
        pollTO.setParticipationTracking(isParticipationTracking());
        pollTO.setState(getPollState());
        pollTO.setTitle(getTitle());
        pollTO.setVersionId(getVersionId());

        return pollTO;
    }

}
