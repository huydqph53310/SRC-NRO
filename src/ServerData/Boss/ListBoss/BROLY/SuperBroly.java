package ServerData.Boss.ListBoss.BROLY;

import java.util.logging.Level;
import java.util.logging.Logger;

import Server.Data.Consts.ConstPlayer;
import ServerData.Boss.*;
import ServerData.Models.Map.Zone;
import ServerData.Models.Player.Player;
import ServerData.Models.Player.PlayerSkill.Skill;
import ServerData.Services.EffectSkillService;
import ServerData.Services.PetService;
import ServerData.Services.PlayerService;
import ServerData.Services.Service;
import ServerData.Utils.SkillUtil;
import ServerData.Utils.Util;

public class SuperBroly extends Boss {

    private long lastUpdate = System.currentTimeMillis();
    private long timeJoinMap;
    protected Player playerAtt;
    private int timeLive = 200000000;
    public int petgender = Util.nextInt(0, 2);
    public Player mypett;
    private long lastTimeDamaged;
    private long lastTimeHP;
    private int timeHP;

    // public SuperBroly(BossData bossData) throws Exception {
    // super(Util.randomBossId(), bossData);
    // }
    // public Superbroly(Zone zone,long hp,long dame) throws Exception {
    // super(Util.randomBossId(), BossesData.SUPER_BROLY);
    // this.zone = zone;
    // this.nPoint.hpg = hp;
    // this.nPoint.dame =dame;
    // }

    public SuperBroly(Zone zone, long dame, long hp) throws Exception {
        super(Util.randomBossId(), new BossData(
                "Super Broly", // name
                ConstPlayer.TRAI_DAT, // gender
                // 1318,1319, 1320,
                new short[] { 294, 295, 296, 28, -1, -1 }, // outfit {head, body, leg, bag, aura, eff}
                dame, // dame
                new long[] { ((Util.nextInt(1_700_000, 16_070_077))) }, // hp
                new int[] { zone.map.mapId }, // map join
                new int[][] {
                        { Skill.LIEN_HOAN, 7, 2000 },
                        { Skill.DRAGON, 7, 2000 },
                        { Skill.KAMEJOKO, 7, 2000 },
                        { Skill.MASENKO, 7, 2000 },
                        { Skill.ANTOMIC, 7, 2000 },
                        { Skill.TAI_TAO_NANG_LUONG, 1, 15000 }, },
                new String[] {
                        "|-1|Gaaaaaa",
                        "|-2|Tới đây đi!"
                }, // text chat 1
                new String[] { "|-1|Các ngươi tới số rồi mới gặp phải ta",
                        "|-1|Gaaaaaa",
                        "|-2|Không ngờ..Hắn mạnh cỡ này sao..!!"
                }, // text chat 2
                new String[] { "|-1|Gaaaaaaaa!!!" }, // text chat 3
                1

        ));
        this.zone = zone;
    }

    public SuperBroly(Zone zone, long hp, long dame, int... id) throws Exception {
        super(Util.randomBossId(), new BossData(
                "Broly", // name
                ConstPlayer.XAYDA, // gender
                new short[] { 291, 292, 293, -1, -1, -1 }, // outfit {head, body, leg, bag, aura, eff}
                dame, // dame
                new long[] { hp }, // hp
                new int[] { 5 }, // map join
                new int[][] {
                        { Skill.DRAGON, 7, 1000 },
                        { Skill.TAI_TAO_NANG_LUONG, 7, 20000 },
                        { Skill.ANTOMIC, 7, 500 } }, // skill
                new String[] {
                        "|-1|Tuy không biết các ngươi là ai, nhưng ta rất ấn tượng đấy!",
                        "|-2|Tới đây đi!"
                }, // text chat 1
                new String[] { "|-1|Các ngươi tới số rồi mới gặp phải ta",
                        "|-1|Gaaaaaa",
                        "|-2|Không..thể..nào!!",
                        "|-2|Không ngờ..Hắn mạnh cỡ này sao..!!"
                }, // text chat 2
                new String[] { "|-1|Gaaaaaaaa!!!" }, // text chat 3
                600 // second rest
        ));
        this.zone = zone;
    }
    @Override
    public void reward(Player plKill) {
        if (plKill.pet != null) {
            return;
        }
        if (plKill.pet == null) {
            PetService.gI().createNormalPet(plKill);
        }
        this.pet = null;
    }

    @Override
    public void active() {
        super.active();
        if (this.pet == null) {
            PetService.gI().createNormalPet(this, petgender);
        }
        try {
            this.hoiPhuc();
        } catch (Exception ex) {
            Logger.getLogger(SuperBroly.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void hoiPhuc() throws Exception {
        if (!Util.canDoWithTime(lastTimeHP, timeHP) || !Util.isTrue(1, 100)) {
            return;
        }
        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }
        this.nPoint.dameg += this.nPoint.dame * 2 / 100;
        this.nPoint.hpg += this.nPoint.hpg * 80 / 100;
        this.nPoint.critg++;
        this.nPoint.calPoint();
        PlayerService.gI().hoiPhuc(this, this.nPoint.hp, this.nPoint.mp);
        Service.gI().sendThongBao(pl, "Tên broly hắn lại tăng sức mạnh rồi!");
        this.chat(2, "Mọi người cẩn thận sức mạnh hắn ta tăng đột biến..");
        this.chat("Graaaaaa...");
        lastTimeHP = System.currentTimeMillis();
        timeHP = Util.nextInt(5000, 10000);
    }

    public void heal(long amount) {
        nPoint.hp = Math.min(nPoint.hp + amount, nPoint.hpMax);
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimeDamaged >= 10000) {
                long healAmount = nPoint.hpMax * 8 / 10; // hồi phục 20% HP gốc khi tấn công trong 1 phút
                this.heal(healAmount);
                byte skillId = (byte) Skill.TAI_TAO_NANG_LUONG;
                if (skillId != 0) {
                    playerSkill.skills.add(SkillUtil.createSkill(skillId, 7));
                    this.chat("Cảm giác rất tốt khi được hồi phục lại năng lượng :)");
                }
                lastTimeDamaged = currentTime;
            }
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                if (damage > this.nPoint.hpMax * 0.1)
                    damage = (this.nPoint.hpMax * 0.1);
            }
            damage = Math.min(damage, nPoint.hpMax * 100 / 100);
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.pet.dispose();
                this.pet = null;
                this.setDie(plAtt);
                die(plAtt);
                BossManager.gI().createBoss(BossID.BROLY_THUONG);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

}
