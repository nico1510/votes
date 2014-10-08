/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.impl;

import Exceptions.VotesException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import logic.VotesLogic;
import logic.to.ItemOptionTO;
import logic.to.ItemTO;
import logic.to.OrganizerTO;
import logic.to.ParticipantTO;
import logic.to.PollTO;
import persistence.PollAccess;
import persistence.entities.DecisionMode;
import persistence.entities.Item;
import persistence.entities.ItemOption;
import persistence.entities.ItemType;
import persistence.entities.Participant;
import persistence.entities.Poll;
import persistence.entities.PollState;
import persistence.entities.Token;

/**
 *
 * @author nico
 */
@Stateless
@LocalBean
public class TestDataGenerator implements Serializable {

    @EJB
    VotesLogic votesLogic;
    @EJB
    PollAccess pollAccess;
    @EJB
    PollLifecycleBean pollLifecycleBean;

    public void generateFinishedPoll() {

        try {
            PollTO poll = new PollTO();
            
            poll.setTitle("Direkt-/Wiederholungswahlen im September 2014");
            poll.setDescription(" Nach Mitteilung der örtlichen Wahlleiter finden im September 2014 in den nachfolgend aufgeführten 24 Gebietskörperschaften "
                    + "Direktwahlen sowie ggf. Stichwahlen statt.\n"
                    + "In insgesamt 14 Fällen handelt es sich dabei um so genannte Wiederholungswahlen; in den übrigen Fällen finden die Direktwahlen "
                    + "wegen Ablaufs der Amtszeit oder Ausscheidens der Amtsinhaber statt. Wiederholungswahlen sind durchzuführen, wenn zu der ursprünglichen "
                    + "Wahl - hier am 25. Mai 2014 - nur eine Bewerbung eingereicht, der Bewerber aber nicht gewählt worden ist.");
            Date startDate = new Date(System.currentTimeMillis() - 3000000);
            poll.setStart(startDate);
            Date endDate = new Date(System.currentTimeMillis() - 100000);
            poll.setEnd(endDate);
            poll.setParticipationTracking(false);
            
            OrganizerTO organizer = votesLogic.getOrganizerByUsername("nico1510");
            OrganizerTO organizer2 = votesLogic.getOrganizerByUsername("adaudrich");
            
            poll.getOrganizers().add(organizer);
            poll.getOrganizers().add(organizer2);
            poll.setCreator(organizer);
            
            ItemTO item = new ItemTO();
            
            item.setTitle("Kommunalwahlen 2014");
            item.setType(ItemType.ONE_OF_N);
            item.setDecisionMode(DecisionMode.ABS_MAJORITY);
            
            ItemOptionTO itemOption1 = new ItemOptionTO();
            itemOption1.setDescription("Die Sozialdemokratische Partei Deutschlands (Kurzbezeichnung: SPD) ist eine deutsche Volkspartei und die älteste "
                    + "parlamentarisch vertretene Partei Deutschlands. Als erste Vorläufer der Partei gelten der 1863 gegründete Allgemeine Deutsche "
                    + "Arbeiterverein und die 1869 gegründete Sozialdemokratische Arbeiterpartei. Sie ist derzeit in insgesamt dreizehn Ländern an der "
                    + "Regierung beteiligt, in neun davon stellt sie den Regierungschef. Auf Bundesebene ist sie in einer Koalition mit den Unionsparteien "
                    + "an der Bundesregierung beteiligt.");
            itemOption1.setShortName("SPD");
            
            ItemOptionTO itemOption2 = new ItemOptionTO();
            itemOption2.setDescription("Die Christlich Demokratische Union Deutschlands (Kurzbezeichnung: CDU) ist eine deutsche Partei. "
                    + "Sie bezeichnet sich als christlich-soziale, liberale sowie wertkonservative Volkspartei[5] und stellt mit ihrer Bundesvorsitzenden "
                    + "Angela Merkel die Bundeskanzlerin der Bundesrepublik Deutschland. Mit der bayerischen Schwesterpartei CSU bildet die CDU im Bundestag eine "
                    + "Fraktionsgemeinschaft. Seit 15. Juni 2014 ist sie auf Bundesebene insgesamt länger in Regierungsverantwortung als jede andere deutsche Partei "
                    + "seit Gründung der Bundesrepublik. Sie ist derzeit in insgesamt sieben Ländern an der Regierung beteiligt, in fünf davon stellt sie den Regierungschef.");
            itemOption2.setShortName("CDU");
            
            ItemOptionTO itemOption3 = new ItemOptionTO();
            itemOption3.setShortName("GRÜNE");
            itemOption3.setDescription("Bündnis 90/Die Grünen (Kurzbezeichnung: Grüne, auch als Bündnisgrüne oder B’90/Grüne bezeichnet) ist eine politische Partei in Deutschland. "
                    + "Ein wesentlicher inhaltlicher Schwerpunkt ist die Umweltpolitik. Leitgedanke grüner Politik ist ökologische, ökonomische und soziale Nachhaltigkeit.");
            ItemOptionTO itemOption4 = new ItemOptionTO();
            itemOption4.setShortName("FDP");
            itemOption4.setDescription("Die Freie Demokratische Partei (Kurzbezeichnung: FDP, von 1968 bis 2001 F.D.P.)[5] ist eine liberale Partei in Deutschland, die ihre "
                    + "politischen Wurzeln in der Bewegung des Vormärz sieht.");
            ItemOptionTO itemOption5 = new ItemOptionTO();
            itemOption5.setShortName("AFD");
            itemOption5.setDescription("Die Alternative für Deutschland (Kurzbezeichnung: AfD) ist eine deutsche Partei, die am 6. Februar 2013 gegründet wurde und bei der "
                    + "Bundestagswahl 2013 und der Landtagswahl in Hessen 2013 erstmals an Wahlen teilnahm. Nach der Europawahl 2014 stellt sie erstmals überregionale "
                    + "Mandatsträger und zog bei der Landtagswahl in Sachsen 2014 erstmals in ein Landesparlament ein. Parteisprecher sind die Bundesvorstandsmitglieder "
                    + "Konrad Adam, Bernd Lucke und Frauke Petry.");
            ItemOptionTO itemOption6 = new ItemOptionTO();
            itemOption6.setShortName("LINKE");
            itemOption6.setDescription("Die Linke (Lang- und Kurzbezeichnung in Eigenschreibweise: DIE LINKE)[6] ist eine politische Partei in Deutschland, die am 16. Juni 2007 durch Verschmelzung"
                    + " von WASG und Linkspartei.PDS entstand. Sie leitet ihren Namen aus dem Anspruch einer linken politischen Orientierung her und zielt auf die Überwindung "
                    + "des Kapitalismus hin zu einem „demokratischen Sozialismus");
            item.getOptions().add(itemOption1);
            item.getOptions().add(itemOption2);
            item.getOptions().add(itemOption3);
            item.getOptions().add(itemOption4);
            item.getOptions().add(itemOption5);
            item.getOptions().add(itemOption6);
            
            ItemTO item2 = new ItemTO();
            item2.setTitle("Soll die Todesstrafe abgeschafft werden ?");
            ItemOptionTO option12 = new ItemOptionTO();
            option12.setShortName("Nein");
            option12.setDescription("Unleugbar haben schwere Verbrechen wie Entführung und Geiseltötung, hat überhaupt Gewalttätigkeit während der letzten Jahre in der westlichen Welt ständig zugenommen. "
                    + "Dagegen muß mit aller Schärfe vorgegangen werden. In den autoritären Staaten, in denen es die Todesstrafe gab oder noch gibt, herrschen Gesetz und Ordnung zum allgemeinen Wohle stärker "
                    + "als in den liberalen Demokratien.");
            ItemOptionTO option22 = new ItemOptionTO();
            option22.setShortName("Ja");
            option22.setDescription("Die Gewalttätigkeit in der Welt wird dadurch nicht verringert, "
                    + "daß man sie vermehrt um die staatliche Gewalt der Armeen, der Polizei und der Henker. Auf die wichtige Frage, ob die Todesstrafe abschreckend wirke, kann es keine wissenschaftlich exakte Antwort geben. "
                    + "Der „normale“ Bürger ist überzeugt von dieser Abschreckungskraft – denn auf ihn selber würde sie abschreckend wirken. Der Kriminologe weiß aus empirischen Untersuchungen, daß die meisten Kriminellentypen "
                    + "vom Risiko der Todesstrafe so wenig abgeschreckt werden wie die meisten Autofahrer durch die Verkehrsunfallstatistiken. Trotz Todesstrafe waren die USA das Land mit den meisten Gewaltverbrechen innerhalb "
                    + "der zivilisierten Welt. Auch ohne Todesstrafe war die Sowjetunion ein Land mit bemerkenswert niedriger Kriminalitätsrate. Daran hat sich nichts geändert, als vor einiger Zeit die Todesstrafe in den USA "
                    + "abgeschafft, in der Sowjetunion wieder eingeführt wurde. Daran wird sich aller Voraussicht nach nichts ändern, wenn demnächst, wie angekündigt, die Todesstrafe in den USA wieder eingeführt, in der Sowjetunion wieder abgeschafft wird.");
            item2.getOptions().add(option12);
            item2.getOptions().add(option22);
            item2.setType(ItemType.YES_NO);
            item2.setDecisionMode(DecisionMode.SIMPLE_MAJORITY);
            
            poll.getItems().add(item);
            poll.getItems().add(item2);
            
            for (int i = 0; i < 500; i++) {
                ParticipantTO participant = new ParticipantTO();
                participant.setEmail(UUID.randomUUID().toString().replace("-", "") + "@example.com");
                poll.getParticipants().add(participant);
            }
            votesLogic.savePoll(poll);
            
            List<Poll> polls = pollAccess.findAll();
            
            Poll savedPoll = null;
            for (Poll p : polls) {
                if (p.getTitle().equals(poll.getTitle())) {
                    savedPoll = p;
                }
            }
            
            List<Item> savedItems = savedPoll.getItems();
            Item savedItem = savedItems.get(0);
            ItemOption opt1 = savedItem.getOptions().get(0);
            ItemOption opt2 = savedItem.getOptions().get(1);
            ItemOption opt3 = savedItem.getOptions().get(2);
            ItemOption opt4 = savedItem.getOptions().get(3);
            ItemOption opt5 = savedItem.getOptions().get(4);
            ItemOption opt6 = savedItem.getOptions().get(5);
            
            savedItem.setAbstentions(5);    // real 150
            opt1.setVotes(150);
            opt2.setVotes(100);
            opt3.setVotes(50);
            opt4.setVotes(25);
            opt5.setVotes(15);
            opt6.setVotes(10);
            
            Item savedItem2 = savedItems.get(1);
            ItemOption opt12 = savedItem2.getOptions().get(0);
            ItemOption opt22 = savedItem2.getOptions().get(1);
            
            savedItem2.setAbstentions(5);   // real 50
            opt12.setVotes(250);
            opt22.setVotes(200);
            pollAccess.edit(savedPoll);
            
            pollLifecycleBean.handleVoteFinished(savedPoll.getId());
        } catch (VotesException ex) {
            Logger.getLogger(TestDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void generatePublishedPoll() {

        try {
            PollTO poll = new PollTO();
            
            poll.setTitle("Published Test Poll abc");
            poll.setDescription(" Nach Mitteilung der örtlichen Wahlleiter finden im September 2014 in den nachfolgend aufgeführten 24 Gebietskörperschaften "
                    + "Direktwahlen sowie ggf. Stichwahlen statt.\n"
                    + "In insgesamt 14 Fällen handelt es sich dabei um so genannte Wiederholungswahlen; in den übrigen Fällen finden die Direktwahlen "
                    + "wegen Ablaufs der Amtszeit oder Ausscheidens der Amtsinhaber statt. Wiederholungswahlen sind durchzuführen, wenn zu der ursprünglichen "
                    + "Wahl - hier am 25. Mai 2014 - nur eine Bewerbung eingereicht, der Bewerber aber nicht gewählt worden ist.");
            Date startDate = new Date(System.currentTimeMillis() - 3000000);
            poll.setStart(startDate);
            Date endDate = new Date(System.currentTimeMillis() - 100000);
            poll.setEnd(endDate);
            poll.setParticipationTracking(false);
            
            OrganizerTO organizer = votesLogic.getOrganizerByUsername("nico1510");
            OrganizerTO organizer2 = votesLogic.getOrganizerByUsername("adaudrich");
            
            poll.getOrganizers().add(organizer);
            poll.getOrganizers().add(organizer2);
            poll.setCreator(organizer);
            
            ItemTO item = new ItemTO();
            
            item.setTitle("Kommunalwahlen 2014");
            item.setType(ItemType.ONE_OF_N);
            item.setDecisionMode(DecisionMode.ABS_MAJORITY);
            
            ItemOptionTO itemOption1 = new ItemOptionTO();
            itemOption1.setDescription("Die Sozialdemokratische Partei Deutschlands (Kurzbezeichnung: SPD) ist eine deutsche Volkspartei und die älteste "
                    + "parlamentarisch vertretene Partei Deutschlands. Als erste Vorläufer der Partei gelten der 1863 gegründete Allgemeine Deutsche "
                    + "Arbeiterverein und die 1869 gegründete Sozialdemokratische Arbeiterpartei. Sie ist derzeit in insgesamt dreizehn Ländern an der "
                    + "Regierung beteiligt, in neun davon stellt sie den Regierungschef. Auf Bundesebene ist sie in einer Koalition mit den Unionsparteien "
                    + "an der Bundesregierung beteiligt.");
            itemOption1.setShortName("SPD");
            
            ItemOptionTO itemOption2 = new ItemOptionTO();
            itemOption2.setDescription("Die Christlich Demokratische Union Deutschlands (Kurzbezeichnung: CDU) ist eine deutsche Partei. "
                    + "Sie bezeichnet sich als christlich-soziale, liberale sowie wertkonservative Volkspartei[5] und stellt mit ihrer Bundesvorsitzenden "
                    + "Angela Merkel die Bundeskanzlerin der Bundesrepublik Deutschland. Mit der bayerischen Schwesterpartei CSU bildet die CDU im Bundestag eine "
                    + "Fraktionsgemeinschaft. Seit 15. Juni 2014 ist sie auf Bundesebene insgesamt länger in Regierungsverantwortung als jede andere deutsche Partei "
                    + "seit Gründung der Bundesrepublik. Sie ist derzeit in insgesamt sieben Ländern an der Regierung beteiligt, in fünf davon stellt sie den Regierungschef.");
            itemOption2.setShortName("CDU");
            
            ItemOptionTO itemOption3 = new ItemOptionTO();
            itemOption3.setShortName("GRÜNE");
            itemOption3.setDescription("Bündnis 90/Die Grünen (Kurzbezeichnung: Grüne, auch als Bündnisgrüne oder B’90/Grüne bezeichnet) ist eine politische Partei in Deutschland. "
                    + "Ein wesentlicher inhaltlicher Schwerpunkt ist die Umweltpolitik. Leitgedanke grüner Politik ist ökologische, ökonomische und soziale Nachhaltigkeit.");
            ItemOptionTO itemOption4 = new ItemOptionTO();
            itemOption4.setShortName("FDP");
            itemOption4.setDescription("Die Freie Demokratische Partei (Kurzbezeichnung: FDP, von 1968 bis 2001 F.D.P.)[5] ist eine liberale Partei in Deutschland, die ihre "
                    + "politischen Wurzeln in der Bewegung des Vormärz sieht.");
            ItemOptionTO itemOption5 = new ItemOptionTO();
            itemOption5.setShortName("AFD");
            itemOption5.setDescription("Die Alternative für Deutschland (Kurzbezeichnung: AfD) ist eine deutsche Partei, die am 6. Februar 2013 gegründet wurde und bei der "
                    + "Bundestagswahl 2013 und der Landtagswahl in Hessen 2013 erstmals an Wahlen teilnahm. Nach der Europawahl 2014 stellt sie erstmals überregionale "
                    + "Mandatsträger und zog bei der Landtagswahl in Sachsen 2014 erstmals in ein Landesparlament ein. Parteisprecher sind die Bundesvorstandsmitglieder "
                    + "Konrad Adam, Bernd Lucke und Frauke Petry.");
            ItemOptionTO itemOption6 = new ItemOptionTO();
            itemOption6.setShortName("LINKE");
            itemOption6.setDescription("Die Linke (Lang- und Kurzbezeichnung in Eigenschreibweise: DIE LINKE)[6] ist eine politische Partei in Deutschland, die am 16. Juni 2007 durch Verschmelzung"
                    + " von WASG und Linkspartei.PDS entstand. Sie leitet ihren Namen aus dem Anspruch einer linken politischen Orientierung her und zielt auf die Überwindung "
                    + "des Kapitalismus hin zu einem „demokratischen Sozialismus");
            item.getOptions().add(itemOption1);
            item.getOptions().add(itemOption2);
            item.getOptions().add(itemOption3);
            item.getOptions().add(itemOption4);
            item.getOptions().add(itemOption5);
            item.getOptions().add(itemOption6);
            
            ItemTO item2 = new ItemTO();
            item2.setTitle("Soll die Todesstrafe abgeschafft werden ?");
            ItemOptionTO option12 = new ItemOptionTO();
            option12.setShortName("Nein");
            option12.setDescription("Unleugbar haben schwere Verbrechen wie Entführung und Geiseltötung, hat überhaupt Gewalttätigkeit während der letzten Jahre in der westlichen Welt ständig zugenommen. "
                    + "Dagegen muß mit aller Schärfe vorgegangen werden. In den autoritären Staaten, in denen es die Todesstrafe gab oder noch gibt, herrschen Gesetz und Ordnung zum allgemeinen Wohle stärker "
                    + "als in den liberalen Demokratien.");
            ItemOptionTO option22 = new ItemOptionTO();
            option22.setShortName("Ja");
            option22.setDescription("Die Gewalttätigkeit in der Welt wird dadurch nicht verringert, "
                    + "daß man sie vermehrt um die staatliche Gewalt der Armeen, der Polizei und der Henker. Auf die wichtige Frage, ob die Todesstrafe abschreckend wirke, kann es keine wissenschaftlich exakte Antwort geben. "
                    + "Der „normale“ Bürger ist überzeugt von dieser Abschreckungskraft – denn auf ihn selber würde sie abschreckend wirken. Der Kriminologe weiß aus empirischen Untersuchungen, daß die meisten Kriminellentypen "
                    + "vom Risiko der Todesstrafe so wenig abgeschreckt werden wie die meisten Autofahrer durch die Verkehrsunfallstatistiken. Trotz Todesstrafe waren die USA das Land mit den meisten Gewaltverbrechen innerhalb "
                    + "der zivilisierten Welt. Auch ohne Todesstrafe war die Sowjetunion ein Land mit bemerkenswert niedriger Kriminalitätsrate. Daran hat sich nichts geändert, als vor einiger Zeit die Todesstrafe in den USA "
                    + "abgeschafft, in der Sowjetunion wieder eingeführt wurde. Daran wird sich aller Voraussicht nach nichts ändern, wenn demnächst, wie angekündigt, die Todesstrafe in den USA wieder eingeführt, in der Sowjetunion wieder abgeschafft wird.");
            item2.getOptions().add(option12);
            item2.getOptions().add(option22);
            item2.setType(ItemType.YES_NO);
            item2.setDecisionMode(DecisionMode.SIMPLE_MAJORITY);
            
            poll.getItems().add(item);
            poll.getItems().add(item2);
            
            for (int i = 0; i < 500; i++) {
                ParticipantTO participant = new ParticipantTO();
                participant.setEmail(UUID.randomUUID().toString().replace("-", "") + "@example.com");
                poll.getParticipants().add(participant);
            }
            votesLogic.savePoll(poll);
            
            List<Poll> polls = pollAccess.findAll();
            
            Poll savedPoll = null;
            for (Poll p : polls) {
                if (p.getTitle().equals(poll.getTitle())) {
                    savedPoll = p;
                }
            }
            
            savedPoll.setMasterToken("abc");
            List<Item> savedItems = savedPoll.getItems();
            Item savedItem = savedItems.get(0);
            ItemOption opt1 = savedItem.getOptions().get(0);
            ItemOption opt2 = savedItem.getOptions().get(1);
            ItemOption opt3 = savedItem.getOptions().get(2);
            ItemOption opt4 = savedItem.getOptions().get(3);
            ItemOption opt5 = savedItem.getOptions().get(4);
            ItemOption opt6 = savedItem.getOptions().get(5);
            
            savedItem.setAbstentions(5);    // real 150
            opt1.setVotes(150);
            opt2.setVotes(100);
            opt3.setVotes(50);
            opt4.setVotes(25);
            opt5.setVotes(15);
            opt6.setVotes(10);
            
            Item savedItem2 = savedItems.get(1);
            ItemOption opt12 = savedItem2.getOptions().get(0);
            ItemOption opt22 = savedItem2.getOptions().get(1);
            
            savedItem2.setAbstentions(5);   // real 50
            opt12.setVotes(250);
            opt22.setVotes(200);
            pollAccess.edit(savedPoll);
            
            pollLifecycleBean.handleVoteFinished(savedPoll.getId());
            pollAccess.updateState(savedPoll.getId(), PollState.PUBLISHED);
        } catch (VotesException ex) {
            Logger.getLogger(TestDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void generatePreparedPoll() {
        try {
            PollTO poll = new PollTO();
            
            poll.setTitle("Testpoll");
            poll.setDescription("Test description");
            Date startDate = new Date(System.currentTimeMillis() + 120000);
            poll.setStart(startDate);
            Logger.getLogger(VotesLogicImpl.class.getName()).log(Level.INFO, "Poll starts at " + startDate.toString());
            Date endDate = new Date(System.currentTimeMillis() + 240000);
            poll.setEnd(endDate);
            Logger.getLogger(VotesLogicImpl.class.getName()).log(Level.INFO, "Poll ends at " + endDate.toString());
            poll.setParticipationTracking(true);
            poll.setState(PollState.PREPARED);
            
            OrganizerTO organizer = votesLogic.getOrganizerByUsername("nico1510");
            poll.getOrganizers().add(organizer);
            
            poll.setCreator(organizer);
            
            ItemTO item = new ItemTO();
            item.setDecisionMode(DecisionMode.SIMPLE_MAJORITY);
            
            item.setTitle("Pommes Tag abschaffen ?");
            item.setType(ItemType.YES_NO);
            
            ItemOptionTO itemOption1 = new ItemOptionTO();
            itemOption1.setDescription("Ja bitte abschaffen");
            itemOption1.setShortName("Yes");
            
            ItemOptionTO itemOption2 = new ItemOptionTO();
            itemOption2.setDescription("Nein nicht abschaffen");
            itemOption2.setShortName("No");
            
            item.getOptions().add(itemOption1);
            item.getOptions().add(itemOption2);
            
            poll.getItems().add(item);
            
            ParticipantTO participant = new ParticipantTO();
            participant.setEmail("nico1510@uni-koblenz.de");
            
            poll.getParticipants().add(participant);
            
            votesLogic.savePoll(poll);
        } catch (VotesException ex) {
            Logger.getLogger(TestDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateProhibitedPoll() {
        try {
            PollTO poll = new PollTO();
            
            poll.setTitle("Abstimmung über JSF und Bootstrap");
            poll.setDescription("Test Beschreibung für einen Prohibited Poll. Die Ergebnisse dürfen nicht "
                    + "vom Organizer oder Admin eingesehen werden.");
            Date startDate = new Date(System.currentTimeMillis() - 3000000);
            poll.setStart(startDate);
            Date endDate = new Date(System.currentTimeMillis() - 100000);
            poll.setEnd(endDate);
            poll.setParticipationTracking(true);
            
            OrganizerTO organizer = votesLogic.getOrganizerByUsername("nico1510");
            OrganizerTO organizer2 = votesLogic.getOrganizerByUsername("adaudrich");
            
            poll.getOrganizers().add(organizer);
            poll.getOrganizers().add(organizer2);
            poll.setCreator(organizer);
            
            ItemTO item = new ItemTO();
            
            item.setTitle("Harmonieren JSF und Bootstrap gut miteinander ?");
            item.setType(ItemType.ONE_OF_N);
            item.setDecisionMode(DecisionMode.ABS_MAJORITY);
            
            ItemOptionTO itemOption1 = new ItemOptionTO();
            itemOption1.setDescription("Nein überhaupt nicht");
            itemOption1.setShortName("Nein");
            
            ItemOptionTO itemOption2 = new ItemOptionTO();
            itemOption2.setDescription("Es geht so");
            itemOption2.setShortName("Mittel");
            
            ItemOptionTO itemOption3 = new ItemOptionTO();
            itemOption3.setShortName("Ja");
            itemOption3.setDescription("Ja es wurde nie eine bessere Kombination erfunden");
            
            item.getOptions().add(itemOption1);
            item.getOptions().add(itemOption2);
            item.getOptions().add(itemOption3);
            
            poll.getItems().add(item);
            
            for (int i = 0; i < 10; i++) {
                ParticipantTO participant = new ParticipantTO();
                participant.setEmail(UUID.randomUUID().toString().replace("-", "") + "@example.com");
                poll.getParticipants().add(participant);
            }
            votesLogic.savePoll(poll);
            
            List<Poll> polls = pollAccess.findAll();
            
            Poll savedPoll = null;
            for (Poll p : polls) {
                if (p.getTitle().equals(poll.getTitle())) {
                    savedPoll = p;
                }
            }
            
            List<Item> savedItems = savedPoll.getItems();
            Item savedItem = savedItems.get(0);
            ItemOption opt1 = savedItem.getOptions().get(0);
            
            savedItem.setAbstentions(5);    // real 5
            opt1.setVotes(5);
            
            pollAccess.edit(savedPoll);
            
            pollLifecycleBean.handleVoteFinished(savedPoll.getId());
        } catch (VotesException ex) {
            Logger.getLogger(TestDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateStartedPoll() {
        
        try {
            PollTO poll = new PollTO();
            
            poll.setTitle("Bundestagswahlen");
            poll.setDescription("Test Beschreibung für einen Started Poll der niemals endet.");
            Date startDate = new Date(System.currentTimeMillis());
            poll.setStart(startDate);
            Date endDate = new Date(System.currentTimeMillis() - 300000);
            poll.setEnd(endDate);
            poll.setParticipationTracking(true);
            
            OrganizerTO organizer = votesLogic.getOrganizerByUsername("nico1510");
            OrganizerTO organizer2 = votesLogic.getOrganizerByUsername("adaudrich");
            
            poll.getOrganizers().add(organizer);
            poll.getOrganizers().add(organizer2);
            poll.setCreator(organizer);
            
            ItemTO item = new ItemTO();
            
            item.setTitle("Bundestagswahlen");
            item.setType(ItemType.M_OF_N);
            item.setM(2);
            item.setDecisionMode(DecisionMode.ABS_MAJORITY);
            item.getOptions().clear();
            
            ItemOptionTO itemOption1 = new ItemOptionTO();
            itemOption1.setDescription("Die Sozialdemokratische Partei Deutschlands (Kurzbezeichnung: SPD) ist eine deutsche Volkspartei und die älteste "
                    + "parlamentarisch vertretene Partei Deutschlands. Als erste Vorläufer der Partei gelten der 1863 gegründete Allgemeine Deutsche "
                    + "Arbeiterverein und die 1869 gegründete Sozialdemokratische Arbeiterpartei. Sie ist derzeit in insgesamt dreizehn Ländern an der "
                    + "Regierung beteiligt, in neun davon stellt sie den Regierungschef. Auf Bundesebene ist sie in einer Koalition mit den Unionsparteien "
                    + "an der Bundesregierung beteiligt.");
            itemOption1.setShortName("SPD");
            
            ItemOptionTO itemOption2 = new ItemOptionTO();
            itemOption2.setDescription("Die Christlich Demokratische Union Deutschlands (Kurzbezeichnung: CDU) ist eine deutsche Partei. "
                    + "Sie bezeichnet sich als christlich-soziale, liberale sowie wertkonservative Volkspartei[5] und stellt mit ihrer Bundesvorsitzenden "
                    + "Angela Merkel die Bundeskanzlerin der Bundesrepublik Deutschland. Mit der bayerischen Schwesterpartei CSU bildet die CDU im Bundestag eine "
                    + "Fraktionsgemeinschaft. Seit 15. Juni 2014 ist sie auf Bundesebene insgesamt länger in Regierungsverantwortung als jede andere deutsche Partei "
                    + "seit Gründung der Bundesrepublik. Sie ist derzeit in insgesamt sieben Ländern an der Regierung beteiligt, in fünf davon stellt sie den Regierungschef.");
            itemOption2.setShortName("CDU");
            
            ItemOptionTO itemOption3 = new ItemOptionTO();
            itemOption3.setShortName("GRÜNE");
            itemOption3.setDescription("Bündnis 90/Die Grünen (Kurzbezeichnung: Grüne, auch als Bündnisgrüne oder B’90/Grüne bezeichnet) ist eine politische Partei in Deutschland. "
                    + "Ein wesentlicher inhaltlicher Schwerpunkt ist die Umweltpolitik. Leitgedanke grüner Politik ist ökologische, ökonomische und soziale Nachhaltigkeit.");
            ItemOptionTO itemOption4 = new ItemOptionTO();
            itemOption4.setShortName("FDP");
            itemOption4.setDescription("Die Freie Demokratische Partei (Kurzbezeichnung: FDP, von 1968 bis 2001 F.D.P.)[5] ist eine liberale Partei in Deutschland, die ihre "
                    + "politischen Wurzeln in der Bewegung des Vormärz sieht.");
            ItemOptionTO itemOption5 = new ItemOptionTO();
            itemOption5.setShortName("AFD");
            itemOption5.setDescription("Die Alternative für Deutschland (Kurzbezeichnung: AfD) ist eine deutsche Partei, die am 6. Februar 2013 gegründet wurde und bei der "
                    + "Bundestagswahl 2013 und der Landtagswahl in Hessen 2013 erstmals an Wahlen teilnahm. Nach der Europawahl 2014 stellt sie erstmals überregionale "
                    + "Mandatsträger und zog bei der Landtagswahl in Sachsen 2014 erstmals in ein Landesparlament ein. Parteisprecher sind die Bundesvorstandsmitglieder "
                    + "Konrad Adam, Bernd Lucke und Frauke Petry.");
            ItemOptionTO itemOption6 = new ItemOptionTO();
            itemOption6.setShortName("LINKE");
            itemOption6.setDescription("Die Linke (Lang- und Kurzbezeichnung in Eigenschreibweise: DIE LINKE)[6] ist eine politische Partei in Deutschland, die am 16. Juni 2007 durch Verschmelzung"
                    + " von WASG und Linkspartei.PDS entstand. Sie leitet ihren Namen aus dem Anspruch einer linken politischen Orientierung her und zielt auf die Überwindung "
                    + "des Kapitalismus hin zu einem „demokratischen Sozialismus");
            item.getOptions().add(itemOption1);
            item.getOptions().add(itemOption2);
            item.getOptions().add(itemOption3);
            item.getOptions().add(itemOption4);
            item.getOptions().add(itemOption5);
            item.getOptions().add(itemOption6);
            
            ItemTO item2 = new ItemTO();
            item2.setTitle("Soll die Todesstrafe abgeschafft werden ?");
            ItemOptionTO option12 = new ItemOptionTO();
            option12.setShortName("Nein");
            option12.setDescription("Unleugbar haben schwere Verbrechen wie Entführung und Geiseltötung, hat überhaupt Gewalttätigkeit während der letzten Jahre in der westlichen Welt ständig zugenommen. "
                    + "Dagegen muß mit aller Schärfe vorgegangen werden. In den autoritären Staaten, in denen es die Todesstrafe gab oder noch gibt, herrschen Gesetz und Ordnung zum allgemeinen Wohle stärker "
                    + "als in den liberalen Demokratien.");
            ItemOptionTO option22 = new ItemOptionTO();
            option22.setShortName("Ja");
            option22.setDescription("Die Gewalttätigkeit in der Welt wird dadurch nicht verringert, "
                    + "daß man sie vermehrt um die staatliche Gewalt der Armeen, der Polizei und der Henker. Auf die wichtige Frage, ob die Todesstrafe abschreckend wirke, kann es keine wissenschaftlich exakte Antwort geben. "
                    + "Der „normale“ Bürger ist überzeugt von dieser Abschreckungskraft – denn auf ihn selber würde sie abschreckend wirken. Der Kriminologe weiß aus empirischen Untersuchungen, daß die meisten Kriminellentypen "
                    + "vom Risiko der Todesstrafe so wenig abgeschreckt werden wie die meisten Autofahrer durch die Verkehrsunfallstatistiken. Trotz Todesstrafe waren die USA das Land mit den meisten Gewaltverbrechen innerhalb "
                    + "der zivilisierten Welt. Auch ohne Todesstrafe war die Sowjetunion ein Land mit bemerkenswert niedriger Kriminalitätsrate. Daran hat sich nichts geändert, als vor einiger Zeit die Todesstrafe in den USA "
                    + "abgeschafft, in der Sowjetunion wieder eingeführt wurde. Daran wird sich aller Voraussicht nach nichts ändern, wenn demnächst, wie angekündigt, die Todesstrafe in den USA wieder eingeführt, in der Sowjetunion wieder abgeschafft wird.");
            item2.getOptions().add(option12);
            item2.getOptions().add(option22);
            item2.setType(ItemType.YES_NO);
            item2.setDecisionMode(DecisionMode.SIMPLE_MAJORITY);
            
            
            ItemTO item3 = new ItemTO();
            
            item3.setTitle("Landtagswahlen");
            item3.setType(ItemType.ONE_OF_N);
            item3.setDecisionMode(DecisionMode.ABS_MAJORITY);
            
            ItemOptionTO itemOption13 = new ItemOptionTO();
            itemOption13.setDescription("Die Sozialdemokratische Partei Deutschlands (Kurzbezeichnung: SPD) ist eine deutsche Volkspartei und die älteste "
                    + "parlamentarisch vertretene Partei Deutschlands. Als erste Vorläufer der Partei gelten der 1863 gegründete Allgemeine Deutsche "
                    + "Arbeiterverein und die 1869 gegründete Sozialdemokratische Arbeiterpartei. Sie ist derzeit in insgesamt dreizehn Ländern an der "
                    + "Regierung beteiligt, in neun davon stellt sie den Regierungschef. Auf Bundesebene ist sie in einer Koalition mit den Unionsparteien "
                    + "an der Bundesregierung beteiligt.");
            itemOption13.setShortName("SPD");
            
            ItemOptionTO itemOption23 = new ItemOptionTO();
            itemOption23.setDescription("Die Christlich Demokratische Union Deutschlands (Kurzbezeichnung: CDU) ist eine deutsche Partei. "
                    + "Sie bezeichnet sich als christlich-soziale, liberale sowie wertkonservative Volkspartei[5] und stellt mit ihrer Bundesvorsitzenden "
                    + "Angela Merkel die Bundeskanzlerin der Bundesrepublik Deutschland. Mit der bayerischen Schwesterpartei CSU bildet die CDU im Bundestag eine "
                    + "Fraktionsgemeinschaft. Seit 15. Juni 2014 ist sie auf Bundesebene insgesamt länger in Regierungsverantwortung als jede andere deutsche Partei "
                    + "seit Gründung der Bundesrepublik. Sie ist derzeit in insgesamt sieben Ländern an der Regierung beteiligt, in fünf davon stellt sie den Regierungschef.");
            itemOption23.setShortName("CDU");
            
            ItemOptionTO itemOption33 = new ItemOptionTO();
            itemOption33.setShortName("GRÜNE");
            itemOption33.setDescription("Bündnis 90/Die Grünen (Kurzbezeichnung: Grüne, auch als Bündnisgrüne oder B’90/Grüne bezeichnet) ist eine politische Partei in Deutschland. "
                    + "Ein wesentlicher inhaltlicher Schwerpunkt ist die Umweltpolitik. Leitgedanke grüner Politik ist ökologische, ökonomische und soziale Nachhaltigkeit.");
            ItemOptionTO itemOption43 = new ItemOptionTO();
            itemOption43.setShortName("FDP");
            itemOption43.setDescription("Die Freie Demokratische Partei (Kurzbezeichnung: FDP, von 1968 bis 2001 F.D.P.)[5] ist eine liberale Partei in Deutschland, die ihre "
                    + "politischen Wurzeln in der Bewegung des Vormärz sieht.");
            ItemOptionTO itemOption53 = new ItemOptionTO();
            itemOption53.setShortName("AFD");
            itemOption53.setDescription("Die Alternative für Deutschland (Kurzbezeichnung: AfD) ist eine deutsche Partei, die am 6. Februar 2013 gegründet wurde und bei der "
                    + "Bundestagswahl 2013 und der Landtagswahl in Hessen 2013 erstmals an Wahlen teilnahm. Nach der Europawahl 2014 stellt sie erstmals überregionale "
                    + "Mandatsträger und zog bei der Landtagswahl in Sachsen 2014 erstmals in ein Landesparlament ein. Parteisprecher sind die Bundesvorstandsmitglieder "
                    + "Konrad Adam, Bernd Lucke und Frauke Petry.");
            ItemOptionTO itemOption63 = new ItemOptionTO();
            itemOption63.setShortName("LINKE");
            itemOption63.setDescription("Die Linke (Lang- und Kurzbezeichnung in Eigenschreibweise: DIE LINKE)[6] ist eine politische Partei in Deutschland, die am 16. Juni 2007 durch Verschmelzung"
                    + " von WASG und Linkspartei.PDS entstand. Sie leitet ihren Namen aus dem Anspruch einer linken politischen Orientierung her und zielt auf die Überwindung "
                    + "des Kapitalismus hin zu einem „demokratischen Sozialismus");
            item3.getOptions().add(itemOption13);
            item3.getOptions().add(itemOption23);
            item3.getOptions().add(itemOption33);
            item3.getOptions().add(itemOption43);
            item3.getOptions().add(itemOption53);
            item3.getOptions().add(itemOption63);
            
            poll.getItems().add(item);
            poll.getItems().add(item2);
            poll.getItems().add(item3);
            
            for (int i = 0; i < 10; i++) {
                ParticipantTO participant = new ParticipantTO();
                participant.setEmail(UUID.randomUUID().toString().replace("-", "") + "@example.com");
                poll.getParticipants().add(participant);
            }
            votesLogic.savePoll(poll);
            
            List<Poll> polls = pollAccess.findAll();
            
            Poll savedPoll = null;
            for (Poll p : polls) {
                if (p.getTitle().equals(poll.getTitle())) {
                    savedPoll = p;
                }
            }
            int i = 0;
            
            for (Participant p : savedPoll.getParticipants()) {
                Token t = new Token();
                t.setPoll(savedPoll);
                String tokenValue = "token" + i + "_started";
                t.setTokenValue(tokenValue);
                t.setValid(true);
                
                if (savedPoll.isParticipationTracking()) {
                    t.setParticipant(p);
                    p.setToken(t);
                }
                savedPoll.getTokens().add(t);
                i++;
            }
            savedPoll.setPollState(PollState.RUNNING);
            pollAccess.edit(savedPoll);
        } catch (VotesException ex) {
            Logger.getLogger(TestDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
