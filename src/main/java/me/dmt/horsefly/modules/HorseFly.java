package me.dmt.horsefly.modules;

import me.dmt.horsefly.util.NaNInputState;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.phys.Vec2;

public class HorseFly extends Module {
    private static final NaNInputState NAN_INPUT = new NaNInputState();
    private final SettingGroup sg = settings.getDefaultGroup();

    public final Setting<Double> speed = sg.add(new DoubleSetting.Builder()
        .name("speed")
        .description("Horizontal speed, blocks per tick.")
        .defaultValue(1)
        .range(0.1, 50)
        .sliderRange(0.1, 10)
        .build()
    );

    public final Setting<Double> verticalSpeed = sg.add(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("Up/down speed, blocks per tick. Jump goes up, sprint goes down.")
        .defaultValue(0.5)
        .range(0, 5)
        .sliderRange(0, 5)
        .build()
    );

    public final Setting<Double> glide = sg.add(new DoubleSetting.Builder()
        .name("glide")
        .description("How far you sink each tick when you aren't going up or down.")
        .defaultValue(0)
        .range(0, 0.5)
        .sliderRange(0, 0.5)
        .decimalPlaces(3)
        .build()
    );

    public final Setting<Boolean> nanInput = sg.add(new BoolSetting.Builder()
        .name("nan-input")
        .description("Makes it work.")
        .defaultValue(true)
        .onChanged(value -> NAN_INPUT.reset())
        .build()
    );

    public final Setting<Integer> nanDelay = sg.add(new IntSetting.Builder()
        .name("nan-delay")
        .description("Delay between move.real and NaN inputs")
        .defaultValue(0)
        .range(0, 20)
        .sliderRange(0, 20)
        .visible(nanInput::get)
        .onChanged(value -> NAN_INPUT.reset())
        .build()
    );

    public HorseFly() {
        super(Categories.Movement, "horse-fly", "Fly horses.");
    }

    @Override
    public void onActivate() {
        NAN_INPUT.reset();
    }

    @Override
    public void onDeactivate() {
        NAN_INPUT.reset();
    }

    public static boolean nanActive() {
        return NAN_INPUT.active();
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        Entity mount = mc.player == null ? null : mc.player.getVehicle();
        NAN_INPUT.tick(nanInput.get() && controlsHorse(mount), nanDelay.get());
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        NAN_INPUT.reset();
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        NAN_INPUT.reset();
    }

    private boolean controlsHorse(Entity entity) {
        return isActive() && mc.player != null && mc.level != null
            && entity != mc.player && entity instanceof AbstractHorse
            && entity.getControllingPassenger() == mc.player;
    }

    public boolean travel(Entity e) {
        if (!controlsHorse(e)) return false;

        Vec2 in = mc.player.input.getMoveVector();
        double s = in.x, f = in.y;
        double x = 0, z = 0;
        double len = Math.sqrt(s * s + f * f);
        if (len > 1e-4) {
            s /= len; f /= len;
            double yaw = Math.toRadians(mc.player.getYRot());
            double sin = Math.sin(yaw), cos = Math.cos(yaw);
            x = (s * cos - f * sin) * speed.get();
            z = (f * cos + s * sin) * speed.get();
        }

        boolean up = mc.options.keyJump.isDown(), down = mc.options.keySprint.isDown();
        double y = up == down ? -glide.get() : up ? verticalSpeed.get() : -verticalSpeed.get();

        e.setDeltaMovement(x, y, z);
        e.move(MoverType.SELF, e.getDeltaMovement());
        e.resetFallDistance();
        return true;
    }
}
