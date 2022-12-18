package fr.hyriode.moutron.util;

import fr.hyriode.hyrame.item.ItemHead;

/**
 * Created by AstFaster
 * on 17/12/2022 at 10:45
 */
public enum MTHead implements ItemHead {

    BLUE_SHEEP("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM5ZWZjNGI0ZWFkZWM0ODU3NmE1NzAwZWM4MTIzOTU1MTAzMjdlNWQxZTdjMTA4ZmQ4YWJjNzc5NjY4NWFhMyJ9fX0="),

    ;

    private final String texture;

    MTHead(String texture) {
        this.texture = texture;
    }

    @Override
    public String getTexture() {
        return this.texture;
    }

}
