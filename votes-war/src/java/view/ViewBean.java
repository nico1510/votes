/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import Exceptions.VotesException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import logic.VotesLogic;
import logic.to.ItemOptionTO;
import logic.to.ItemTO;
import logic.to.ParticipantTO;
import logic.to.PollTO;
import login.UserBean;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import persistence.entities.ItemType;

/**
 *
 * @author nico
 */
@Named(value = "viewBean")
@ViewScoped
public class ViewBean {

    @EJB
    VotesLogic votesLogic;
    @Inject
    UserBean userBean;

    private PollTO poll;
    private String masterToken;
    private ResourceBundle captionsBundle;
    private ResourceBundle messageBundle;
    private HashMap<Long, ArrayList<ChartModel>> chartMap = new HashMap<Long, ArrayList<ChartModel>>();
    private HashMap<Long, String> selectedOptionTextMap = new HashMap<Long, String>();
    private boolean isPollOrganizer;

    /**
     * If the URL contains the request parameter token, load the poll linked to
     * this token.
     */
    @PostConstruct
    public void init() {

        FacesContext context = FacesContext.getCurrentInstance();
        Flash flash = context.getExternalContext().getFlash();

        // load resource bundles
        captionsBundle = context.getApplication().getResourceBundle(context, "cpts");
        messageBundle = context.getApplication().getResourceBundle(context, "msgs");

        if (flash.containsKey("pollId")) {
            Long pollId = (Long) flash.get("pollId");
            poll = votesLogic.getPoll(pollId);
            if (poll==null) {
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString("overview.pollDeleted"), "");
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                return;
            }
        } else if (context.getExternalContext().getRequestParameterMap().containsKey("pollid")) {
            // this is for voters who got here with a masterToken link
            masterToken = context.getExternalContext().getRequestParameterMap().get("pollid");
            try {
                poll = votesLogic.getPollByMasterToken(masterToken);

            } catch (VotesException ex) {
                Logger.getLogger(ViewBean.class.getName()).log(Level.SEVERE, null, ex);
                FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageBundle.getString(ex.getMessage()), "");
                FacesContext.getCurrentInstance().addMessage(null, facesMsg);
                return;
            }
        } else {
            return;
        }
        initDescriptions();
        initCharts();
    }

    private void initDescriptions() {
        for(ItemTO item : poll.getItems()) {
            getSelectedOptionTextMap().put(item.getId(), item.getOptions().get(0).getDescription());
        }
    }
    
    public void selectOptionText(Long itemId, String optionDescription) {
        getSelectedOptionTextMap().put(itemId, optionDescription);
    }

    /**
     * @return the poll
     */
    public PollTO getPoll() {
        return poll;
    }

    /**
     * @param poll the poll to set
     */
    public void setPoll(PollTO poll) {
        this.poll = poll;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return masterToken;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.masterToken = token;
    }

    public boolean getIsPollOrganizer() {
        return poll.getOrganizers().contains(userBean.getOrganizer());
    }

    /**
     * @param isPollOrganizer the isPollOrganizer to set
     */
    public void setIsPollOrganizer(boolean isPollOrganizer) {
        this.isPollOrganizer = isPollOrganizer;
    }

    public double computeTurnout(ItemTO item) {
        int voters = poll.getParticipants().size();
        int abstentions = item.getAbstentions();
        return Math.round(10000 * (voters - abstentions) / (double) voters) / 100.0;
    }

    private void initCharts() {

        for (ItemTO item : poll.getItems()) {
            BarChartModel bar = new BarChartModel();
            PieChartModel pie = new PieChartModel();

            List<ItemOptionTO> options = new ArrayList<ItemOptionTO>(item.getOptions());
            Collections.sort(options, Collections.reverseOrder());
            int abstentions = item.getAbstentions();

            // uncomment to include abstentions in the charts 
//            ChartSeries abstentionSeries = new ChartSeries();
//            abstentionSeries.setLabel(captionsBundle.getString("view.abstentions")+" : "+abstentions);
//            pie.set(captionsBundle.getString("view.abstentions")+" : "+abstentions, abstentions);
//            abstentionSeries.set(captionsBundle.getString("view.votes"), abstentions);
//            bar.addSeries(abstentionSeries);
            for (ItemOptionTO option : options) {
                int votes = option.getVotes();
                ChartSeries optionSeries = new ChartSeries();
                optionSeries.setLabel(option.getShortName() + " : " + votes);
                optionSeries.set(captionsBundle.getString("view.votes"), votes);
                bar.addSeries(optionSeries);
                pie.set(option.getShortName() + " : " + votes, votes);
            }

            pie.setShowDataLabels(true);
            pie.setShadow(true);
            pie.setLegendPosition("ne");

            bar.setAnimate(true);
            bar.setLegendPosition("ne");
            bar.setShadow(true);

            ArrayList<ChartModel> charts = new ArrayList<>();
            charts.add(bar);
            charts.add(pie);

            getChartMap().put(item.getId(), charts);
        }
    }

    /**
     * @return the chartMap
     */
    public HashMap<Long, ArrayList<ChartModel>> getChartMap() {
        return chartMap;
    }

    /**
     * @param chartMap the chartMap to set
     */
    public void setChartMap(HashMap<Long, ArrayList<ChartModel>> chartMap) {
        this.chartMap = chartMap;
    }

    public String restartPoll() {
        PollTO newPoll = new PollTO();
        newPoll.setTitle(poll.getTitle());
        newPoll.setCreator(poll.getCreator());
        newPoll.setOrganizers(poll.getOrganizers());
        newPoll.setDescription(poll.getDescription());
        newPoll.setParticipants(poll.getParticipants());
        newPoll.setParticipationTracking(poll.isParticipationTracking());
        newPoll.setStart(new Date());
        for (ItemTO item : poll.getItems()) {
            if (item.getWinner() == null) {
                ItemTO newItem = new ItemTO();
                newItem.setDecisionMode(item.getDecisionMode());
                newItem.setType(item.getType());
                if (item.getType() == ItemType.M_OF_N) {
                    newItem.setM(item.getM());
                }
                newItem.setTitle(item.getTitle());
                for (ItemOptionTO option : item.getOptions()) {
                    ItemOptionTO newOption = new ItemOptionTO();
                    newOption.setShortName(option.getShortName());
                    newOption.setDescription(option.getDescription());
                    newItem.getOptions().add(newOption);
                }
                newPoll.getItems().add(newItem);
            }
        }
        for (ParticipantTO participant : poll.getParticipants()) {
            ParticipantTO newParticipant = new ParticipantTO();
            newParticipant.setEmail(participant.getEmail());
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("poll", newPoll);
        return "organizer/edit.xhtml?faces-redirect=true";
    }

    /**
     * @return the selectedOptionTextMap
     */
    public HashMap<Long, String> getSelectedOptionTextMap() {
        return selectedOptionTextMap;
    }

    /**
     * @param selectedOptionTextMap the selectedOptionTextMap to set
     */
    public void setSelectedOptionTextMap(HashMap<Long, String> selectedOptionTextMap) {
        this.selectedOptionTextMap = selectedOptionTextMap;
    }
}
