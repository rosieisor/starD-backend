package com.web.stard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Location {
    private String address; // 장소

    private double latitude; // 위도
    private double longitude; // 경도
    private double weight; // 가중치

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String address, double weight) {
        this.address = address;
        this.weight = weight;
    }

    public Location(String address, double latitude, double longitude, double weight) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Location {" +
                "주소='" + address + '\'' +
                ", 위도=" + latitude +
                ", 경도=" + longitude +
                ", 가중치=" + weight +
                '}';
    }

    public static Location calculate(List<Location> locations) {
        double totalWeightedLat = 0;
        double totalWeightedLon = 0;
        double totalWeight = 0;

        for (Location loc : locations) {
            double weight = loc.getWeight();
            totalWeightedLat += loc.getLatitude() * weight;
            totalWeightedLon += loc.getLongitude() * weight;
            totalWeight += weight;
        }

        return new Location(totalWeightedLat / totalWeight, totalWeightedLon / totalWeight);
    }


}
