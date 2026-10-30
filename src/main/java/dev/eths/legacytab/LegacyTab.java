package dev.eths.legacytab;

import dev.eths.legacytab.player.WrappedTabPlayer;
import dev.eths.legacytab.tab.TabAdapter;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class LegacyTab implements AutoCloseable {

    @Getter
    private final HashMap<UUID, WrappedTabPlayer> playerMap = new HashMap<>();
    private final BukkitTask task;

    public LegacyTab(JavaPlugin plugin) {
        this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    @Override
    public void close() {
        this.task.cancel();

        for (WrappedTabPlayer player : this.playerMap.values())
            player.getTabImpl().close();

        this.playerMap.clear();
    }

    public void setAdapter(Player player, TabAdapter adapter) {
        WrappedTabPlayer tab = this.playerMap.computeIfAbsent(player.getUniqueId(), ignored -> new WrappedTabPlayer(player));
        tab.setTabAdapter(adapter);
    }

    public void removeAdapter(Player player) {
        WrappedTabPlayer tab = this.playerMap.get(player.getUniqueId());

        if (tab != null)
            tab.setTabAdapter(TabAdapter.DEFAULT);
    }

    private void tick() {
        HashSet<TabAdapter> updated = new HashSet<>();

        for (WrappedTabPlayer player : this.playerMap.values()) {
            TabAdapter adapter = player.getTabAdapter();

            if (updated.add(adapter))
                adapter.tick();

            player.getTabImpl().tick(adapter);
        }
    }
}
