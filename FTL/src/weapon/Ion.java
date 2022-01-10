package weapon;

import display.StdDraw;
import display.Vector2;


public class Ion extends Weapon{
        /**
         * A Ion projectile which makes zero damage but disable the module
         */
        public class IonProjectile extends Projectile {
                public IonProjectile(Vector2<Double> pos, Vector2<Double> dir) {
                        super(0.03, 0.03);
                        this.x = pos.getX();
                        this.y = pos.getY();
                        this.cSpeed = 0.5;
                        this.xSpeed = dir.getX()*cSpeed;
                        this.ySpeed = dir.getY()*cSpeed;
                        this.color = StdDraw.GREEN;
                        this.damage = shotDamage;
                }
        }
        
        /**
         * Creates a Ion
         */
        public Ion() {
                name = "Ion";
                requiredPower = 1;
                chargeTime = 2;
                shotDamage = 0;
                timeDeactivation = 1;
        }

        /**
         * Shots a ion projectile
         * @see weapon.Weapon#shot(display.Vector2, display.Vector2)
         */
        @Override
        public Projectile shot(Vector2<Double> pos, Vector2<Double> dir) {
                return new IonProjectile(pos, dir);
        }
}
