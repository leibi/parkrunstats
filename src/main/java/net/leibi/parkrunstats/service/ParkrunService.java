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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static String getTextFromClass(Element result, String className) {
        TextNode node = (TextNode) result.getElementsByClass(className).get(0).childNodes().get(0);
        return node.text();
    }

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

    public List<ParkrunResult> getParkrunResults(@Nonnull String event, @Nonnull Integer eventnumber) {
        String resultsUrl = EVENTRESULTBASEURL.formatted(event, eventnumber);
        var resultList = new ArrayList<ParkrunResult>();
        Optional<Document> optionalWebsite = websiteFetcherService.getWebsite(resultsUrl);
        if (optionalWebsite.isEmpty()) return resultList;
        Elements results = optionalWebsite.get().getElementById("content").getElementsByClass("Results-table-row");
        for (Element result : results) {
            resultList.add(getParkrunResultFromResultElement(result));
        }
        return resultList;
    }

    private ParkrunResult getParkrunResultFromResultElement(Element result) {
        String name = result.attr("data-name");
        String ageGroup = result.attr("data-agegroup");
        String club = result.attr("data-club");
        String gender = result.attr("data-gender");
        String position = result.attr("data-position");
        String runs = result.attr("data-runs");
        String vols = result.attr("data-vols");
        String agegrade = result.attr("data-agegrade");

        String time = ((TextNode) result.childNodes().get(5).childNodes().get(0).childNode(0)).text();
        ParkRunner parkrunner = new ParkRunner(name, ageGroup, club, gender, Integer.valueOf(runs), Integer.valueOf(vols));
        return new ParkrunResult(parkrunner, Integer.valueOf(position), time, Double.valueOf(agegrade));
    }
}
