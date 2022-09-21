package com.laioffer.twitch.Controller;

import com.laioffer.twitch.entity.response.db.Item;
import com.laioffer.twitch.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class SearchController {
    @Autowired
    private GameService gameService;

    @RequestMapping(value = "/search", method = RequestMethod.GET )
    @ResponseBody //自动把object返回成jason的格式返回给浏览器
    public Map<String, List<Item>> search(@RequestParam(value = "game_id") String gameId){
        return gameService.searchItems(gameId);

    }
}
