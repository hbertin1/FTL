package ship;
import display.Vector2;
import module.Module;
import module.Reactor;
import module.WeaponControl;
import weapon.Ion;
import weapon.Laser;
import weapon.Missile;
import weapon.Weapon;
import module.Motor;
import module.Shield;

public class PlayerShip extends Ship {
	
	/**
	 * Creates a Ship for the player at the provided position.
	 * @param isPlayer whether the ship is for the player
	 * @param position the location to create it
	 */
	public PlayerShip(boolean isPlayer, Vector2<Double> position) {
		// Creates the ship
		super(isPlayer, position);
		
		// Sets the characteristics of the ship.
		totalHull 		= 30;
		currentHull		= 30;
		modules = new Module[4];
		nbMissiles = 3;
		
		
		reactor = new Reactor(new Vector2<Double>(0.025, 0.02), getNextTilePosition(),isPlayer, 3);
		addTile(reactor);
		
		
		
		weaponControl = new WeaponControl(new Vector2<Double>(0.08, 0.02), getNextTilePosition(), isPlayer, 4, 4);
		addTile(weaponControl);
		
		motor = new Motor(new Vector2<Double>(0.5, 0.02), getNextTilePosition(),isPlayer, 1);
		addTile(motor);
		
		shield = new Shield(new Vector2<Double>(0.58, 0.02), getNextTilePosition(),isPlayer, 1);
		addTile(shield);
		
		// Assigns the modules
		modules[0] = reactor;
		modules[1] = weaponControl;
		modules[2] = motor;
		modules[3] = shield;
		
		// Creates the gun of the ship
		Weapon i = new Ion();
		Weapon l = new Laser();
		Weapon m = new Missile();
		

		// Assigns the gun to the weapon control
		weaponControl.addWeapon(l);
		weaponControl.addWeapon(i);
		weaponControl.addWeapon(m);
		
		// Places the weapon at the front
		reactor.setWeapon(l);
		weaponControl.setWeapon(i);;
		motor.setWeapon(m);
		
		// Adds a crew member to the ship
		addCrewMember(new CrewMember("Hugo"));
		
	}
	
	private Vector2<Double> getNextTilePosition() {
		if (isPlayer)
			return new Vector2<Double>(position.getX()-(layout.size()*0.02), position.getY()); 
		else
			return new Vector2<Double>(position.getX(), position.getY()-(layout.size()*0.02));
	}

}