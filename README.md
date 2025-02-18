## Introduction
Protects your crops from being trampled by both players and mobs.


## Features
- **Crop Protection:** Prevents players and mobs from walking over and damaging your crops.
- **Messages:** A message will be displayed to the player saying that the action has been blocked.
- **Sound Effects:** A sound effect will be played indicating that the action has been blocked.

## Permissions
- **antitrample.use:** Under whitelist mode, granting this permission stops crop tramping.
- **antitrample.ignored:** Players with this permission will be allowed to trample on crops.
- **antitrample.reload:** Players with this permission will be able to reload the configuration of antitrample.

## Configuration
<details>
  <summary>config.yml</summary>

  ```yml
  # Change the permission checking mode for trampling crops.
  # "BYPASS": Anti-trample prevention applies to all players unless they have the bypass permission.
  # "WHITELIST": Anti-trample prevention only applies to players with the use permission.
  PermissionMode: "BYPASS"
    
  # Enable or disable prevention of mobs trampling crops.
  # Set to false if you want to allow mobs to trample crops as usual.
  PreventMobs: true
    
  # Message to display wen a player attempts to trample crops.
  # Leave this field empty to disable message notifications.
  Message: "&cYou do not have permission to trample on crops."
    
  # Sound to play when a player attempts to trample on crops.
  # Use a sound identifier like "BLOCK_NOTE_BLOCK_BASS" for a note sound.
  # Leave this field empty to disable sound notifications
  Sound: "BLOCK_NOTE_BLOCK_BASS"
  ```

</details>

## Note:
If you have **ANY** issues with this plugin please make a GitHub issue and I will try my best to resolve the issue.