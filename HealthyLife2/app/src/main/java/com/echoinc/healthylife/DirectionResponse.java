package com.echoinc.healthylife;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by msi_ on 11-Jan-18.
 */

public class DirectionResponse {

    @SerializedName("routes")
    @Expose
    private List<Route> routes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class Route {

        @SerializedName("copyrights")
        @Expose
        private String copyrights;
        @SerializedName("legs")
        @Expose
        private List<Leg> legs = null;
        @SerializedName("summary")
        @Expose
        private String summary;
        @SerializedName("warnings")
        @Expose
        private List<Object> warnings = null;
        @SerializedName("waypoint_order")
        @Expose
        private List<Object> waypointOrder = null;

        public void setCopyrights(String copyrights) {
            this.copyrights = copyrights;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public void setLegs(List<Leg> legs) {
            this.legs = legs;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public List<Object> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<Object> warnings) {
            this.warnings = warnings;
        }

        public List<Object> getWaypointOrder() {
            return waypointOrder;
        }

        public void setWaypointOrder(List<Object> waypointOrder) {
            this.waypointOrder = waypointOrder;
        }

    }

    public class Leg {

        @SerializedName("distance")
        @Expose
        private Distance distance;
        @SerializedName("duration")
        @Expose
        private Duration duration;
        @SerializedName("end_address")
        @Expose
        private String endAddress;
        @SerializedName("end_location")
        @Expose
        private EndLocation endLocation;
        @SerializedName("start_address")
        @Expose
        private String startAddress;
        @SerializedName("start_location")
        @Expose
        private StartLocation startLocation;
        @SerializedName("steps")
        @Expose
        private List<Step> steps = null;
        @SerializedName("traffic_speed_entry")
        @Expose
        private List<Object> trafficSpeedEntry = null;
        @SerializedName("via_waypoint")
        @Expose
        private List<Object> viaWaypoint = null;

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public String getEndAddress() {
            return endAddress;
        }

        public void setEndAddress(String endAddress) {
            this.endAddress = endAddress;
        }

        public EndLocation getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(EndLocation endLocation) {
            this.endLocation = endLocation;
        }

        public String getStartAddress() {
            return startAddress;
        }

        public void setStartAddress(String startAddress) {
            this.startAddress = startAddress;
        }

        public StartLocation getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(StartLocation startLocation) {
            this.startLocation = startLocation;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }

        public List<Object> getTrafficSpeedEntry() {
            return trafficSpeedEntry;
        }

        public void setTrafficSpeedEntry(List<Object> trafficSpeedEntry) {
            this.trafficSpeedEntry = trafficSpeedEntry;
        }

        public List<Object> getViaWaypoint() {
            return viaWaypoint;
        }

        public void setViaWaypoint(List<Object> viaWaypoint) {
            this.viaWaypoint = viaWaypoint;
        }

    }

    public class StartLocation {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }

    public class EndLocation {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }

    public class Duration {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("value")
        @Expose
        private Integer value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }

    public class Distance {

        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("value")
        @Expose
        private Integer value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

    }


    public class Step {

        @SerializedName("end_location")
        @Expose
        private EndLocation_ endLocation;
        @SerializedName("html_instructions")
        @Expose
        private String htmlInstructions;
        @SerializedName("polyline")
        @Expose
        private Polyline polyline;
        @SerializedName("start_location")
        @Expose
        private StartLocation_ startLocation;
        @SerializedName("travel_mode")
        @Expose
        private String travelMode;
        @SerializedName("maneuver")
        @Expose
        private String maneuver;

        public EndLocation_ getEndLocation() {
            return endLocation;
        }

        public void setEndLocation(EndLocation_ endLocation) {
            this.endLocation = endLocation;
        }

        public String getHtmlInstructions() {
            return htmlInstructions;
        }

        public void setHtmlInstructions(String htmlInstructions) {
            this.htmlInstructions = htmlInstructions;
        }

        public Polyline getPolyline() {
            return polyline;
        }

        public void setPolyline(Polyline polyline) {
            this.polyline = polyline;
        }

        public StartLocation_ getStartLocation() {
            return startLocation;
        }

        public void setStartLocation(StartLocation_ startLocation) {
            this.startLocation = startLocation;
        }

        public String getTravelMode() {
            return travelMode;
        }

        public void setTravelMode(String travelMode) {
            this.travelMode = travelMode;
        }

        public String getManeuver() {
            return maneuver;
        }

        public void setManeuver(String maneuver) {
            this.maneuver = maneuver;
        }

    }

    public class StartLocation_ {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }

    public class EndLocation_ {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

    }

    public class Polyline {

        @SerializedName("points")
        @Expose
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

    }
}
