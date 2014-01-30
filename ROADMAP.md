## VALLEY OF BONES

### Chapter 1: Marine (April)

- Basic game mechanics:
    - Move
    - Attack
    - Build
- Multiplayer-only

### Chapter 2: Sniper (May)

- Add a few new units
    - Sniper

- Abilities
    - Sniper has stealth
        - activate with stealth
        - cannot be hit while in stealth, has 0.5x movement
        - cannot fire while in stealth
        - cannot re-enter stealth on the same turn
    - Marine has detection
        - when in range, can shoot stealthed units

- Control points
    - Grant small income/food --> less income/food from base
    - Mechanics:
        - can be captured by moving unit to adjacent space when uncontrolled
        - gains 5 HP per turn per adjacent unit up to 30 HP total (does not increase when 'contested' - enemy unit also adjacent)
        - control resets when "destroyed"
        - spawns "destroyed" (uncontrolled)

### Chapter 3: Mech (December)

+ Central server
    + Keeps a master server list, updates when users start 'public' servers
    + also allow configurable port
    + game reports
- UI Improvements
    + updated look/feel
    - Finish unimplemented features:
        + In game menu (Surrender goes here)
        x Refund
        + Activate/deactivate cloak
        + Zoom
        + Better unit boxes
+ AI
- Android version
    + logo
    - description
    - single page website (at least unit descriptions)
+ Gameplay changes
    + heavy infantry (bazooka/missile launcher)
    + Sabotuer --> 'spy'
    + Add 2 new mech units
        + light mech
        + artillery
            + splash damage
    + Units built don't grant visibility until end of turn (to prevent 'walking' along the map)
- Basic sounds
+ Basic animations

### 0.1.x

+ Sniper cloaking:
    + should not be able to enter cloak if has fired or moved twice
+ Pause/unpause
- 3rd map
+ selected build item shows stats in info panel
- hexagonal icons

**Note: 0.1.x will be the last minor version that includes major gameplay changes, from now on, the rev # will only be bugfixes/artwork changes**

### 0.2.0

- Multiplayer/Game Setup improvements
    - Ready/not-ready
    - lobby chat
    - select color
    - select spawn
    - 1st move
    - map thumbs
    - timing rules

- Tower/Castle +1 attack speed?
- Diminishing returns:
    - Instead of all (15/2), towers give (25/4, 20/3, 10/2, 5/1) - alternatively, how about (20/2, 15/2, 10/2, 5/2) - only $50 total
- Increasing returns: base gives +$/turn as game goes on (ie: +$5 every 3 turns, will need a cap)

### 0.3.0

- Global server
    - /players
    - server auth?

- Server
    - CLI only

- Tower upgrades:
    - +20HP, $40-60
    - +1 attack speed, $40-60
    - +1 sight range, $30-40
    - + detection, $30-40
    - All upgrades destroyed on change of possession
    - Can't upgrade till tower in green?
    - auto-heal tower?
- Garrisons?
- Healing in range of base/to
- context-sensitive upgrade menu
- Veteran units


### Things

- Multiplayer/Game Setup improvements
    - Ready/not-ready
    - lobby chat
    - select color
    - select spawn
    - 1st move
    - map thumbs
    - timing rules

- Global server
    - /players
    - server auth?

- Client
    - Pause/Unpause

- Server
    - CLI only

- AI
    - Finish UnitFSM, parameterize (to allow configurable behavior)
    - Overall AI (building logic, goal setting keys)

- Gameplay changes
    - Tower/Castle +1 attack speed?
    - Tower upgrades:
        - +20HP, $40-60
        - +1 attack speed, $40-60
        - +1 sight range, $30-40
        - + detection, $30-40
        - All upgrades destroyed on change of possession
        - Can't upgrade till tower in green?
        - auto-heal tower?
    - Garrisons?
    - Healing in range of base/tower?
    - Diminishing returns:
        - Instead of all (15/2), towers give (25/4, 20/3, 10/2, 5/1) - alternatively, how about (20/2, 15/2, 10/2, 5/2) - only $50 total
    - Increasing returns: base gives +$/turn as game goes on (ie: +$5 every 3 turns, will need a cap)
    - Sniper cloaking:
        - should not be able to enter cloak if has fired or moved twice

- UI changes
    - selected build item shows stats in info panel
    - context-sensitive upgrade menu

- Other
    - Veteran units
    - 3rd map that emphasizes choke points


### Later

- Another race
    - set up tension between human/alien races
    - biological race that 'evolves' similarly to zerg?
- Gameplay changes
    - Attachments for base
        - "Defense platform" --> grants extra attack/damage
        - "Research facility" --> allows upgrades
    - Rewards for kills --> also allows less income from base
