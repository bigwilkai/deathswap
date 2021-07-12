# Minecraft Deathswap
A high quality take on the Deathswap minigame with a large amount of pretty cool features!  

![deathswap-1](https://user-images.githubusercontent.com/78887935/125244693-86b0f980-e2e7-11eb-997d-1253d76c4ac6.png)  

## Concept
The Objective of Deathswap is to kill all other Players, the catch is though that you can't actually attack them.  
Instead after a certain amount of time you will be swapped and must kill the other Players by swapping them into a deadly situation.  
  
Examples could be:
> Falling from a high place.  
> In a pool of lava.  
> About-to-explode desert temple.  
> <strike>Miami</strike>

## Features  
### 2+ Players!  
Deathswap has support for more than 2 Players!  
> Instead of Players being swapped they will be teleported to a specific individual.  
> A -> B -> C -> A  
  
> This also goes the opposite direction every other round.  
> A <- B <- C <- A  

*To make things a little more fair*  

### 8 Config Settings  
Deathswap provides 8 Config Options to allow you to control the Games behaviour!  

> ### Swap Interval  
> The amount of time in seconds between each swap.  

> ### Swap Interval Variation  
> The amount that the Swap Interval can vary.  
> e.g, If the Swap Interval is 300 (5 minutes) and this is 120 (2 minutes)  
> then the time between swaps can be 3 - 7 minutes.  

> ### Grace Period  
> The amount of time in seconds in which no swaps will occur.  

> ### Mob Spawn Rate  
> A % cap for the amount of Hostile Mobs that can spawn.  
> 100% means mobs will spawn normally.  
> 50% means only 50% of mobs will spawn.  
> 0% means no mobs will spawn.  

> ### Allow PvP  
> Controls whether Players can attack each other or not.  

> ### Allow Nether  
> Controls whether Players can light Nether Portals or not.  

> ### Show Timer  
> Displays a Timer at the top of your Game showing how long is left of the Grace Period or until the next swap.  

> ### Kick Players on Leave  
> Whether or not Deathswap should remove Players from the Game if they leave.  

### Commands  
*All Commands start with /deathswap.*  
`start` - Starts the Deathswap.  
`cancel` - Cancel the Deathswap.  
`players <add | remove | list>` - Adds, Removes or Lists Players.  
`help` - Displays a list of Commands.  
`config <get | set | reset | help>` - Allows for the viewing and changing of Config Settings without having to change the config.yml.  
