package net.leibi.parkrunstats.service;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.leibi.parkrunstats.dto.ParkRunner;
import net.leibi.parkrunstats.dto.ParkrunResult;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ch.qos.logback.core.util.OptionHelper.isNullOrEmpty;
import static java.util.Objects.isNull;

@Service
@Slf4j
public class ParkrunService {

    // first path param is the event name
    // second parameter is the event number
    // example: https://www.parkrun.com.de/seewoog/results/157/
    private static final String EVENTRESULTBASEURL = "https://www.parkrun.com.de/%s/results/%s/";
    private final WebsiteFetcherService websiteFetcherService;
    private String[] supportedEvents = {"seewoog"};

    public ParkrunService(WebsiteFetcherService websiteFetcherService) {
        this.websiteFetcherService = websiteFetcherService;
    }

    @Cacheable
    public Optional<Integer> getLatestEventNumber(@Nonnull final String event) {
        // https://www.parkrun.com.de/seewoog/results/latestresults/
        String latestresultsURL = EVENTRESULTBASEURL.formatted(event, "latestresults");

        Optional<Document> resultsPage = websiteFetcherService.getWebsite(latestresultsURL);
        if (resultsPage.isEmpty()) return Optional.empty();
        Element contentElement = resultsPage.get().getElementById("content");
        if (isNull(contentElement)) return Optional.empty();
        Node content = contentElement.getElementsByClass("Results-header").get(0).childNode(1).childNode(2).childNode(0);
        TextNode textNode = ((TextNode) content);
        return Optional.of(Integer.valueOf(textNode.text().substring(1)));
    }

    @Cacheable
    public List<ParkrunResult> getParkrunResults(@Nonnull String event, @Nonnull Integer eventNumber) {
        String resultsUrl = EVENTRESULTBASEURL.formatted(event, eventNumber);
        var resultList = new ArrayList<ParkrunResult>();
        Optional<Document> optionalWebsite = websiteFetcherService.getWebsite(resultsUrl);
        if (optionalWebsite.isEmpty()) return resultList;
        Elements results = optionalWebsite.get().getElementById("content").getElementsByClass("Results-table-row");
        for (Element result : results) {
            resultList.add(getParkrunResultFromResultElement(result, eventNumber));
        }
        return resultList;
    }

    private ParkrunResult getParkrunResultFromResultElement(Element result, Integer eventNumber) {
        String name = result.attr("data-name");
        String ageGroup = result.attr("data-agegroup");
        String club = result.attr("data-club");
        String gender = result.attr("data-gender");
        String position = result.attr("data-position");
        String runs = result.attr("data-runs");
        String vols = result.attr("data-vols");
        String agegrade = result.attr("data-agegrade");
        String time = null;
        if (result.childNodeSize() >= 6) {
            Node timeNode = result.childNode(5);
            if (timeNode.childNodeSize() >= 1) {
                Node resultTimeNode = timeNode.childNode(0);
                if (resultTimeNode.childNodeSize() >= 1) {
                    time = ((TextNode) resultTimeNode.childNode(0)).text();
                }
            }
        }
        Integer runsInt = isNullOrEmpty(runs) ? 0 : Integer.valueOf(runs);
        Integer volsInt = isNullOrEmpty(vols) ? 0 : Integer.valueOf(vols);
        ParkRunner parkrunner = new ParkRunner(name, ageGroup, club, gender, runsInt, volsInt);

        return new ParkrunResult(eventNumber, parkrunner, Integer.parseInt(position), time, Double.parseDouble(agegrade));
    }
}
