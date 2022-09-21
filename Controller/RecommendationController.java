package com.laioffer.twitch.Controller;

import com.laioffer.twitch.entity.response.db.Item;
import com.laioffer.twitch.service.RecommendationException;
import com.laioffer.twitch.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    @RequestMapping(value = "/recommendation", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, List<Item>> recommendation(HttpServletRequest request) throws ServletException {
        HttpSession session = request.getSession(false);
        Map<String, List<Item>> itemMap;
        try {
            if (session == null) {
                //检查是否有登陆，如果没登陆就用default recommend
                itemMap = recommendationService.recommendItemsByDefault();
            } else {
                //如果登陆了，recommend by user
                String userId = (String) session.getAttribute("user_id");
                itemMap = recommendationService.recommendItemsByUser(userId);
            }
        } catch (RecommendationException e) {
            throw new ServletException(e);
        }

        return itemMap;
    }

}
