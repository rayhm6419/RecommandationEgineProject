package com.laioffer.twitch.entity.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

//JasonProperty的作用就是返回定义的名字，而不是返回field里定义的名字， like release_time to releaseTime.
@JsonIgnoreProperties(ignoreUnknown = true) //有一些filed没出现在里面，可以ignore
@JsonInclude(JsonInclude.Include.NON_NULL)//最后返回前端，值不为空的
@JsonDeserialize(builder = Game.Builder.class)//deserialize

public class Game {
    @JsonProperty("id")
    private final String id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("box_art_url")
    private final String boxArtUrl;

    private Game(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.boxArtUrl = builder.boxArtUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBoxArtUrl() {
        return boxArtUrl;
    }
    @JsonIgnoreProperties(ignoreUnknown = true) //有一些filed没出现在里面，可以ignore
    @JsonInclude(JsonInclude.Include.NON_NULL)//最后返回前端，值不为空的
    public static class Builder { //desera lization
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("box_art_url")
        private String boxArtUrl;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder boxArtUrl(String boxArtUrl) {
            this.boxArtUrl = boxArtUrl;
            return this;
        }

        public Game build() {
            return new Game(this);
        }
    }

}
