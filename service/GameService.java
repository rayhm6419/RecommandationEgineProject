package com.laioffer.twitch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.twitch.entity.response.Game;
import com.laioffer.twitch.entity.response.db.Item;
import com.laioffer.twitch.entity.response.db.ItemType;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
public class GameService {
    private static final String TOKEN = "Bearer 9fpgi30y06eqnygpehuhvh29darc5d";
    private static final String CLIENT_ID = "c409wg5iphnqemkcgs8i9nqeh5emsj";
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final int DEFAULT_GAME_LIMIT = 20;

    private static final String STREAM_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/streams?game_id=%s&first=%s";
    private static final String VIDEO_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/videos?game_id=%s&first=%s";
    private static final String CLIP_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/clips?game_id=%s&first=%s";
    private static final String TWITCH_BASE_URL = "https://www.twitch.tv/";
    private static final int DEFAULT_SEARCH_LIMIT = 20;



    // Build the request URL which will be used when calling Twitch APIs,
    // e.g. https://api.twitch.tv/helix/games/top when trying to get top games.

    private String buildGameURL(String url, String gameName, int limit){
        if (gameName.equals("")) { //返回前n个
            return String.format(url, limit);
        } else {
            try {
                // Encode special characters in URL, e.g. Rick Sun -> Rick%20Sun
                gameName = URLEncoder.encode(gameName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return String.format(url, gameName);
        }
    }

    // Similar to buildGameURL, build Search URL that will be used when calling Twitch API. e.g. https://api.twitch.tv/helix/clips?game_id=12924.
    //调用这个方法，去获得STREAM_SEARCH_URL_TEMPLATE，VIDEO_SEARCH_URL_TEMPLATE，CLIP_SEARCH_URL_TEMPLATE
    private String buildSearchURL(String url, String gameId, int limit) {
        try {
            gameId = URLEncoder.encode(gameId, "UTF-8"); //encode成string
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format(url, gameId, limit);
    }


    private String searchTwitch(String url) throws TwitchException{ //call twitch 有error会抛异常
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Define the response handler to parse and return HTTP response body returned from Twitch
        //从twitch获得的response，最终以String返回给浏览器,同时检查responseCode(==200)和entity(不为null)  是否满足
        ResponseHandler<String> responseHandler = response -> {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) { //返回200是正常，如果不是200那就抛异常了
                System.out.println("Response status: " + response.getStatusLine().getReasonPhrase());
                throw new TwitchException("Failed to get result from Twitch API");
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new TwitchException("Failed to get result from Twitch API");
            }
            JSONObject obj = new JSONObject(EntityUtils.toString(entity));
            return obj.getJSONArray("data").toString();
        };


        try {
            // Define the HTTP request, TOKEN and CLIENT_ID are used for user authentication on Twitch backend
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", TOKEN);
            request.setHeader("Client-Id", CLIENT_ID);
            return httpclient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to get result from Twitch API");
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //拿到string，要转换成list of game
        //Convert JSON format data returned from Twitch to an Arraylist of Game objects
    private List<Game> getGameList(String data){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Game[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse game data from Twitch API");
        }
    }

    // Integrate search() and getGameList() together, returns the dedicated game based on the game name.
    public List<Game> topGames(int limit){
        if (limit <= 0) {
            limit = DEFAULT_GAME_LIMIT;
        }
        return getGameList(searchTwitch(buildGameURL(TOP_GAME_URL, "", limit)));
        //  String url = buildGameURL(TOP_GAME_URL, "", limit);
//        String data = SearchTwitch(url);
//        return getGameList(data);
    }


    // Integrate search() and getGameList() together, returns the dedicated game based on the game name.
    public Game searchGame(String gameName){
        List<Game> gameList = getGameList(searchTwitch(buildGameURL(GAME_SEARCH_URL_TEMPLATE, gameName, 0)));
        if (gameList.size() != 0) {
            return gameList.get(0);
        }
        return null;
    }
    // Similar to getGameList, convert the json data returned from Twitch to a list of Item objects.
    private List<Item> getItemList(String data){
        ObjectMapper mapper = new ObjectMapper();
        try{
            return Arrays.asList(mapper.readValue(data, Item[].class));
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
            throw  new RuntimeException("Failed to parse item data from Twitch API");
        }
    }
    // Returns the top x streams based on game ID.
    private List<Item> searchStreams(String gameId, int limit) throws TwitchException{
        String url = buildSearchURL(STREAM_SEARCH_URL_TEMPLATE, gameId, limit);
        //拿到url以后，调用searchTwitch在后端去做status和response
        String data = searchTwitch(url);
        List<Item> streams = getItemList(data);

        //streams原本是没有url的，所以要通过遍历map里的data 手动做一个twitch base url + get生成的url
        for (Item item : streams){
            item.setUrl(TWITCH_BASE_URL + item.getThumbnailUrl());
            item.setItemType(ItemType.STREAM); //返回在哪个type底下（category）
        }
        return streams;
    }
// Returns the top x clips based on game ID.
    private List<Item> searchClips (String gameId, int limit) throws TwitchException{
        String url = buildSearchURL(CLIP_SEARCH_URL_TEMPLATE, gameId, limit);
        //拿到url以后，调用searchTwitch在后端去做status和response
        String data = searchTwitch(url);
        List<Item> clips = getItemList(data);

        //streams原本是没有url的，所以要通过遍历map里的data 手动做一个twitch base url + get生成的url
        for (Item item : clips) {
            item.setItemType(ItemType.CLIP); //返回在哪个type底下（category）
        }
        return clips;
    }
// Returns the top x video based on game ID.
    private List<Item> searchVideos(String gameId, int limit) throws TwitchException{
        String url = buildSearchURL(VIDEO_SEARCH_URL_TEMPLATE, gameId, limit);
        //拿到url以后，调用searchTwitch在后端去做status和response
        String data = searchTwitch(url);
        List<Item> videos = getItemList(data);

        //streams原本是没有url的，所以要通过遍历map里的data 手动做一个twitch base url + get生成的url
        for (Item item : videos) {
            item.setItemType(ItemType.VIDEO); //返回在哪个type底下（category）
        }
        return videos;
    }
    public List<Item> searchByType(String gameId, ItemType type, int limit) throws TwitchException {
        List<Item> items = Collections.emptyList();

        switch (type) {
            case STREAM:
                items = searchStreams(gameId, limit);
                break;
            case VIDEO:
                items = searchVideos(gameId, limit);
                break;
            case CLIP:
                items = searchClips(gameId, limit);
                break;
        }

        // Update gameId for all items. GameId is used by recommendation function
        for (Item item : items) {
            item.setGameId(gameId);
        }
        return items;
    }

    //map<key = Streams,Videos,Clips, values = 存放着的item>
    public Map<String, List<Item>> searchItems(String gameId) throws TwitchException {
        Map<String, List<Item>> itemMap = new HashMap<>();
        for (ItemType type : ItemType.values()) {
            itemMap.put(type.toString(), searchByType(gameId, type, DEFAULT_SEARCH_LIMIT));
        }
        return itemMap;
    }
}


//给twitch发送request，等待twitch的response并且通过responseHandler处理返回给浏览器