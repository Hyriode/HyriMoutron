package fr.hyriode.moutron.api;

import fr.hyriode.api.mongodb.MongoDocument;
import fr.hyriode.api.mongodb.MongoSerializable;
import fr.hyriode.api.mongodb.MongoSerializer;
import fr.hyriode.api.player.IHyriPlayer;
import fr.hyriode.api.player.model.IHyriStatistics;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTGameType;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MTStatistics implements IHyriStatistics {

    private final Map<MTGameType, Data> dataMap = new HashMap<>();

    public Map<MTGameType, Data> getData() {
        return this.dataMap;
    }

    @Override
    public void save(MongoDocument document) {
        for (Map.Entry<MTGameType, Data> entry : this.dataMap.entrySet()) {
            document.append(entry.getKey().name(), MongoSerializer.serialize(entry.getValue()));
        }
    }

    @Override
    public void load(MongoDocument document) {
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            final MongoDocument dataDocument = MongoDocument.of((Document) entry.getValue());
            final Data data = new Data();

            data.load(dataDocument);

            this.dataMap.put(MTGameType.valueOf(entry.getKey()), data);
        }
    }

    public Data getData(MTGameType gameType) {
        return this.dataMap.merge(gameType, new Data(), (oldValue, newValue) -> oldValue);
    }

    public void update(IHyriPlayer account) {
        account.getStatistics().add(HyriMoutron.ID, this);
        account.update();
    }

    public static MTStatistics get(IHyriPlayer account) {
        MTStatistics statistics = account.getStatistics().read(HyriMoutron.ID, new MTStatistics());

        if (statistics == null) {
            statistics = new MTStatistics();
            statistics.update(account);
        }
        return statistics;
    }

    public static MTStatistics get(UUID playerId) {
        return get(IHyriPlayer.get(playerId));
    }

    public static class Data implements MongoSerializable {

        private long kills;
        private long deaths;

        private long victories;
        private long gamesPlayed;

        @Override
        public void save(MongoDocument document) {
            document.append("kills", this.kills);
            document.append("deaths", this.deaths);
            document.append("victories", this.victories);
            document.append("gamesPlayed", this.gamesPlayed);
        }

        @Override
        public void load(MongoDocument document) {
            this.kills = document.getLong("kills");
            this.deaths = document.getLong("deaths");
            this.victories = document.getLong("victories");
            this.gamesPlayed = document.getLong("gamesPlayed");
        }

        public long getKills() {
            return this.kills;
        }

        public void addKills(long kills) {
            this.kills += kills;
        }

        public long getDeaths() {
            return this.deaths;
        }

        public void addDeaths(long deaths) {
            this.deaths += deaths;
        }

        public long getVictories() {
            return this.victories;
        }

        public void addVictories(long victories) {
            this.victories += victories;
        }

        public long getGamesPlayed() {
            return this.gamesPlayed;
        }

        public void addGamesPlayed(long gamesPlayed) {
            this.gamesPlayed += gamesPlayed;
        }

    }

}
