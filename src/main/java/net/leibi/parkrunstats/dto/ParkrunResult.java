package net.leibi.parkrunstats.dto;

public record ParkrunResult(String event, Integer eventNumber, ParkRunner parkRunner, int position, String time,
                            double ageGrade) {
}
