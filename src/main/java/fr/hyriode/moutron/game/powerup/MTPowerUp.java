package fr.hyriode.moutron.game.powerup;

import fr.hyriode.api.language.HyriLanguageMessage;
import fr.hyriode.hyrame.IHyrame;
import fr.hyriode.hyrame.item.HyriItem;
import fr.hyriode.hyrame.item.ItemBuilder;
import fr.hyriode.moutron.HyriMoutron;
import fr.hyriode.moutron.game.MTPlayer;
import fr.hyriode.moutron.language.MTMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by AstFaster
 * on 19/12/2022 at 11:42
 */
public abstract class MTPowerUp extends HyriItem<HyriMoutron> {

    public MTPowerUp(HyriMoutron plugin, String name, ItemStack item) {
        super(plugin, name, () -> HyriLanguageMessage.get("power-up." + name + ".display"), () -> HyriLanguageMessage.get("power-up." + name + ".description"), item);
    }

    @Override
    public ItemStack onPreGive(IHyrame hyrame, Player player, int slot, ItemStack itemStack) {
        return new ItemBuilder(itemStack)
                .withName(itemStack.getItemMeta().getDisplayName() + " " + MTMessage.ITEM_RIGHT_CLICK.asString(player))
                .build();
    }

    @Override
    public void onRightClick(IHyrame hyrame, PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final MTPlayer gamePlayer = HyriMoutron.get().getGame().getPlayer(player);

        this.handle(gamePlayer);

        player.getInventory().setItem(4, null);
        gamePlayer.setPowerUp(null);
    }

    public abstract void handle(MTPlayer gamePlayer);

}
