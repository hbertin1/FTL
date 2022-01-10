package main;
import java.awt.event.KeyEvent;

import display.StdDraw;

/**
 * The bindings class processes the key pressed by the player.
 */
public class Bindings {

	private enum ExtendedKeyCode{
		EKC_1(0x96),
		EKC_2(0x10000e9),
		EKC_3(0x98),
		EKC_4(0xde),
		EKC_5(0x207);

		private int code;

		private ExtendedKeyCode(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

	}

	private World w;		// The world on which the actions act

	/**
	 * Create the bindings
	 * @param w the world
	 */
	public Bindings(World w) {
		this.w=w;
	}

	/**
	 * Processes the key pressed by the player.
	 * Escape kills the game.
	 * The keys from 1 to 5 acts on the modules.
	 * The keys from A to R acts on the weapons.
	 * The key from Q act on the crew.
	 * The arrows keys acts on the aiming system and on the crew.
	 * 
	 * It processes only one key at the time but the
	 * keys are popped out of a stack which prevents
	 * key shadowing and key loss.
	 */
	public void processKey() {
		if (!StdDraw.hasNextKeyTyped())
			return;

		KeyEvent key = StdDraw.nextKeyTyped();

		if (key.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
		// Module Energy Management
		if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_1.getCode() && key.isShiftDown())
			w.player.removeEnergy(1);
		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_1.getCode())
			w.player.addEnergy(1);

		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_2.getCode() && key.isShiftDown())
			w.player.removeEnergy(2);
		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_2.getCode())
			w.player.addEnergy(2);

		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_3.getCode() && key.isShiftDown())
			w.player.removeEnergy(3);
		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_3.getCode())
			w.player.addEnergy(3);

		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_4.getCode() && key.isShiftDown())
			w.player.removeEnergy(4);
		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_4.getCode())
			w.player.addEnergy(4);

		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_5.getCode() && key.isShiftDown())
			w.player.removeEnergy(5);
		else if(key.getExtendedKeyCode() == ExtendedKeyCode.EKC_5.getCode())
			w.player.addEnergy(5);


		// Weapon Management
		else if(key.getKeyCode() == KeyEvent.VK_A && key.isControlDown())
			w.player.shotWeapon(0);
		else if(key.getKeyCode() == KeyEvent.VK_A && key.isShiftDown())
			w.player.deactiveWeapon(0);
		else if(key.getKeyCode() == KeyEvent.VK_A)
			w.player.activeWeapon(0);

		else if(key.getKeyCode() == KeyEvent.VK_Z && key.isControlDown())
			w.player.shotWeapon(1);
		else if(key.getKeyCode() == KeyEvent.VK_Z && key.isShiftDown())
			w.player.deactiveWeapon(1);
		else if(key.getKeyCode() == KeyEvent.VK_Z)
			w.player.activeWeapon(1);

		else if(key.getKeyCode() == KeyEvent.VK_E && key.isControlDown())
			w.player.shotWeapon(2);
		else if(key.getKeyCode() == KeyEvent.VK_E && key.isShiftDown())
			w.player.deactiveWeapon(2);
		else if(key.getKeyCode() == KeyEvent.VK_E)
			w.player.activeWeapon(2);

		else if(key.getKeyCode() == KeyEvent.VK_R && key.isControlDown() && w.player.getWeaponControl().getWeapons().length > 3)
			w.player.shotWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_R && key.isShiftDown() && w.player.getWeaponControl().getWeapons().length > 3)
			w.player.deactiveWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_R && w.player.getWeaponControl().getWeapons().length > 3)
			w.player.activeWeapon(3);

		else if(key.getKeyCode() == KeyEvent.VK_T && key.isControlDown() && w.player.getWeaponControl().getWeapons().length >= 4)
			w.player.shotWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_T && key.isShiftDown() && w.player.getWeaponControl().getWeapons().length >= 4)
			w.player.deactiveWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_T && w.player.getWeaponControl().getWeapons().length >= 4)
			w.player.activeWeapon(3);

		else if(key.getKeyCode() == KeyEvent.VK_Y && key.isControlDown() && w.player.getWeaponControl().getWeapons().length >= 5)
			w.player.shotWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_Y && key.isShiftDown() && w.player.getWeaponControl().getWeapons().length >= 5)
			w.player.deactiveWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_Y && w.player.getWeaponControl().getWeapons().length >= 5)
			w.player.activeWeapon(3);

		else if(key.getKeyCode() == KeyEvent.VK_U && key.isControlDown() && w.player.getWeaponControl().getWeapons().length >= 6)
			w.player.shotWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_U && key.isShiftDown() && w.player.getWeaponControl().getWeapons().length >= 6)
			w.player.deactiveWeapon(3);
		else if(key.getKeyCode() == KeyEvent.VK_U && w.player.getWeaponControl().getWeapons().length >= 6)
			w.player.activeWeapon(3);

		// Crew Management
		else if(key.getKeyCode() == KeyEvent.VK_Q && key.isShiftDown())
			w.player.unselectCrewMember();
		else if(key.getKeyCode() == KeyEvent.VK_Q)
			w.player.selectNextMember();

		else if(key.getKeyCode() == KeyEvent.VK_LEFT && key.isShiftDown())
			processArrowKeyCrew(KeyEvent.VK_LEFT);
		else if(key.getKeyCode() == KeyEvent.VK_RIGHT && key.isShiftDown())
			processArrowKeyCrew(KeyEvent.VK_RIGHT);


		// Aiming System
		else if(key.getKeyCode() == KeyEvent.VK_UP)
			processArrowKey(KeyEvent.VK_UP);
		else if(key.getKeyCode() == KeyEvent.VK_DOWN)
			processArrowKey(KeyEvent.VK_DOWN);
		else if(key.getKeyCode() == KeyEvent.VK_LEFT)
			processArrowKey(KeyEvent.VK_LEFT);
		else if(key.getKeyCode() == KeyEvent.VK_RIGHT)
			processArrowKey(KeyEvent.VK_RIGHT);
		//Rules
		else if(key.getKeyCode() ==  KeyEvent.VK_SPACE) {
			long time = System.currentTimeMillis();
			while (System.currentTimeMillis() < time + 7000){
				StdDraw.clear();
				StdDraw.text(0.5, 0.8, "Touches :");
				for (int i=0; i<7; i++) {
					StdDraw.text(0.5, 0.6-(0.05*i), stringTouches(i));
				}
				StdDraw.show();
			}
			StdDraw.clear();
		}
	}

	/**
	 * Process the arrow keys.
	 * @param key the arrow key pressed
	 */
	private void processArrowKey(int key) {
		switch(key) {
		case(KeyEvent.VK_UP):
			w.player.aimUp(w.opponent);
		break;
		case(KeyEvent.VK_DOWN):
			w.player.aimDown(w.opponent);
		break;
		case(KeyEvent.VK_LEFT):
			w.player.aimLeft(w.opponent);
		break;
		case(KeyEvent.VK_RIGHT):
			w.player.aimRight(w.opponent);
		break;
		}
	}
	/**
	 * Process the arrow keys.
	 * @param key the arrow key pressed
	 */
	private void processArrowKeyCrew(int key) {
		switch(key) {
		case(KeyEvent.VK_LEFT):
			w.player.moveCrewMemberLeft();
		break;
		case(KeyEvent.VK_RIGHT):
			w.player.moveCrewMemberRight();
		break;
		}
	}
	//Pause

	/**
	 * text to explain the keys
	 * @return the text
	 */

	private String stringTouches(int i) {
		String[] tab = new String[7];
		tab[0] = "Les touches 1,2 et 3 : permettent d allouer de l energie a un module\r\n";
		tab[1] = "Les touches de A a R permettent de charger l arme correspondante (l ordre des armes correspond a l ordre des touches)\r\n";
		tab[2] = "Ctrl + une touche d arme permet de tirer avec l arme correspondante\r\n";
		tab[3] = "Shift + une touche d arme permet de decharger une arme\r\n";
		tab[4] = "Q permet de selectionner un membre d equipage, on selectionne le membre suivant en appuyant de nouveau sur Q\r\n"; 
		tab[5] = "Shift + fleche droite ou gauche permet de deplacer un membre d equipage (un membre d equipage doit etre selectionne\r\n";
		tab[6] = "Les fleches directrices permettent de viser une salle sur le vaisseau ennemi\r\n";
		return tab[i];
	}

}
