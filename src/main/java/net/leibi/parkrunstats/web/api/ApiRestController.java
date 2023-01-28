package net.leibi.parkrunstats.web.api;

import net.leibi.parkrunstats.dto.ParkRunStat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ApiRestController {

    @GetMapping("/")
    List<ParkRunStat> getAllStats() {
        return new ArrayList<>();
    }
}
