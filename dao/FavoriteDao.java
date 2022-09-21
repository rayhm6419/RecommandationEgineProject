package com.laioffer.twitch.dao;


import com.laioffer.twitch.entity.response.db.Item;
import com.laioffer.twitch.entity.response.db.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import com.laioffer.twitch.entity.response.db.ItemType;


import java.util.HashSet;
import java.util.Set;

@Repository
public class FavoriteDao {

    @Autowired
    private SessionFactory sessionFactory;

    //关注
    public void setFavoriteItem(String userId, Item item){
        Session session = null;

        try{
            session = sessionFactory.openSession();
            //通过user，把favorite的item添加到数据库里
            User user = session.get(User.class, userId);
            user.getItemSet().add(item);

            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (Exception ex){
            ex.printStackTrace();
            if (session != null) session.getTransaction().rollback();
        } finally {
            if (session != null){
                session.close();
            }
        }
    }

    //取消关注
    public void unsetFavoriteItem(String userId, String itemId){
        Session session = null;

        try{
            session = sessionFactory.openSession();
            //通过user，把favorite的item添加到数据库里
            User user = session.get(User.class, userId);
            Item item = session.get(Item.class, itemId);
            user.getItemSet().remove(item);  //从user里remove掉favorite item

            session.beginTransaction();
            session.update(user);  //这里是update user remove掉某个item
            session.getTransaction().commit();
        } catch (Exception ex){
            ex.printStackTrace();
            if (session != null) session.getTransaction().rollback();
        } finally {
            if (session != null){
                session.close();
            }
        }
    }

    public Set<Item> getFavoriteItems(String userId){
        Session session = null;

        try {
            session = sessionFactory.openSession();
            User user = session.get(User.class, userId);
            if (user != null){
                return user.getItemSet();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (session != null){
                session.close();
            }
        }
        return new HashSet<>();
    }
    // Get favorite item ids for the given user
    //通过userid去获得他曾经favorite过的游戏
    public Set<String> getFavoriteItemIds(String userId) {
        Set<String> itemIds = new HashSet<>();

        try (Session session = sessionFactory.openSession()) {
            Set<Item> items = session.get(User.class, userId).getItemSet();
            for(Item item : items) {
                itemIds.add(item.getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return itemIds;
    }
    // Get favorite items for the given user. The returned map includes three entries like {"Video": [item1, item2, item3], "Stream": [item4, item5, item6], "Clip": [item7, item8, ...]}
    //通过user favorite过的item，现在进行分类，stream ，video，clips
    public Map<String, List<String>> getFavoriteGameIds(Set<String> favoriteItemIds) {
        Map<String, List<String>> itemMap = new HashMap<>();
        for (ItemType type : ItemType.values()) {
            itemMap.put(type.toString(), new ArrayList<>());
        }

        try (Session session = sessionFactory.openSession()) {
            for(String itemId : favoriteItemIds) {
                Item item = session.get(Item.class, itemId);
                itemMap.get(item.getItemType().toString()).add(item.getGameId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return itemMap;
    }
}

//总结line 93 - 122：getFavoriteItemIds，首先是通过user先拿到所有favorite过的item，
//                   getFavoriteGameIds， 然后放进分视频的类别，然后通过user
//                  喜欢过的次数进行排序，
//                  次数越多优先级越高，
//                  最后通过级别推荐别的视频给用户


