package weapon;

import display.StdDraw;
import display.Vector2;


public class Missile extends Weapon{
        /**
         * A missile projectile is a projectile which makes 4 damages
         */
        public class MissileProjectile extends Projectile {
                public MissileProjectile(Vector2<Double> pos, Vector2<Double> dir) {
                        super(0.03, 0.02);
                        this.x = pos.getX();
                        this.y = pos.getY();
                        this.cSpeed = 0.5;
                        this.xSpeed = dir.getX()*cSpeed;
                        this.ySpeed = dir.getY()*cSpeed;
                        this.color = StdDraw.BLACK;
                        this.damage = shotDamage;
                }
        }
        
        /**
         * Creates a missile
         */
        public Missile() {
                name = "Missile";
                requiredPower = 1;
                chargeTime = 2;
                shotDamage = 30;
                timeDeactivation = 0;
        }

        /**
         * Shots a missile projectile
         * @see weapon.Weapon#shot(display.Vector2, display.Vector2)
         */
        @Override
        public Projectile shot(Vector2<Double> pos, Vector2<Double> dir) {
                return new MissileProjectile(pos, dir);
        }
}
