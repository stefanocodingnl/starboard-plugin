package com.avairebot.starboard;

import com.avairebot.Constants;
import com.avairebot.database.collection.DataRow;
import com.avairebot.plugin.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;

public class Starboard extends JavaPlugin {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Starboard.class);
    static final String STARBOARD_TABLE = "starboard";

    @Override
    public void onEnable() {
        registerCommand(new StarboardCommand(this));
        registerEventListener(new EmoteEventListener(this));
        registerMigration(new SetupStarboardTableAndFieldMigration());
    }

    Color getColor(float percentage) {
        percentage = percentage > 1.0F ? 1.0F : percentage;
        percentage = percentage < 0.0F ? 0.05F : percentage;

        float inverse_blending = 1 - percentage;

        return new Color(
                (Color.YELLOW.getRed() * percentage + Color.WHITE.getRed() * inverse_blending) / 255,
                (Color.YELLOW.getGreen() * percentage + Color.WHITE.getGreen() * inverse_blending) / 255,
                (Color.YELLOW.getBlue() * percentage + Color.WHITE.getBlue() * inverse_blending) / 255
        );
    }

    String getStarEmote(int stars) {
        if (stars <= 5) {
            return "\u2B50";
        } else if (stars <= 10) {
            return "\uD83C\uDF1F";
        } else if (stars <= 25) {
            return "\uD83D\uDCAB";
        }
        return "\u2728";
    }

    String getStarboardValueFromGuild(String guildId) {
        try {
            DataRow first = getAvaire().getDatabase().newQueryBuilder(Constants.GUILD_TABLE_NAME)
                    .select("starboard")
                    .where("id", guildId)
                    .get()
                    .first();

            return first.getString("starboard", null);
        } catch (SQLException e) {
            LOGGER.error("Failed to fetch the guild starboard value from the database for: " + guildId, e);
        }
        return null;
    }
}
