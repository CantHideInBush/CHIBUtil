package com.canthideinbush.utils.storing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class ArgParser {


    private final String[] args;
    private int index;

    public ArgParser(String[] args) {
        this.args = args;
        this.index = 0;
    }

    public ArgParser(String[] args, int index) {
        this.args = args;
        this.index = index;
    }

    public boolean hasNext() {
        return args.length > index;
    }

    public boolean hasNext(int i) {
        return args.length > index + i - 1;
    }

    public String current() {
        return args[index];
    }

    public String next() {
        return args[index++];
    }

    public String previous() {
        return args[index - 1];
    }

    public int nextInt() {
        return Integer.parseInt(next());
    }

    public float nextFloat() {
        return Float.parseFloat(next());
    }

    public double nextDouble() {
        return Double.parseDouble(next());
    }

    public boolean nextBoolean() {
        return Boolean.parseBoolean(next());
    }

    public Material nextMaterial() {
        return Material.valueOf(next().toUpperCase());
    }

    public Vector nextVector() {
        return new Vector(nextDouble(), nextDouble(), nextDouble());
    }

    public World nextWorld() {
        return Bukkit.getWorld(next());
    }

    public <T> T nextDeserialize(Class<T> c) {
        return YAMLConfig.deserialize(c, next());
    }

    public int getIndex() {
        return index;
    }

    public int getLength() {
        return args.length;
    }


    /**
     *
     * @return amount of times next() method can be invoked; remaining objects
     */
    public int remaining() {
        return getLength() - getIndex() - 1;
    }

    public String[] remainingArgs() {
        return Arrays.copyOfRange(args, getIndex(), getLength());
    }
}
