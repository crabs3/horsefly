# horsefly

meteor addon that lets you fly horses

## how it works

wasd -> horse move horiz
space -> up
lctrl -> down

## how the NaN exploit works

`NaN` value getting shoved down the throat of grim prediction

### getting past the input check

`NaN` means "Not a Number". It has a valid representation in a packet 32bit float field but its comparisons behave differently from ordinary numbers:

```java
Float.NaN > 0.98f;   // false
Float.NaN < -0.98f;  // false
Float.NaN == Float.NaN; // false
Float.NaN != Float.NaN; // true
```

A check that rejects input only when its value exceeds the allowed range will therefore miss NaN:

```java
if (Math.abs(forward) > 0.98f || Math.abs(sideways) > 0.98f) {
    rejectInput();
}
```

Both comparisons evaluate to false when both inputs are NaN

### fucking the prediction

Clamping alone does not repair the input. For example:

```java
float input = Float.NaN;
double clamped = Math.min(0.98, Math.max(-0.98, input)); // NaN
double acceleration = clamped * movementSpeed;          // NaN
```

Grims [steering listener](https://github.com/GrimAnticheat/Grim/blob/d8b8ae4331f705c4992fe16b4b8689a6f28bee38/common/src/main/java/ac/grim/grimac/events/packets/PacketPlayerSteer.java#L34-L46) stores the received floats. Its [movement runner](https://github.com/GrimAnticheat/Grim/blob/d8b8ae4331f705c4992fe16b4b8689a6f28bee38/common/src/main/java/ac/grim/grimac/predictionengine/MovementCheckRunner.java#L670-L673) applies that min/max clamp, and its [horse simulation](https://github.com/GrimAnticheat/Grim/blob/d8b8ae4331f705c4992fe16b4b8689a6f28bee38/common/src/main/java/ac/grim/grimac/predictionengine/movementtick/MovementTickerHorse.java#L20-L28) uses those values to construct presumably real movement input. That path can introduce nonfinite values into the prediction motion

## settings

- `speed` - blocks per tick, default 1 (20 blocks/s)
- `vertical speed` - blocks per tick going up or down, default 0.5
- `glide` - how far you sink per tick when idle, default 0
- `nan input` - send NaN horse steering input through ViaFabricPlus, default on
- `nan delay` - ordinary input ticks between NaN inputs, 0–20, default 0 (every tick)

NaN input needs ViaFabricPlus to equal to or below 1.21.1

## install

Put the jar in your mods folder with meteor
