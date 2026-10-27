package me.dmt.horsefly;

import com.mojang.logging.LogUtils;
import me.dmt.horsefly.modules.HorseFly;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class HorseFlyAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOG.info("horsefly loaded");
        Modules.get().add(new HorseFly());
    }

    @Override
    public String getPackage() {
        return "me.dmt.horsefly";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("crabs3", "horsefly");
    }
}
