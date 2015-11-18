package com.feamor.testing.server.initialization;

import com.feamor.testing.server.games.ChatGameCreator;
import com.feamor.testing.server.services.GameResolver;
import com.feamor.testing.server.services.PlayersDAO;
import com.feamor.testing.server.services.UserManager;
import com.feamor.testing.server.utils.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by feamor on 10.11.2015.
 */
public class TestInitializer extends Initializer {

    @Autowired
    private PlayersDAO playersDAO;

    @Autowired
    private GameResolver gameResolver;

    @Override
    public void initialize() {
        fillGameTags();
        createGameDescriptions();
        createGameCreators();

        createTestUsers();
    }

    private void fillGameTags() {
        GameResolver.GameCategory gameCategory;

        gameCategory = new GameResolver.GameCategory();
        gameCategory.id = 1;
        gameCategory.descriptino = "Test game category or tag";
        gameCategory.name = "test";
        gameResolver.addGameCategory(gameCategory);

        gameCategory = new GameResolver.GameCategory();
        gameCategory.id = 10;
        gameCategory.descriptino = "Services and games for communication.";
        gameCategory.name = "talk";
        gameResolver.addGameCategory(gameCategory);

        gameCategory = new GameResolver.GameCategory();
        gameCategory.id = 20;
        gameCategory.descriptino = "Different text quests.";
        gameCategory.name = "text quest";
        gameResolver.addGameCategory(gameCategory);
    }

    private void createGameDescriptions() {
        GameResolver.GameDescription description;

        description = new GameResolver.GameDescription();
        description.alias = "test";
        description.description = "Test Game description";
        description.gameCategories.add(1);
        description.id = 10;
        description.name = "Simple test game";
        description.iconUri = "https://lh4.ggpht.com/wKrDLLmmxjfRG2-E-k5L5BUuHWpCOe4lWRF7oVs1Gzdn5e5yvr8fj-ORTlBF43U47yI=w300";
        gameResolver.addGameDescription(description);

        description = new GameResolver.GameDescription();
        description.alias = "chat";
        description.description = "Simple chat between all players, allow ask any questionû!";
        description.gameCategories.add(1);
        description.gameCategories.add(10);
        description.id = 11;
        description.iconUri = "http://www.felixsupplies.ae/wp-content/uploads/icon_chat.png";
        description.name = "Simple Chat";
        gameResolver.addGameDescription(description);
    }

    private void createGameCreators() {
        gameResolver.addCreatorClass("chat", ChatGameCreator.class);
    }

    private void createTestUsers() {
        UserInfo info;
        playersDAO.registerUserWithLoginAnsPassword("test", "test");
        info = playersDAO.getUserInfo("test");
        info.firstName = "TestFirstName";
        info.secondName = "TestSecondName";
        info.email = "test@test.ru";
        info.iconUri = "https://pp.vk.me/c623828/v623828675/3e729/RsFsJUHidlE.jpg";
        info.avalableGames.put(1000L, 10);
        info.avalableGames.put(1001L, 11);
        playersDAO.changeUserInfo(info);

        playersDAO.registerUserWithLoginAnsPassword("feamor", "x86");
        info = playersDAO.getUserInfo("feamor");
        info.firstName = "Sergeuy";
        info.secondName = "Svendrovskiy";
        info.email = "feamorx86@yandex.ru";
        info.iconUri = "https://pp.vk.me/c309922/v309922675/29fc/8GxBcjmfZ9c.jpg";
        info.avalableGames.put(2000L, 11);
        playersDAO.changeUserInfo(info);
    }


}
