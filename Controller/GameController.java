package com.laioffer.twitch.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.twitch.service.TwitchException;
import com.laioffer.twitch.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class GameController {

    //autowired 是injection的其中一个步骤
    @Autowired //autowired
    private GameService gameService; //Constructor Injection

//    public GameController(GameService gameService) { //Field Injection
//        this.gameService = gameService;
//    }

    //game?game_name = whatever
    //game
    @RequestMapping(value = "/game", method = RequestMethod.GET) //定义这个RESTAPI，such as HTTP URL, method,
    //required不是强制要求的的，有了表示game?game_name = whatever 或者 game都可以搜索到
    public void getGame(@RequestParam(value = "game name", required = false) String gameName,
                        HttpServletResponse response) throws IOException, ServletException {//response to client, if no response, throw exception

        response.setContentType("application/json;charset=UTF-8");
        try {
            //当用户搜索specific的游戏名称，就会调用GameService里的searchGame
            if (gameName != null) {
                response.getWriter().print(new ObjectMapper().writeValueAsString(gameService.searchGame(gameName)));
            } else { //如果gameName不存在，就返回topGame
                response.getWriter().print(new ObjectMapper().writeValueAsString(gameService.topGames(0)));
            }
        } catch (TwitchException e) {
            throw new ServletException(e);
        }
    }
}

