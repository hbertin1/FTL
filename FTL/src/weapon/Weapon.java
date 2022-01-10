package weapon;

import display.Vector2;

/**
 * A weapon goes into a WeaponControl to be activated and
 * deactivated. A weapon charges until it can shot and waits
 * for a shot command from the captain of the ship.
 * If deactivated, the weapon loses it charges progressively.
 */
public abstract class Weapon {

	protected String		name;				//The weapon's name
	protected int 			requiredPower;		//The Power required to shoot with the weapon
	protected int 			chargeTime;			//The time required to charge a weapon
	protected int 			timeDeactivation;	//the Time of deactivation
	protected int 			shotDamage;			//the amount of damage allocated to a weapon
	protected boolean 		activated;			//if the weapon is activated
	protected double		currentCharge;		//the charge level of the weapon
	
	/**
	 * Creates a projectile at given position and aiming at the
	 * provided direction.
	 * @param pos position of the projectile when shot
	 * @param dir direction of the projectile
	 * @return the created projectile
	 */
	public abstract Projectile shot(Vector2<Double> pos, Vector2<Double> dir);

	/**
	 * Activates the weapon.
	 */
	public void activate() {
		activated = true;
	}
	
	/**
	 * Deactivates the weapon.
	 */
	public void deactive() {
		activated = false;
	}
	
	/**
	 * Checks whether the weapon is activated.
	 * @return whether the weapon is activated.
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * Gives the required power to activate the weapon.
	 * @return the required power 
	 */
	public int getRequiredPower() {
		return requiredPower;
	}
	
	/**
	 * Gives the name of the weapon.
	 * @return the name of the weapon
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gives the total required charge time.
	 * @return the total required charge time
	 */
	public int getChargeTime() {
		return chargeTime;
	}
	
	/**
	 * Gives the current charge.
	 * @return the current charge
	 */
	public double getCurrentCharge() {
		return currentCharge;
	}

	/**
	 * Charges the weapon by the provided time.
	 * @param time time by which to charge the weapon
	 */
	public void charge(double time) {
		if (currentCharge + time < 0)
			currentCharge = 0;
		else if (currentCharge + time >= chargeTime)
			currentCharge = chargeTime;
		else
			currentCharge = currentCharge + time;
	}
	
	/**
	 * Resets the charge of the weapon back to zero.
	 */
	public void resetCharge() {
		currentCharge = 0;
	}

	/**
	 * Checks whether the weapon is fully charged.
	 * @return whether the weapon is fully charged
	 */
	public boolean isCharged() {
		return currentCharge >= chargeTime;
	}

	/**
	 * @return the time of deactivation
	 */
	public int getTimeDeactivation() {
		return timeDeactivation;
	}

	/**
	 * Set the time of deactivation
	 * @param timeDeactivation the time of deactivation
	 */
	public void setTimeDeactivation(int timeDeactivation) {
		this.timeDeactivation = timeDeactivation;
	}

	/**
	 * Set the damages of the weapon
	 * @param shotDamage the damage of the weapon
	 */
	public void setShotDamage(int shotDamage) {
		this.shotDamage = shotDamage;
	}
	
}