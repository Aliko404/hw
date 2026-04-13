package com.narxoz.rpg;

import com.narxoz.rpg.strategy.*;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.observer.*;
import com.narxoz.rpg.engine.*;

import java.util.*;

public class Main {

    static class DungeonBoss implements GameObserver {

        int hp, maxHp, attack, defense;
        int phase = 1;
        CombatStrategy strategy;
        GameObserver.EventBus bus;

        DungeonBoss(int hp, int attack, int defense, GameObserver.EventBus bus) {
            this.hp = hp;
            this.maxHp = hp;
            this.attack = attack;
            this.defense = defense;
            this.bus = bus;
            this.strategy = new CombatStrategy.Phase1();
        }

        int attack() { return strategy.calculateDamage(attack); }
        int defend() { return strategy.calculateDefense(defense); }

        void takeDamage(int dmg) {
            hp = Math.max(0, hp - dmg);
            int percent = hp * 100 / maxHp;

            if (percent <= 60 && phase == 1) {
                phase = 2;
                bus.notify(new GameEvent(GameEventType.BOSS_PHASE_CHANGED, "Boss", 2));
            } else if (percent <= 30 && phase == 2) {
                phase = 3;
                bus.notify(new GameEvent(GameEventType.BOSS_PHASE_CHANGED, "Boss", 3));
            }
        }

        public void onEvent(GameEvent e) {
            if (e.getType() == GameEventType.BOSS_PHASE_CHANGED) {
                if (e.getValue() == 2) strategy = new CombatStrategy.Phase2();
                if (e.getValue() == 3) strategy = new CombatStrategy.Phase3();
                System.out.println("Boss -> " + strategy.getName());
            }
        }

        boolean isAlive() { return hp > 0; }
    }

    static class DungeonEngine {

        GameObserver.EventBus bus;

        DungeonEngine(GameObserver.EventBus bus) {
            this.bus = bus;
        }

        EncounterResult run(List<Hero> heroes, DungeonBoss boss) {

            int rounds = 0;

            while (boss.isAlive() && heroes.stream().anyMatch(Hero::isAlive)) {
                rounds++;

                for (Hero h : heroes) {
                    if (!h.isAlive()) continue;

                    int dmg = Math.max(0, h.attack() - boss.defend());
                    boss.takeDamage(dmg);
                    bus.notify(new GameEvent(GameEventType.ATTACK_LANDED, h.getName(), dmg));
                }

                for (Hero h : heroes) {
                    if (!h.isAlive()) continue;

                    int dmg = Math.max(0, boss.attack() - h.defend());
                    h.takeDamage(dmg);

                    bus.notify(new GameEvent(GameEventType.ATTACK_LANDED, "Boss", dmg));

                    if (h.getHp() <= h.getMaxHp() * 0.3)
                        bus.notify(new GameEvent(GameEventType.HERO_LOW_HP, h.getName(), h.getHp()));

                    if (!h.isAlive())
                        bus.notify(new GameEvent(GameEventType.HERO_DIED, h.getName(), 0));
                }

                if (!boss.isAlive()) {
                    bus.notify(new GameEvent(GameEventType.BOSS_DEFEATED, "Boss", 0));
                    break;
                }

                if (rounds == 3) {
                    heroes.get(0).setStrategy(new CombatStrategy.Defensive());
                }

                if (rounds > 20) break;
            }

            long alive = heroes.stream().filter(Hero::isAlive).count();
            return new EncounterResult(!boss.isAlive(), rounds, (int) alive);
        }
    }

    public static void main(String[] args) {

        GameObserver.EventBus bus = new GameObserver.EventBus();

        Hero h1 = new Hero("Warrior", 100, 20, 10);
        Hero h2 = new Hero("Tank", 120, 15, 15);
        Hero h3 = new Hero("Rogue", 80, 25, 8);

        h1.setStrategy(new CombatStrategy.Aggressive());
        h2.setStrategy(new CombatStrategy.Defensive());
        h3.setStrategy(new CombatStrategy.Balanced());

        List<Hero> heroes = Arrays.asList(h1, h2, h3);

        DungeonBoss boss = new DungeonBoss(300, 25, 10, bus);

        bus.register(new GameObserver.BattleLogger());
        bus.register(new GameObserver.AchievementTracker());
        bus.register(new GameObserver.PartySupport(heroes));
        bus.register(new GameObserver.HeroStatusMonitor(heroes));
        bus.register(new GameObserver.LootDropper());
        bus.register(boss);

        DungeonEngine engine = new DungeonEngine(bus);

        EncounterResult result = engine.run(heroes, boss);

        System.out.println("=== RESULT ===");
        System.out.println("Heroes won: " + result.isHeroesWon());
        System.out.println("Rounds: " + result.getRoundsPlayed());
        System.out.println("Survivors: " + result.getSurvivingHeroes());
    }
}