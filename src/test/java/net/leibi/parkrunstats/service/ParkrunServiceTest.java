package net.leibi.parkrunstats.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ParkrunServiceTest {

    @Autowired
    ParkrunService parkrunService;


    @Test
    @Disabled
    void testClient() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.google.com/"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);

        Document document = Jsoup.connect("https://www.parkrun.com.de/seewoog/results/latestresults/").get();

        Node content = Objects.requireNonNull(document.getElementById("content")).getElementsByClass("Results-header").get(0).childNode(1).childNode(2).childNode(0);
        content.attr("value");
        Elements results = Objects.requireNonNull(document.getElementById("content")).getElementsByClass("Results-table-row");

        assertThat(document).isNotNull();

    }

    @Test
    void getLatestEventNumber() {
        assertThat(parkrunService.getLatestEventNumber("seewoog")).hasValue(158);
    }

    @Test
    void getParkrunResults() {
        assertThat(parkrunService.getParkrunResults("seewoog", 158))
                .isNotEmpty()
                .hasSize(29)
                .allSatisfy(parkrunResult -> assertThat(parkrunResult.eventNumber()).isEqualTo(158));
    }

    @Test
    void getAllResultsFromEvent() {
        assertThat(parkrunService.getAllResultsFromEvent("seewoog")).hasSize(5376);
    }

    @Test
    void getAllResultsFromAllSupportedEvents() {
        assertThat(parkrunService.getAllResultsFromAllSupportedEvents()).hasSize(17591);
    }
}