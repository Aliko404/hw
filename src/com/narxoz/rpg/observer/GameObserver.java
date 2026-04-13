package com.narxoz.rpg.observer;

import com.narxoz.rpg.combatant.Hero;
import java.util.*;

public interface GameObserver {

    void onEvent(GameEvent event);

    class EventBus {
        private final List<GameObserver> observers = new ArrayList<>();

        public void register(GameObserver o) {
            observers.add(o);
        }

        public void notify(GameEvent e) {
            for (GameObserver o : observers) {
                o.onEvent(e);
            }
        }
    }

    class BattleLogger implements GameObserver {
        public void onEvent(GameEvent e) {
            System.out.println("[LOG] " + e.getType() + " | " + e.getSourceName() + " | " + e.getValue());
        }
    }

    class AchievementTracker implements GameObserver {
        int hits = 0;

        public void onEvent(GameEvent e) {
            if (e.getType() == GameEventType.ATTACK_LANDED) {
                hits++;
                if (hits == 5) System.out.println("Achievement: 5 hits!");
            }
            if (e.getType() == GameEventType.HERO_DIED)
                System.out.println("Achievement: Hero died");

            if (e.getType() == GameEventType.BOSS_DEFEATED)
                System.out.println("Achievement: Boss killed!");
        }
    }

    class PartySupport implements GameObserver {
        List<Hero> heroes;
        Random r = new Random();

        public PartySupport(List<Hero> heroes) {
            this.heroes = heroes;
        }

        public void onEvent(GameEvent e) {
            if (e.getType() == GameEventType.HERO_LOW_HP) {
                Hero h = heroes.get(r.nextInt(heroes.size()));
                if (h.isAlive()) {
                    h.heal(10);
                    System.out.println("Heal: " + h.getName());
                }
            }
        }
    }

    class HeroStatusMonitor implements GameObserver {
        List<Hero> heroes;

        public HeroStatusMonitor(List<Hero> heroes) {
            this.heroes = heroes;
        }

        public void onEvent(GameEvent e) {
            if (e.getType() == GameEventType.HERO_LOW_HP ||
                e.getType() == GameEventType.HERO_DIED) {

                System.out.println("=== STATUS ===");
                for (Hero h : heroes) {
                    System.out.println(h.getName() + " HP: " + h.getHp());
                }
            }
        }
    }

    class LootDropper implements GameObserver {
        public void onEvent(GameEvent e) {
            if (e.getType() == GameEventType.BOSS_PHASE_CHANGED)
                System.out.println("Loot: Potion");

            if (e.getType() == GameEventType.BOSS_DEFEATED)
                System.out.println("Loot: Legendary Sword");
        }
    }
}