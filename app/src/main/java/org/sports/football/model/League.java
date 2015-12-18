package org.sports.football.model;


import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * All getters and setters are used for deserializing using Jackson.
 */
@SuppressWarnings("unused")
public class League implements Serializable {


    private String leagueName;
    private byte[] image;
    private Bitmap imageBitmap;
    private Links _links;
    private String caption;
    private String league;
    private String year;
    private String numberOfTeams;
    private String numberOfGames;
    private String lastUpdated;

    public League(){}

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(String numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public String getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(String numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    static class Links implements Serializable {
        private Self self;
        private Teams teams;
        private Fixtures fixtures;
        private LeagueTable leagueTable;

        public Links(){}

        public Self getSelf() {
            return self;
        }

        public void setSelf(Self self) {
            this.self = self;
        }

        public Teams getTeams() {
            return teams;
        }

        public void setTeams(Teams teams) {
            this.teams = teams;
        }

        public Fixtures getFixtures() {
            return fixtures;
        }

        public void setFixtures(Fixtures fixtures) {
            this.fixtures = fixtures;
        }

        public LeagueTable getLeagueTable() {
            return leagueTable;
        }

        public void setLeagueTable(LeagueTable leagueTable) {
            this.leagueTable = leagueTable;
        }
    }

    static class Self implements Serializable {
        private String href;
        public Self(){}

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    static class Teams implements Serializable{
        private String href;
        public Teams(){}

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    static class Fixtures implements Serializable {
        private String href;
        public Fixtures(){}

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    static class LeagueTable implements Serializable {
        private String href;
        public LeagueTable(){}

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    @Override
    public String toString() {
        return this.caption;
    }
}
