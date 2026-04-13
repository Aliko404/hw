package com.narxoz.rpg.combatant;

import com.narxoz.rpg.strategy.CombatStrategy;

public class Hero {

    private final String name;
    private int hp;
    private final int maxHp;
    private final int attackPower;
    private final int defense;

    private CombatStrategy strategy;

    public Hero(String name, int hp, int attackPower, int defense) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attackPower = attackPower;
        this.defense = defense;
    }

    public void setStrategy(CombatStrategy strategy) {
        this.strategy = strategy;
    }

    public int attack() {
        return strategy.calculateDamage(attackPower);
    }

    public int defend() {
        return strategy.calculateDefense(defense);
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public boolean isAlive() { return hp > 0; }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }
}