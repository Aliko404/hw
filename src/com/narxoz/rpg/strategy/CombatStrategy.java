package com.narxoz.rpg.strategy;

public interface CombatStrategy {

    int calculateDamage(int basePower);
    int calculateDefense(int baseDefense);
    String getName();

    class Aggressive implements CombatStrategy {
        public int calculateDamage(int basePower) { return (int)(basePower * 1.5); }
        public int calculateDefense(int baseDefense) { return (int)(baseDefense * 0.5); }
        public String getName() { return "Aggressive"; }
    }

    class Defensive implements CombatStrategy {
        public int calculateDamage(int basePower) { return (int)(basePower * 0.7); }
        public int calculateDefense(int baseDefense) { return (int)(baseDefense * 1.5); }
        public String getName() { return "Defensive"; }
    }

    class Balanced implements CombatStrategy {
        public int calculateDamage(int basePower) { return basePower; }
        public int calculateDefense(int baseDefense) { return baseDefense; }
        public String getName() { return "Balanced"; }
    }

    class Phase1 implements CombatStrategy {
        public int calculateDamage(int basePower) { return basePower; }
        public int calculateDefense(int baseDefense) { return baseDefense; }
        public String getName() { return "Phase 1"; }
    }

    class Phase2 implements CombatStrategy {
        public int calculateDamage(int basePower) { return (int)(basePower * 1.4); }
        public int calculateDefense(int baseDefense) { return (int)(baseDefense * 0.8); }
        public String getName() { return "Phase 2"; }
    }

    class Phase3 implements CombatStrategy {
        public int calculateDamage(int basePower) { return (int)(basePower * 2); }
        public int calculateDefense(int baseDefense) { return 0; }
        public String getName() { return "Phase 3"; }
    }
}