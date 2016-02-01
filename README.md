AndWars
=======

AndWars is a turn-based strategy game of territorial conquest and unit control where the goal is to defeat your enemies. The game is based on the classic Palm strategy game [Palmwars](http://leggettnet.org.uk/palmstuff/palmwars.html).

# Rules

* Maps are divided into interconnected *stars*. Each star can hold up to 9 units of the same player.
* On each turn, each player has 2 moves. Units in one star can be moved only to a connected star.
* If the target star is unoccupied, the moved units will conquer it with no resistance. If the target star belongs to an enemy, a battle will take place.
* At the end of each turn, each star which is linked to 2 or more of the same player's stars will gain an extra unit.
* The game ends when you defeat all the units of your enemies.

# Game modes

* Human vs Computer
* Human vs Human (offline)

# Community

If you have any question or you want to submit your own map, don't hesitate to access to our [Community] (https://plus.google.com/communities/116804771459012895746).

# Configuration

After you clone the repository you have to add the following lines to the file `gradle.properties`:

```ini
RELEASE_KEY_ALIAS      = ???
RELEASE_KEY_PASSWORD   = ???
RELEASE_STORE_PASSWORD = ???
RELEASE_STORE_FILE     = ???
```

<a href="https://play.google.com/store/apps/details?id=com.mauriciotogneri.andwars" target="_blank">
	<img src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" align="left" height="72" >
</a>