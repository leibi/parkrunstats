package net.leibi.parkrunstats.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class WebsiteFetcherService {

    @NonNull
    public Optional<Document> getWebsite(final @NonNull String url) {
        try {
            log.info("Checking Url: {}", url);
            return Optional.of(Jsoup.connect(url).get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
