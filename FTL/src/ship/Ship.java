package ship;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import display.StdDraw;
import display.Vector2;
import module.Module;
import module.Reactor;
import module.WeaponControl;
import module.Motor;
import module.Shield;
import module.Shield.Protection;
import weapon.Ion;
import weapon.Ion.IonProjectile;
import weapon.Laser;
import weapon.Missile;
import weapon.Missile.MissileProjectile;
import weapon.Projectile;
import weapon.Weapon;

/**
 * A ship is the composed of modules, tiles and crew members.
 * The ship has a hull that can be damaged by an opponent's ship.
 */
public abstract class Ship {

	protected Vector2<Double>			position;		// The position of the ship
	protected int						totalHull;		// The total hull integrity of the ship
	protected int						currentHull;	// The current hull integrity of the ship
	protected int						nbMissiles;	    // The number of missile
	protected Reactor					reactor;		// The reactor of the ship
	protected WeaponControl				weaponControl;	// The weapon control system
	protected Motor						motor;			// The motor of the ship
	protected Shield					shield;			// The shield system
	protected Collection<CrewMember> 	crew;			// The crew members in the ship
	protected Collection<Tile>			layout;			// The layout of the ship
	protected boolean					isPlayer;		// Whether this ship is owned by the player
	protected Module[]					modules;		// The modules on the ship
	protected Collection<Projectile>	projectiles;	// The projectiles shot by the ship
	protected Tile						target;			// The targeted tile of the enemy ship
	protected CrewMember				selectedMember; // The currently selected crew member	
	protected boolean 					haveShield;

	/**
	 * @return the haveShield
	 */
	public boolean isHaveShield() {
		return haveShield;
	}

	/**
	 * @return the currentHull
	 */
	public int getCurrentHull() {
		return currentHull;
	}

	/**
	 * get the currentHull
	 */
	public void setCurrentHull(int currentHull) {
		this.currentHull = currentHull;
	}

	/**
	 * @return the target
	 */
	public Tile getTarget() {
		return target;
	}
	/**
	 * @return the layout
	 */
	public Collection<Tile> getLayout() {
		return layout;
	}

	/**
	 * @return the nbMissiles
	 */
	public int getNbMissiles() {
		return nbMissiles;
	}

	/**
	 * @param nbMissiles the nbMissiles to set
	 */
	public void setNbMissiles(int nbMissiles) {
		this.nbMissiles = nbMissiles;
	}
	/**
	 * @return the weaponControl
	 */
	public WeaponControl getWeaponControl() {
		return weaponControl;
	}

	/**
	 * Creates a Ship for the player or the opponent at the provided position.
	 * @param isPlayer whether the ship is for the player
	 * @param position the location to create it
	 */
	public Ship(boolean isPlayer, Vector2<Double> position) {
		this.isPlayer = isPlayer;
		this.position = position;
		crew = new ArrayList<CrewMember>();
		projectiles = new ArrayList<Projectile>();
		layout = new ArrayList<Tile>();
		haveShield = false;
	}

	// Main Methods

	/**
	 * Processes the action of the AI.
	 * @param player the enemy of the AI
	 */
	public void ai(Ship player) {
		if (!isPlayer) {
			aiCharge();//Fontion qui charge les armes
			aiShot(player);//Fonction qui tire
			aiRepair();//Fonction qui place un crewMember sur un module qui a besoin d'etre repare
		}
	}

	/**
	 * Shot for opponent (ai).
	 * @param player the ship of the player
	 */
	public void aiShot(Ship player) {
		if(System.currentTimeMillis()%3000 < 30) {

			int rndmTarget = (int)(Math.random() * player.getLayout().size());
			this.target = ((ArrayList<Tile>)player.getLayout()).get(rndmTarget);//On choisit un target random

			int rndmWeapon;
			int rndmMax = this.weaponControl.getAllocatedEnergy();
			if(rndmMax > 3)
				rndmMax = 3;

			if(rndmMax >= 2 && this.nbMissiles > 0) {
				rndmWeapon = (int)(Math.random() * (rndmMax));
			}
			else  {
				rndmWeapon = (int)(Math.random() * (rndmMax - 1));
			}
			
			this.shotWeapon(rndmWeapon); //On tire avec une arme random
			
			if(rndmMax >= 2 && this.nbMissiles > 0) {
				rndmWeapon = (int)(Math.random() * (rndmMax));
			}
			else  {
				rndmWeapon = (int)(Math.random() * (rndmMax - 1));
			}
			
			this.shotWeapon(rndmWeapon);//On tire avec une arme random
			

		}
	}

	/**
	 * Shot for opponent (AI).
	 */
	public void aiCharge() {
		if(System.currentTimeMillis()%3000 < 30) {//Toutes les 3 secondes on charge toutes les amres
			
			for(int i = 0; i < this.weaponControl.getWeapons().length - 1; i++){
				if(!this.weaponControl.getWeapon(i).isCharged()) {
					this.activeWeapon(i);
				}
			}

		}
	}

	/**
	 * Add a crewMemeber to a module which need to be repair.
	 */
	public void aiRepair() {
		
		for(int i = 0; i < this.modules.length; i++) {//on parcourt les modules
			if(this.modules[i].getAmountDamage() > 0 && this.modules[i].getMembers().isEmpty()) {//si un module est endommage et sans crewMember
				for(CrewMember c : crew) {
					for(int j = 0; j < ((ArrayList<Tile>)this.layout).size(); j++) {
						
						Tile t = ((ArrayList<Tile>)this.layout).get(j);
						
						if(t.getMembers().contains(c) && (t instanceof Module && ((Module)t).getAmountDamage() <= 0)) {
							this.selectedMember = c;//on deplace le crewMember dans le module qui a besoin d'etre repare
							this.moveCrewMemberTo(selectedMember, this.modules[i]);
							this.unselectCrewMember();
						}
					}
				}
			}
		}
	}

	/**
	 * Processes the time elapsed between the steps.
	 * @param elapsedTime the time elapsed since the last step
	 */
	public void step(double elapsedTime) {
		chargeWeapons(elapsedTime);
		processProjectiles(elapsedTime);
	}

	// Drawing Methods

	/**
	 * Draws the ship and all its components.
	 */
	public void draw() {
		drawTiles();
		for (Projectile p : projectiles)
			p.draw();
	}

	/**
	 * Draw the tiles of the ship.
	 */
	private void drawTiles() {
		for (Tile t : layout)
			t.draw();
	}

	/**
	 * Draws the HUD of the ship.
	 */
	public void drawHUD() {
		if (isPlayer)
			drawPlayerHUD();
		else
			drawOpponentHUD();
	}

	/**
	 * Draws the HUD of the player.
	 */
	private void drawPlayerHUD() {
		// Modules
		for (Module m : modules)
			m.drawHud(isPlayer);
		// Hull
		StdDraw.text(0.025, 0.97, "Hull");
		int j = currentHull;
		for (int i = 1; i <= totalHull; i++)
			if (j > 0) {
				StdDraw.filledRectangle(0.05+(i*0.015), 0.97, 0.005, 0.015);
				j--;
			} else
				StdDraw.rectangle(0.05+(i*0.015), 0.97, 0.005, 0.015);
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.rectangle(0.323, 0.5, 0.32, 0.49);
		StdDraw.setPenColor(StdDraw.BLACK);
	}

	/**
	 * Draws the HUD of the opponent.
	 */
	private void drawOpponentHUD() {
		for (Module m : modules) {
			if(m instanceof Shield)	
				m.drawHud(isPlayer);
		}
		int j = currentHull;
		for (int i = 1; i <= totalHull; i++)
			if (j > 0) {
				StdDraw.filledRectangle(0.67+(i*0.0075), 0.75, 0.0025, 0.0075);
				j--;
			} else
				StdDraw.rectangle(0.67+(i*0.0075), 0.75, 0.0025, 0.0075);
		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.rectangle(0.79, 0.53, 0.13, 0.25);
		StdDraw.setPenColor(StdDraw.BLACK);
	}

	// Crew Methods

	/**
	 * Check if a crew member is currently selected.
	 * @return whether a crew member is selected
	 */
	public boolean isCrewMemberSelected() {
		return selectedMember != null;
	}

	/**
	 * Unselects the selected crew member.
	 */
	public void unselectCrewMember() {
		if (!isCrewMemberSelected())
			return;
		selectedMember.unselect();
		selectedMember = null;
	}

	/**
	 * Selects a crew member.
	 */
	public void selectNextMember() {
		ArrayList<CrewMember> listCM = ((ArrayList<CrewMember>)this.crew);
		if(selectedMember == null) {
			selectedMember = listCM.get(0);
			selectedMember.select();
		}
		else {
			if(listCM.indexOf(selectedMember)+1 > listCM.size()-1) {
				selectedMember.unselect();
				selectedMember = listCM.get(0);
				selectedMember.select();
			}
			else {
				selectedMember.unselect();
				selectedMember = listCM.get(listCM.indexOf(selectedMember)+1);
				selectedMember.select();
			}
		}
	}

	/**
	 * Adds a crew member to the ship.
	 * @param member the crew member to add
	 */
	public void addCrewMember(CrewMember member) {
		Tile t = getFirstTile();
		if (t == null) {
			System.err.println("The ship is full ! Sorry " + member.getName() + "...");
			return;
		}
		crew.add(member);
		t.setCrewMember(member);
	}

	/**
	 * Move a crew member to a tile.
	 * @param c the crewMember to move
	 * @param t the tile to go
	 */
	public void moveCrewMemberTo(CrewMember c, Tile t) {
		ArrayList<Tile> listT = ((ArrayList<Tile>)this.layout);
		Tile save = null;
		for (Tile t2 : listT) {
			if(t2.hasCrewMember() && t2.isCrewMember(c)) {
				save = t2;
			}
		}
		if(t instanceof Module)
			((Module) t).setDamageTime(System.currentTimeMillis());
		t.setCrewMember(c);
		save.removeCrewMember(selectedMember);
	}

	/**
	 * Move a crew member to the right.
	 */
	public void moveCrewMemberLeft() {
		if (selectedMember != null) {
			ArrayList<Tile> listT = ((ArrayList<Tile>)this.layout);
			CrewMember m = null;
			Tile save = null;
			for (Tile t : listT) {
				if(t.hasCrewMember() && t.isCrewMember(selectedMember)) {
					m = selectedMember;
					save = t;
				}
			}
			if (listT.indexOf(save)+1 != listT.size()) {
				Tile tile = listT.get(listT.indexOf(save)+1);
				if(tile instanceof Module)
					((Module) tile).setDamageTime(System.currentTimeMillis());
				tile.setCrewMember(m);
				save.removeCrewMember(selectedMember);
			}
		}
		else {
			System.err.println("Please Select a crew Member!!");
		}
	}

	/**
	 * Move a crew member to the left.
	 */
	public void moveCrewMemberRight() {
		if (selectedMember != null) {
			ArrayList<Tile> listT = ((ArrayList<Tile>)this.layout);
			CrewMember m = null;
			Tile save = null;
			for (Tile t : listT) {
				if(t.hasCrewMember() && t.isCrewMember(selectedMember)) {
					m = selectedMember;
					save = t;
				}
			}
			if(listT.indexOf(save) != 0) {
				Tile tile = listT.get(listT.indexOf(save)-1);
				if(tile instanceof Module)
					((Module) tile).setDamageTime(System.currentTimeMillis());
				tile.setCrewMember(m);
				save.removeCrewMember(selectedMember);
			}
		}
		else {
			System.err.println("Please Select a crew Member!!");
		}
	}

	// Layout Methods

	/**
	 * Adds a tile to the ship.
	 * @param t the tile to add
	 */
	protected void addTile(Tile t) {
		layout.add(t);
	}

	/**
	 * Gives an empty tile of the ship
	 * @return a tile empty of crew member
	 */
	public Tile getEmptyTile() {
		Iterator<Tile> it = layout.iterator();
		while(it.hasNext()) {
			Tile t = it.next();
			if (!t.hasCrewMember())
				return t;
		}
		return null;
	}
	/**
	 * Gives an empty tile of the ship
	 * @return a tile empty of crew member
	 */
	public Tile getTileWithoutWeapon() {
		Iterator<Tile> it = layout.iterator();
		while(it.hasNext()) {
			Tile t = it.next();
			if (t.getWeapon() == null)
				return t;
		}
		return null;
	}

	/**
	 * Gives the first tile of the ship.
	 * @return the first tile of the ship
	 */
	private Tile getFirstTile() {
		return layout.iterator().next();
	}

	// Energy Methods

	/**
	 * Decreased the energy allocated in the module. 
	 * @param module the module to remove energy from
	 */
	public void removeEnergy(int module) {
		if (module >= modules.length)
			return;
		if (modules[module].removeEnergy()) {
			reactor.addEnergy();
			if(modules[module] == weaponControl) {
				for (int i = 0; i < weaponControl.getWeapons().length; i++) {
					if (weaponControl.getWeapons()[i] instanceof Laser)
						(weaponControl.getWeapons()[i]).setShotDamage(weaponControl.getAllocatedEnergy());
					if (weaponControl.getWeapons()[i] instanceof Ion)
						(weaponControl.getWeapons()[i]).setTimeDeactivation(weaponControl.getAllocatedEnergy());
				}
			}
			else if(modules[module] == shield) {
				if(shield.getAllocatedEnergy()%2 == 0) {
					shield.removeLastProtection();
				}
				else{
					shield.getProtections().get((shield.getProtections()).size()-1).setFastCharge(false);
				}
			}
		}
	}


	/**
	 * Increases the energy allocated in the module.
	 * @param module the module to add energy to
	 */
	public void addEnergy(int module) {
		if (module >= modules.length)
			return;
		if (reactor.getAllocatedEnergy() > 0 && modules[module].addEnergy()) {
			reactor.removeEnergy();
			if(modules[module] == weaponControl) {
				for (int i = 0; i < weaponControl.getWeapons().length; i++) {
					if (weaponControl.getWeapons()[i] instanceof Laser)
						(weaponControl.getWeapons()[i]).setShotDamage(weaponControl.getAllocatedEnergy());
					if (weaponControl.getWeapons()[i] instanceof Ion)
						(weaponControl.getWeapons()[i]).setTimeDeactivation(weaponControl.getAllocatedEnergy());
				}
			}
			else if(modules[module] == shield) {
				if(shield.getAllocatedEnergy()%2 == 1) {
					Protection p = shield.new Protection(false, true);
					shield.addProtection(p);
				}
				else {
					shield.getProtections().get((shield.getAllocatedEnergy()-1)/2).setFastCharge(true);
				}
			}
		}
	}

	// Weapon Methods

	/**
	 * Deactivates a weapon. 
	 * @param weapon the weapon to deactivate
	 */
	public void deactiveWeapon(int weapon) {
		weaponControl.deactiveWeapon(weapon);
	}

	/**
	 * Activates a weapon. 
	 * @param weapon the weapon to activate
	 */
	public void activeWeapon(int weapon) {
		weaponControl.activeWeapon(weapon);
	}

	/**
	 * Charges the weapons by the time provided
	 * @param time the time to charge the weapons by
	 */
	public void chargeWeapons(double time) {
		weaponControl.chargeTime(time);
	}

	/**
	 * Shots a weapon.
	 * @param weapon the weapon to shot
	 */
	public void shotWeapon(int weapon) {
		if(weaponControl.getWeapon(weapon).isCharged() && weaponControl.getWeapon(weapon) instanceof Missile && nbMissiles > 0) {

			if (target!=null) {

				double xSpeed = 1;
				double ySpeed = 0;
				if(!isPlayer) {
					xSpeed = 0;
					ySpeed = 1;
				}

				Projectile p = weaponControl.shotWeapon(weapon, getWeaponTile(weaponControl.getWeapon(weapon)), new Vector2<Double>(xSpeed, ySpeed));

				if (p != null)
					projectiles.add(p);

				nbMissiles--;
				if(nbMissiles == 0)
					this.weaponControl.deactiveWeapon(weapon);

				if(nbMissiles < 0)
					nbMissiles = 0;
			}
		}

		else if(!(weaponControl.getWeapon(weapon) instanceof Missile)) {

			if (target!=null && weaponControl.getAllocatedEnergy()>0) {

				double xSpeed = 1;
				double ySpeed = 0;
				if(!isPlayer) {
					xSpeed = 0;
					ySpeed = 1;
				}

				Projectile p = weaponControl.shotWeapon(weapon, getWeaponTile(weaponControl.getWeapon(weapon)), new Vector2<Double>(xSpeed, ySpeed));

				if (p != null)
					projectiles.add(p);
			}
		}
	}

	/**
	 * Gives the tile on which the weapon is.
	 * @param w the weapon to get the tile from
	 * @return the tile on which the weapon is attached
	 */
	private Tile getWeaponTile(Weapon w) {
		for (Tile t : layout)
			if (t.getWeapon() == w)
				return t;
		return null;
	}


	// Projectile Methods

	/**
	 * Processes the projectiles with the time elapsed since
	 * the last processing.
	 * @param elapsedTime the time elapsed since the last call
	 */
	private void processProjectiles(double elapsedTime) {
		for (Projectile p : projectiles)
			if (p != null)
				p.step(elapsedTime);
	}

	/**
	 * Gives the projectiles shot by the ship.
	 * @return A collection of projectiles
	 */
	public Collection<Projectile> getProjectiles(){
		return projectiles;
	}

	/**
	 * Applies the damage a projectile did.
	 * @param p the projectile to process
	 */
	public void applyDamage(Projectile p, Tile t) {

		if(t instanceof Module)
			((Module) t).setDamageTime(System.currentTimeMillis());

		double random = Math.random();
		
		if(random >= ((double)motor.getAllocatedEnergy())*0.05) {
			boolean oneProtectionActive = false;
			for(int i = shield.getProtections().size()-1; i >=0 && !oneProtectionActive; i--) {
				if(shield.getProtections().get(i).isCharged()) {
					oneProtectionActive = true;	
				}
			}
			if(oneProtectionActive && !(p instanceof MissileProjectile)) {
				shield.deactivateLastProtection();
			}
			else {
				int d = p.getDamage();
				this.currentHull = this.currentHull-d;
				if (t==motor) {
					applyDamageLevelModule(d, motor);
				}
				else if(t==shield) {
					applyDamageLevelModule(d, shield);
				}
				else if(t==weaponControl) {
					applyDamageLevelModule(d, weaponControl);
				}
			}
		}
		else {
			StdDraw.text(this.position.getX(), this.position.getX(), "Esquive");
		}
	}

	/**
	 * Function which make damage to the module shot
	 * @param d the amount of damage
	 * @param m the module shot
	 */
	public void applyDamageLevelModule(int d, Module m) {
		if(d == 0 || m.isDisactivated()) {
			return;
		}
		
		if(m.getAllocatedEnergy() >0 && m.getAllocatedEnergy() >= m.getCurrentLevel() - d) {
			
			int a = d - ((m.getCurrentLevel() - m.getAmountDamage()) - m.getAllocatedEnergy());
			
			reactor.setAllocatedEnergy(reactor.getAllocatedEnergy() + a);
			
			if(reactor.getAllocatedEnergy()>reactor.getCurrentLevel())
				reactor.setAllocatedEnergy(reactor.getCurrentLevel());
			
			m.setAllocatedEnergy(m.getAllocatedEnergy() - a);
			
			if(m.getAllocatedEnergy() < 0)
				m.setAllocatedEnergy(0);
		}
		
		if(m.getAmountDamage()+d <= m.getCurrentLevel())
			m.setAmountDamage(m.getAmountDamage()+d);
		else
			m.setAmountDamage(m.getCurrentLevel());
	}

	/**
	 * Function which repairs modules
	 */
	public void repairModule() {
		ArrayList<CrewMember> listCM = ((ArrayList<CrewMember>)this.crew);
		
		for (int i=0; i<modules.length; i++) {
			if(!modules[i].isDisactivated()) {
				if(modules[i] != reactor) {
					int nCrewMember = 0;
					for (CrewMember c : listCM) {
						if(modules[i].isCrewMember(c))
							nCrewMember++;
					}
					if(nCrewMember > 0) {
						if (System.currentTimeMillis() > modules[i].getDamageTime() + 2000/nCrewMember) {
							if (modules[i].getAmountDamage()>0 && !modules[i].isDisactivated()) {
								modules[i].setDamageTime(System.currentTimeMillis());
								modules[i].setAmountDamage(modules[i].getAmountDamage()-1);
								if(!isPlayer && !modules[i].isDisactivated()) {
									this.addEnergy(i);
								}
							}
						}

					}
				}
			}
		}
	}

	/**
	 * Function which repairs protections of the shield
	 */
	public void repairShield() {
		
		for (Protection p : shield.getProtections()) {
			if(p.isCharged()) {
				this.haveShield = true;
				break;
			}
			this.haveShield = false;
		}
		
		for (Protection p : shield.getProtections()) {
			
			if(!p.isCharged() && p.isFastCharge() && System.currentTimeMillis()>p.getTime()+1500) {
				p.setCharged(true);
				for (Protection pro : shield.getProtections()) {
					if(!pro.isCharged()) {
						pro.setTime(System.currentTimeMillis());
					}
				}
			}
			
			else if(!p.isCharged() && !p.isFastCharge() && System.currentTimeMillis()>p.getTime()+2000) {
				p.setCharged(true);
				for (Protection pro : shield.getProtections()) {
					if(!pro.isCharged()) {
						pro.setTime(System.currentTimeMillis());
					}
				}
			}
			
		}
	}

	/**
	 * Deactivate a module shot by an Ion projectile.
	 * @param p the projectile to process
	 * @param t the tile to deactivates
	 */
	public void deactivation(Projectile p, Tile t) {
		if(p instanceof IonProjectile) {
			for(int i=0; i<modules.length; i++) {
				if(t==modules[i]) {
					modules[i].setDisactivated(true);
					modules[i].setEnergyBeforeDisactivation(modules[i].getAllocatedEnergy());
					modules[i].setAllocatedEnergy(0);
					modules[i].setStopTime(System.currentTimeMillis());
				}
			}
		}
	}

	/**
	 * Activates the modules
	 * @param opponent the ship opponent
	 */
	public void activation(Ship opponent) {
		
		int lvlWeaponControlOpponent = opponent.weaponControl.getAllocatedEnergy();
		
		for(int i=0; i<modules.length; i++) {
			if(modules[i].isDisactivated()) {
				if (System.currentTimeMillis() > modules[i].getStopTime()+1000*lvlWeaponControlOpponent) {
					modules[i].setAllocatedEnergy(modules[i].getEnergyBeforeDisactivation());
					modules[i].setDisactivated(false);
				}
			}
		}
		
	}

	/**
	 * Improve the ship at the end of the level
	 */
	public void nextShip() {
		
		this.setCurrentHull(30);//Remet la vie à 30
		
		for(int i = 0; i < this.modules.length; i++) {
			
			if(this.modules[i].getCurrentLevel() + 1 <= this.modules[i].getMaxLevel()) {
				this.modules[i].setCurrentLevel(this.modules[i].getCurrentLevel() + 1);
				this.addEnergy(i);
			}
			
		}
	}

	// Aiming Methods

	/**
	 * Aims the guns up.
	 * @param opponent the ship to aim at
	 */
	public void aimUp(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		}
		else if(((ArrayList<Tile>)opponent.layout).indexOf(target) > 0){
			target.unmarkTarget();
			target = ((ArrayList<Tile>) opponent.layout).get(((ArrayList<Tile>)opponent.layout).indexOf(target) - 1);
			target.markTarget();
			return;
		}
		else {
			System.err.println("Aiming System Critical Failure !");
		}




	}


	/**
	 * Aims the guns down.
	 * @param opponent the ship to aim at
	 */
	public void aimDown(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		}
		else if(((ArrayList<Tile>)opponent.layout).indexOf(target) < ((ArrayList<Tile>)opponent.layout).size()-1){
			target.unmarkTarget();
			target = ((ArrayList<Tile>) opponent.layout).get(((ArrayList<Tile>)opponent.layout).indexOf(target) + 1);
			target.markTarget();
			return;
		}
		else {
			System.err.println("Aiming System Critical Failure !");
		}
	}

	/**
	 * NOUS N'UTILISONS PAS CES FONCTIONS
	 * Aims the guns left.
	 * @param opponent the ship to aim at
	 */
	public void aimLeft(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		}
		System.err.println("Aiming System Critical Failure !");
	}

	/**
	 * NOUS N'UTILISONS PAS CES FONCTIONS
	 * Aims the guns right.
	 * @param opponent the ship to aim at
	 */
	public void aimRight(Ship opponent) {
		if (target == null) {
			target = opponent.getFirstTile();
			target.markTarget();
			return;
		}
		System.err.println("Aiming System Critical Failure !");
	}

}
