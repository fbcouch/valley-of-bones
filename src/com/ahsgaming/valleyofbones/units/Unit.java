/**
 * Copyright 2012 Jami Couch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This project uses:
 * 
 * LibGDX
 * Copyright 2011 see LibGDX AUTHORS file
 * Licensed under Apache License, Version 2.0 (see above).
 * 
 */
package com.ahsgaming.valleyofbones.units;

import com.ahsgaming.valleyofbones.*;
import com.ahsgaming.valleyofbones.ai.UnitFSM;
import com.ahsgaming.valleyofbones.units.behavior.*;

/**
 * @author jami
 *
 */
public class Unit extends AbstractUnit {

    public static AbstractUnit createUnit(int id, String protoId, Player owner) {
        AbstractUnit unit = new Unit();

        unit.id = id;
        unit.originalOwner = unit.owner = owner;
        unit.proto = Prototypes.getProto((owner != null ? owner.getRace() : "terran"), protoId);
        unit.data = UnitData.createUnitData(unit.proto);
        unit.view = UnitView.createUnitView(unit);

        unit.attackBehavior = selectAttackBehavior(unit);
        unit.defendBehavior = selectDefendBehavior(unit);
        unit.moveBehavior = selectMoveBehavior(unit);
        if (unit.getData().getHeal() > 0) {
            unit.healBehavior = new BasicHeal(unit);
        } else {
            unit.healBehavior = new NoHeal();
        }
        unit.turnBehavior = new BasicUnitTurnListener(unit);

        if (unit.getData().getAutoheal() > 0) {
            unit.turnBehavior = new AutoHealUnitTurnListener(unit.turnBehavior);
        }

        if (unit.getData().isCapturable()) {
            unit.turnBehavior = new CapturableUnitTurnListener(unit.turnBehavior);
        }
        unit = applyAbility(unit);

        if (unit.getData().buildTime > 0) {
            unit.getData().building = true;
            unit.getData().buildTimeLeft = unit.getData().buildTime;
        }

        return unit;
    }

    public static MoveBehavior selectMoveBehavior(AbstractUnit unit) {
        if (unit.getData().getMoveSpeed() > 0) {
            return new BasicMove(unit);
        }
        return new NoMove();
    }

    public static AttackBehavior selectAttackBehavior(AbstractUnit unit) {
        AttackBehavior behavior;
        if (unit.getData().getAttackSpeed() > 0) {
            behavior = new BasicAttack(unit);
            if (unit.getData().getSplashDamage() > 0) {
                behavior = new SplashAttack((BasicAttack)behavior);
            }
            return behavior;
        }
        return new NoAttack();
    }

    public static DefendBehavior selectDefendBehavior(AbstractUnit unit) {
        return new BasicDefend(unit);
    }

    public static AbstractUnit applyAbility(AbstractUnit unit) {
        String ability = unit.getData().getAbility();
        if (ability.equals("increasing-returns")) {
            unit.turnBehavior = new IncreasingReturnsUnitTurnListener(unit.turnBehavior);
        } else if (ability.equals("detect")) {
            unit.getData().setDetector(true);
        } else if (ability.equals("stealth")) {
            unit.abilityBehavior = new StealthAbility(unit);
        } else if (ability.equals("sabotage")) {
            unit.attackBehavior = new SabotageAttack(unit);
            unit.getData().setDetector(true);
            unit.getData().setInvisible(true);
            unit.getData().setControllable(false);
        } else if (ability.equals("mind-control")) {
            unit.getData().setInvisible(true);
            unit.getData().setControllable(false);
            MindControlAbility mindControlAbility = new MindControlAbility(unit);
            unit.attackBehavior = mindControlAbility;
            unit.abilityBehavior = mindControlAbility;
            unit.turnBehavior = new MindControlUnitTurnListener(unit.turnBehavior);
        } else if (ability.equals("shift")) {
            unit.moveBehavior = new ShiftMove(unit);
        }
        return unit;
    }
}
