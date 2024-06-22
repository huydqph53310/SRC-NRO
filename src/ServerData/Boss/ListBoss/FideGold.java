package ServerData.Boss.ListBoss;

import ServerData.Boss.Boss;
import ServerData.Boss.BossID;
import ServerData.Boss.BossStatus;
import ServerData.Boss.BossesData;
import ServerData.Models.Player.Player;
import ServerData.Server.ServerNotify;
import ServerData.Services.EffectSkillService;
import ServerData.Services.Service;
import ServerData.Utils.Util;

public class FideGold extends Boss{
     public FideGold() throws Exception {
        super(BossID.FIDE_GOLD, BossesData.FIDE_GOLD);
    }
    public int  xy = 10;
    public int  dem = 0;
    @Override
    public void reward(Player plKill) {
        Service.gI().dropItemMap(this.zone, Util.caitrangFideVang(zone, 629, 1, this.location.x, this.location.y, plKill.id));
        do{
            dem++;
            Service.gI().dropItemMap(this.zone, Util.HongNgoc(zone, 861, 10, this.location.x+=xy, this.location.y, plKill.id));
        }while(dem<5);    
    }
         @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 90)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage/3);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
                dem=0;
            }
            return damage;
        } else {
            return 0;
        }
    }
    
    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;
    
}
