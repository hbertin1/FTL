package weapon;

import display.StdDraw;
import display.Vector2;

public class Laser extends Weapon{
        /**
         * A laser projectile is a projectile which makes damage in function of the weaponControl allocated energy
         */
        public class LaserProjectile extends Projectile {
                public LaserProjectile(Vector2<Double> pos, Vector2<Double> dir) {
                        super(0.01, 0.01);
                        this.x = pos.getX();
                        this.y = pos.getY();
                        this.cSpeed = 0.5;
                        this.xSpeed = dir.getX()*cSpeed;
                        this.ySpeed = dir.getY()*cSpeed;
                        this.color = StdDraw.RED;
                        this.damage = shotDamage;
                }
        }
        
        /**
         * Creates a Laser
         */
        public Laser() {
                name = "Laser";
                requiredPower = 1;
                chargeTime = 1;
                shotDamage = 0;
                timeDeactivation = 0;
        }

        /**
         * Shots a laser projectile
         * @see weapon.Weapon#shot(display.Vector2, display.Vector2)
         */
        @Override
        public Projectile shot(Vector2<Double> pos, Vector2<Double> dir) {
                return new LaserProjectile(pos, dir);
        }
}
