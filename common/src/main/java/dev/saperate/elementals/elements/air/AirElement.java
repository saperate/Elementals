package dev.saperate.elementals.elements.air;

import dev.saperate.elementals.data.Bender;
import dev.saperate.elementals.data.PlayerData;
import dev.saperate.elementals.elements.Element;
import dev.saperate.elementals.elements.Upgrade;

public class AirElement extends Element {
    public AirElement() {
        super("Air", new Upgrade[]{
                new Upgrade("airGust", new Upgrade[]{
                        new Upgrade("airShield", 2),
                        new Upgrade("airTornado", new Upgrade[]{
                                new Upgrade("airTornadoSpeedI", new Upgrade[]{
                                        new Upgrade("airTornadoSpeedII", 1)
                                }, 1)
                        }, 2)
                }, true, 2),
                new Upgrade("airStream", new Upgrade[]{
                        new Upgrade("airBall", new Upgrade[]{
                                new Upgrade("airBallSpeedI", new Upgrade[]{
                                        new Upgrade("airBallSpeedII", 1)
                                }, 1)
                        }, false, -1, 2),
                        new Upgrade("airStreamDamageI", new Upgrade[]{
                                new Upgrade("airBullets", new Upgrade[]{
                                        new Upgrade("airBulletsDamageI", new Upgrade[]{
                                                new Upgrade("airBulletsSpeedI", new Upgrade[]{
                                                        new Upgrade("airBulletsSpeedII", new Upgrade[]{
                                                                new Upgrade("airBulletsMastery", 2)
                                                        }, 1)
                                                }, 1)
                                        }, 1),
                                        new Upgrade("airBulletsCountI", new Upgrade[]{
                                                new Upgrade("airBulletsCountII", 1)
                                        }, 1)
                                }, 2),
                                new Upgrade("airSuffocate", 2)
                        }, true, 1),
                        new Upgrade("airStreamSpeedI", new Upgrade[]{
                                new Upgrade("airStreamSpeedII", new Upgrade[]{
                                        new Upgrade("airStreamEfficiencyI", new Upgrade[]{
                                                new Upgrade("airStreamMastery", 2)
                                        }, 1)
                                }, 1)
                        }, false, 1, 1)
                }, 2),
                new Upgrade("airJump", new Upgrade[]{
                        new Upgrade("airJumpRangeI", new Upgrade[]{
                                new Upgrade("airJumpRangeII", 1),
                        }, 1),
                        new Upgrade("airScooter", new Upgrade[]{
                                new Upgrade("airScooterSpeedI", new Upgrade[]{
                                        new Upgrade("airScooterSpeedII", 1)
                                }, 1)
                        }, 2)
                }, 2),
                new Upgrade("airSpiritProjection", new Upgrade[]{
                        new Upgrade("airSpiritProjectionRangeI", new Upgrade[]{
                                new Upgrade("airSpiritProjectionRangeII", new Upgrade[]{
                                        new Upgrade("airSpiritProjectionRangeIII", new Upgrade[]{
                                                new Upgrade("airSpiritProjectionRangeIV", 1)
                                        }, 1)
                                }, 1)
                        }, 1)
                }, 4)
        });
        addAbility(new AbilityAir1(), 0);
        addAbility(new AbilityAirGust());
        addAbility(new AbilityAirShield(), 4);
        addAbility(new AbilityAirTornado(), 5);
        addAbility(new AbilityAir2(), 1);
        addAbility(new AbilityAirStream());
        addAbility(new AbilityAirBall());
        addAbility(new AbilityAirBullets(), 6);
        addAbility(new AbilityAirSuffocate(), 7);
        addAbility(new AbilityAir3(), 2);
        addAbility(new AbilityAirScooter());
        addAbility(new AbilityAirJump());
        addAbility(new AbilityAir4(), 3);

        registerUpgradeKeybind("airShield", 4);
        registerUpgradeKeybind("airTornado", 5);
        registerUpgradeKeybind("airBullets", 6);
        registerUpgradeKeybind("airSuffocate", 7);
    }

    public static Element get() {
        return getElement("Air");
    }


    @Override
    public int getColor() {
        return 0xFFdeeaff;
    }

    @Override
    public int getSecondaryColor() {
        return 0xFFb9d3ff;
    }

    @Override
    public int getTertiaryColor() {
        return 0xFF485291;
    }

    @Override
    public String[] getBackgroundTextures() {
        return new String[]{"bottom.png"};
    }

    @Override
    public boolean isSkillTreeComplete(Bender bender) {
        PlayerData plrData = bender.plrData;
        return bender.hasElement(this)
                && (plrData.canUseUpgrade("airTornadoSpeedII") || plrData.canUseUpgrade("airShield"))
                && plrData.canUseUpgrade("airBallSpeedII")
                && ((plrData.canUseUpgrade("airBulletsMastery") && plrData.canUseUpgrade("airBulletsCountII")) || plrData.canUseUpgrade("airSuffocate"))
                && plrData.canUseUpgrade("airStreamMastery")
                && plrData.canUseUpgrade("airJumpRangeII")
                && plrData.canUseUpgrade("airScooterSpeedII")
                && plrData.canUseUpgrade("airSpiritProjectionRangeIV")
                ;
    }
}