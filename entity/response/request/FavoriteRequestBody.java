package com.laioffer.twitch.entity.response.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.laioffer.twitch.entity.response.db.Item;

//从客户端发送操作 jason变成object type
public class FavoriteRequestBody {
    @JsonProperty("favorite")
    private Item favoriteItem;

    public Item getFavoriteItem() {
        return favoriteItem;
    }

}
