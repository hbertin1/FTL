package module;

import java.util.ArrayList;

import display.StdDraw;
import display.Vector2;
import module.Shield.Protection;
import weapon.Ion;
import weapon.Laser;
import weapon.Weapon;


/**
 * A Shield is a module
 */
public class Shield extends Module {

	public class Protection{

		private boolean fastCharge;		//if the shield is fast Charged (if the time to repair it, is short)
		private boolean isCharged;		//if the shield is charged
		private long 	time; 			//the current time when the protection is hitted
		/**
		 * Construct a Protection
		 * @param fastCharge if the protection charge in 2s or in 1.5s
		 * @param isCharged if the protection is charged
		 */
		public Protection(boolean fastCharge, boolean isCharged) {
			this.fastCharge = fastCharge;
			this.isCharged = isCharged;
			this.time = 0;
		}

		/**
		 * @return the fastCharge
		 */
		public boolean isFastCharge() {
			return fastCharge;
		}

		/**
		 * @param fastCharge the fastCharge to set
		 */
		public void setFastCharge(boolean fastCharge) {
			this.fastCharge = fastCharge;
		}

		/**
		 * @return the isCharged
		 */
		public boolean isCharged() {
			return isCharged;
		}

		/**
		 * @param isCharged the isCharged to set
		 */
		public void setCharged(boolean isCharged) {
			this.isCharged = isCharged;
		}

		/**
		 * @return the time
		 */
		public long getTime() {
			return time;
		}

		/**
		 * @param time the time to set
		 */
		public void setTime(long time) {
			this.time = time;
		}

	}

	private ArrayList<Protection> protections; //ArrayList which stock all the protections

	/**
	 * Construct a Shield owned by the player or the opponent.
	 * The Shield tile is drawn at the location given in tilePos.
	 * The Shield HUD is drawn at the location given in hudPos.
	 * The initialLevel of the Shield is the amount of energy it
	 * provides in the ship.
	 * @param hudPos position at which to draw the HUD
	 * @param tilePos position at which to draw the tile
	 * @param isPlayer whether it belongs to the player
	 * @param initialLevel initial amount of energy which it can provide
	 */
	public Shield(Vector2<Double> hudPos, Vector2<Double> tilePos, boolean isPlayer, int initialLevel) {
		super(hudPos, tilePos, isPlayer);
		name = "Shield";
		maxLevel = 8;
		currentLevel = initialLevel;
		allocatedEnergy = 0;
		amountDamage = 0;
		canBeManned = true;
		protections = new ArrayList<Protection>();
	}


	/**
	 * Draws the shield if there is at list one protection
	 */
	@Override
	public void drawHud(boolean isPlayer) {
		super.drawHud(isPlayer);
		int nbProtectionsActives = 0;
		for(int i = protections.size()-1; i >=0; i--) {
			if(protections.get(i).isCharged()) {
				nbProtectionsActives++;	
			}
		}
		if(nbProtectionsActives > 0) {
			StdDraw.setPenRadius(nbProtectionsActives*0.003);
			StdDraw.setPenColor(StdDraw.BLUE);
			if(isPlayer) {
				StdDraw.circle(0.277, 0.489, 0.1);
			}
			else {
				StdDraw.circle(0.79, 0.489, 0.1);
			}
			StdDraw.setPenRadius();
			StdDraw.setPenColor(StdDraw.BLACK);
		}
	}


	/**
	 * @return the protections
	 */
	public ArrayList<Protection> getProtections() {
		return protections;
	}

	/**
	 * Add a protection to the ArrayList protections
	 * @param Protection the protection to add
	 */
	public void addProtection(Protection p) {
		protections.add(p);
	}

	/**
	 * Remove the last protection in the ArrayList protections
	 */
	public void removeLastProtection() {
		protections.remove(protections.size()-1);
	}

	/**
	 * Deactivate the last protection in the ArrayList protections
	 */
	public void deactivateLastProtection() {
		boolean find = false;
		
		for(int i = protections.size()-1; i >=0 && !find; i--) {
			if(protections.get(i).isCharged()) {
				protections.get(i).isCharged = false;
				protections.get(i).setTime(System.currentTimeMillis());
				find = true;				
			}
		}
	}

	/**
	 * Function which remove or add a protection to the shield
	 * @param allocatedEnergy the allocated energy to set
	 */
	@Override
	public void setAllocatedEnergy(int allocatedEnergy) {
		
		if(allocatedEnergy < 0)
			allocatedEnergy = 0;
		if(allocatedEnergy > this.currentLevel)
			allocatedEnergy = this.currentLevel;
		
		if(allocatedEnergy < this.allocatedEnergy) {
			
			while(allocatedEnergy < this.allocatedEnergy) {
				this.removeEnergy();
				
				if(this.getAllocatedEnergy()%2 == 0) {
					this.removeLastProtection();
				}
				else{
					this.protections.get(this.protections.size()-1).setFastCharge(false);
				}
			}
		}
		
		else {
			
			while(allocatedEnergy > this.allocatedEnergy) {
				this.addEnergy();
				
				if(this.getAllocatedEnergy()%2 == 1) {
					Protection p = new Protection(false, true);
					this.addProtection(p);
				}
				else if(this.allocatedEnergy > 0){
					this.protections.get((this.allocatedEnergy-1)/2).setFastCharge(true);
				}
			}
		}
		
	}


}
