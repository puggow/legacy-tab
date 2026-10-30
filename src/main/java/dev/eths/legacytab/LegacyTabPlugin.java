package dev.eths.legacytab;

import dev.eths.legacytab.listener.PlayerJoinListener;
import dev.eths.legacytab.listener.PlayerQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class LegacyTabPlugin extends JavaPlugin {

    private LegacyTab service;

    @Override
    public void onEnable() {
        this.service = new LegacyTab(this);
        new PlayerJoinListener(this, this.service);
        new PlayerQuitListener(this, this.service);
    }

    @Override
    public void onDisable() {
        if (this.service != null)
            this.service.close();
    }

}
