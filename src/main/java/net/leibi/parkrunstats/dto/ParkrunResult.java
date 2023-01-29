package net.leibi.parkrunstats.dto;

public record ParkrunResult(Integer eventNumber, ParkRunner parkRunner, int position, String time, double ageGrade) {
}
