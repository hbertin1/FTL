package main;
import java.util.ArrayList;
import java.util.Collection;

import display.Button;
import display.StdDraw;
import display.Vector2;
import ship.CrewMember;
import ship.OpponentShip;
import ship.PlayerShip;
import ship.Ship;
import ship.Tile;
import weapon.Ion;
import weapon.Laser;
import weapon.Missile;
import weapon.Missile.MissileProjectile;
import weapon.Projectile;
import weapon.Weapon;
import module.Module;
import module.Reactor;

/**
 * The world contains the ships and draws them to screen.
 */
public class World {

	private Bindings 	bind;	// The bindings of the game.
	private long 		time;	// The current time
	private int			level;  // The level of the player
	private boolean		readyToPlay; //If the ship is ready for a new level
	Ship player;				// The ship of the player
	Ship opponent;				// The ship of the opponent

	/**
	 * Creates the world with the bindings, the player ship
	 * and the opponent ship.
	 */
	public World() {
		bind = new Bindings(this);
		player = new PlayerShip(true, new Vector2<Double>(0.3, 0.5));
		opponent = new OpponentShip(false, new Vector2<Double>(0.8, 0.5));
		opponent.nextShip();
		time = System.currentTimeMillis();
		level = 1;
		readyToPlay = true;
		endBtns = new EndButton[4];
	}

	/**
	 * Processes the key pressed.
	 */
	public void processKey(){
		this.bind.processKey();
	}

	/**
	 * Makes a step in the world.
	 */
	public void step() {

		player.step(((double) (System.currentTimeMillis()-time))/1000);
		opponent.step(((double) (System.currentTimeMillis()-time))/1000);

		opponent.ai(player);

		processHit(player.getProjectiles(), true);
		processHit(opponent.getProjectiles(), false);

		time = System.currentTimeMillis();

		//La fonction activation active un module si il a ete desactive par un Ion
		player.activation(opponent);
		opponent.activation(player);
		
		//La fonction repairModule repare un module si il est endommage
		player.repairModule();
		opponent.repairModule();

		//La fonction repairShield repare le shield si il est endommage
		player.repairShield();
		opponent.repairShield();

	}

	/**
	 * Processes the projectiles hit
	 * @param projectiles collection of projectiles to check for hit
	 * @param isPlayer whether the own of the projectiles is the player
	 */
	private void processHit(Collection<Projectile> projectiles, boolean isPlayer) {
		ArrayList<Projectile> toDelete = new ArrayList<Projectile>();//Liste des projectiles à supprimer (ceux qui touchent leur cible)

		for (Projectile p : projectiles) {
			if (p != null) {
				if(p.isOutOfScreen()) {//On supprime le projectile si il sort de l'ecran
					System.out.println("Projectile out of screen");
					toDelete.add(p);
				}

				else if(!isPlayer && !(p instanceof MissileProjectile) &&!p.isOutOfCircle(0.277, 0.489, 0.1) && player.isHaveShield()) {
					//Si le joueur a un shield et que le projectile  n'est pas un missile
					player.deactivation(p, opponent.getTarget());//On desactive le module vise (Ne marche que pour Ion)
					player.applyDamage(p, opponent.getTarget());//On applique les dommages
					System.out.println("Shield player touché");
					toDelete.add(p);//Et on supprime le projectile
				}

				else if(isPlayer && !(p instanceof MissileProjectile) && !p.isOutOfCircle(0.79, 0.489, 0.1) && opponent.isHaveShield()) {
					//Si l'opponent a un shield et que le projectile  n'est pas un missile
					opponent.deactivation(p, player.getTarget());//On desactive le module vise (Ne marche que pour Ion)
					opponent.applyDamage(p, player.getTarget());//On applique les dommages
					System.out.println("Shield opponent touché");
					toDelete.add(p);//Et on supprime le projectile
				}

				else if(isPlayer && p.isOutOfRectangle(0.323, 0.5, 0.32, 0.49) && p.isOutOfRectangle(0.79, 0.53, 0.13, 0.25)) {
					//Si le projectile sort du rectangle vert:
					//On le place aleatoirement sur un cote du rectangle rouge
					double rndm = Math.random();
					if(rndm < 0.25)
						p.setPosition(0.79 + 0.13, Math.random()*0.5 + 0.53 - 0.25);
					else if(rndm < 0.5)
						p.setPosition(0.79 - 0.13, Math.random()*0.5 + 0.53 - 0.25);
					else if(rndm < 0.75)
						p.setPosition(Math.random()*0.26 + 0.79 - 0.13, 0.53 + 0.25);
					else
						p.setPosition(Math.random()*0.26 + 0.79 - 0.13, 0.53 - 0.25);

					p.computeDirection(player.getTarget().getCenterPosition());//Et on ajuste la direction du projectile
				}

				else if(!isPlayer && p.isOutOfRectangle(0.79, 0.53, 0.13, 0.25) && p.isOutOfRectangle(0.323, 0.5, 0.32, 0.49)) {
					//Si le projectile sort du rectangle rouge:
					//On le place aleatoirement sur un cote du rectangle vert
					double rndm = Math.random();
					if(rndm < 0.25)
						p.setPosition(0.323 + 0.32, Math.random()*0.49*2 + 0.5 - 0.49);
					else if(rndm < 0.5)
						p.setPosition(0.323 - 0.32, Math.random()*0.49*2 + 0.5 - 0.49);
					else if(rndm < 0.75)
						p.setPosition(Math.random()*0.32*2 + 0.323 - 0.32, 0.5 + 0.49);
					else
						p.setPosition(Math.random()*0.32*2 + 0.323 - 0.32, 0.5 - 0.49);

					p.computeDirection(opponent.getTarget().getCenterPosition());//Et on ajuste la direction du projectile
				}

				else if(isPlayer && !p.isOutOfRectangle(player.getTarget().getCenterPosition().getX(), player.getTarget().getCenterPosition().getY(), 0.01, 0.01)) {
					//Si le projectile touche sa cible
					opponent.deactivation(p, player.getTarget());//On desactive le module vise (Ne marche que pour Ion)
					opponent.applyDamage(p, player.getTarget());//On applique les dommages
					System.out.println("opponent touché");
					toDelete.add(p);//Et on supprime le projectile
				}

				else if(!isPlayer && !p.isOutOfRectangle(opponent.getTarget().getCenterPosition().getX(), opponent.getTarget().getCenterPosition().getY(), 0.01, 0.01)) {
					//On fait pareil pour l'opponent
					player.deactivation(p, opponent.getTarget());
					player.applyDamage(p, opponent.getTarget());
					System.out.println("player touché");
					toDelete.add(p);
				}
			}
		}
		projectiles.removeAll(toDelete);//On supprime les projectiles qui ont touche leur cible
		toDelete.removeAll(toDelete);

	}

	/**
	 * Add a random reward
	 * @return the name of the reward
	 */
	public String randReward() {
		int rand = (int)(Math.random()*4);//On genere une recompense aleatoirement
		if(rand==0) {//Soit c'est une nouvelle arme (aleatoire)
			int randWeapon = (int) (Math.random()*3);
			ArrayList<Weapon> listWeapon = new ArrayList<Weapon>();
			listWeapon.add(new Ion());
			listWeapon.add(new Laser());
			listWeapon.add(new Missile());
			Weapon newWeapon = listWeapon.get(randWeapon);
			if(player.getTileWithoutWeapon() != null) {//On place l'arme sur un salle sans arme
				player.getWeaponControl().addWeapon(newWeapon);
				player.getTileWithoutWeapon().setWeapon(newWeapon);
				Module wc = (Module)((ArrayList<Tile>)player.getLayout()).get(1);
				wc.setAllocatedEnergy(wc.getAllocatedEnergy());
			}
			return "NEW WEAPON : " + newWeapon.getClass().getSimpleName();

		}
		else if (rand==1) {//Soit le joueur gagne de la vie
			int randHull = (int) (Math.random()*5);
			if (player.getCurrentHull()+randHull>=30) {
				player.setCurrentHull(30);
				return "HULL = 30 !!!";
			}
			else {
				player.setCurrentHull(player.getCurrentHull()+randHull);
				return "HULL + " + randHull + " !!!";
			}
		}
		else if (rand==2) {//Soit le joueur gagne un crewMember
			player.addCrewMember(new CrewMember("Pedro" + level));
			return "NEW CREW MEMBER !!!";
		}
		else {//Ou bien un missile
			player.setNbMissiles(player.getNbMissiles()+1);
			return "NEW MISSILE !!!";
		}
	}


	/**
	 * Apply next level
	 */
	public void nextLevel() {
		opponent.nextShip();//Fonction qui ameliore le vaisseau ennemie
		level++;
	}

	/**
	 * @return the current level
	 */
	public int getLevel() {
		return level;
	}
	
	
	
	/**
	 * Draws the ships and HUDs.
	 */
	public void draw() {
		player.draw();
		player.drawHUD();

		opponent.draw();
		opponent.drawHUD();

	}



	/**
	 * @return the readyToPlay
	 */
	public boolean isReadyToPlay() {
		return readyToPlay;
	}

	/**
	 * @param readyToPlay the readyToPlay to set
	 */
	public void setReadyToPlay(boolean readyToPlay) {
		this.readyToPlay = readyToPlay;
	}



	private EndButton[] 	endBtns;	// Tous les boutons de fin de partie (pour ameliorer les modules)

	/**
	 * A EndButton is an implementation of a Button
	 * which add energy to player choice
	 */
	private class EndButton extends Button {

		private int moduleIndex;

		public EndButton(Vector2<Double> pos, Vector2<Double> dim, int moduleIndex) {
			super(pos, dim);
			this.moduleIndex = moduleIndex;
		}

		@Override
		protected void onLeftClick() {
			Module m = ((Module)((ArrayList<Tile>)player.getLayout()).get(moduleIndex));
			if(m.getCurrentLevel() + 1 <= m.getMaxLevel()) {
				m.setCurrentLevel(m.getCurrentLevel() + 1);;
				if(m instanceof Reactor) {
					m.setAllocatedEnergy(m.getAllocatedEnergy()+1);
				}
				readyToPlay = true;
			}
		}

		@Override
		protected void onRightClick() {
		}

		@Override
		protected void onMiddleClick() {}

	}
	/**
	 * Draws end HUDs, when player win
	 */
	public void drawEnd() {
		double x = 0.15;
		double y = 0.2;
		for (int i = 0; i < endBtns.length; i++) {
			if (endBtns[i] == null)
				endBtns[i] = new EndButton(new Vector2<Double>(x+0.05+(0.2*i), y+0.2), new Vector2<Double>(0.095, 0.035), i);
			else
				endBtns[i].draw();
			StdDraw.rectangle(x+0.05+(0.2*i), y+0.2, 0.095, 0.035);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text(x+0.05+(0.2*i), y+0.2, ((ArrayList<Tile>)player.getLayout()).get(i).getClass().getSimpleName());
			Module m = ((Module)((ArrayList<Tile>)player.getLayout()).get(i));
			if(m.getCurrentLevel() >= m.getMaxLevel()){
				StdDraw.text(x+0.05+(0.2*i), y+0.13, "Full");

			}
		}
	}

	/**
	 * @return if all the modules are max level
	 */
	public boolean AllModulesLevelMax() {
		for(int i = 0; i < endBtns.length; i++) {
			Module m = ((Module)((ArrayList<Tile>)player.getLayout()).get(i));
			if(m.getCurrentLevel() < m.getMaxLevel()) {
				return false;
			}
		}
		return true;
	}
}
