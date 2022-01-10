package module;

import display.Vector2;

/**
 * A Motor is a module
 */
public class Motor extends Module {
	
	/**
	 * Construct a Motor owned by the player or the opponent.
	 * The Motor tile is drawn at the location given in tilePos.
	 * The Motor HUD is drawn at the location given in hudPos.
	 * The initialLevel of the Motor is the amount of energy it
	 * provides in the ship.
	 * @param hudPos position at which to draw the HUD
	 * @param tilePos position at which to draw the tile
	 * @param isPlayer whether it belongs to the player
	 * @param initialLevel initial amount of energy which it can provide
	 */
	public Motor(Vector2<Double> hudPos, Vector2<Double> tilePos, boolean isPlayer, int initialLevel) {
		super(hudPos, tilePos, isPlayer);
		name = "Motor";
		maxLevel = 8;
		currentLevel = initialLevel;
		allocatedEnergy = 0;
		amountDamage = 0;
		canBeManned = true;
	}
	
}
