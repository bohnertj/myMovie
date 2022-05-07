package com.mobileapplication.mymovie_10.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_settings")
public class AppSettingsEnity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entry_id")
    @NonNull
    public int entryId;

    @ColumnInfo(name = "setting_app_language")
    public String settingAppLanguage;

    @ColumnInfo(name = "setting_region")
    public String settingRegion;

    @ColumnInfo(name = "max_search_hits")
    public int maxSearchHits;

    @ColumnInfo(name = "max_popular_hits")
    public int maxPopularHits;

    @ColumnInfo(name = "max_top_rated_hits")
    public int maxTopRatedHits;

    @ColumnInfo(name = "search_type")
    public int searchType;

    public AppSettingsEnity(String settingAppLanguage, String settingRegion, int maxSearchHits, int maxPopularHits, int maxTopRatedHits, int searchType) {
        this.settingAppLanguage = settingAppLanguage;
        this.settingRegion = settingRegion;
        this.maxSearchHits = maxSearchHits;
        this.maxPopularHits = maxPopularHits;
        this.maxTopRatedHits = maxTopRatedHits;
        this.searchType = searchType;
    }

    @Override
    public String toString() {
        return "AppSettingsEnity{" +
                "entryId=" + entryId +
                ", settingAppLanguage='" + settingAppLanguage + '\'' +
                ", settingRegion='" + settingRegion + '\'' +
                ", maxSearchHits=" + maxSearchHits +
                ", maxPopularHits=" + maxPopularHits +
                ", maxTopRatedHits=" + maxTopRatedHits +
                ", searchType=" + searchType +
                '}';
    }
}
