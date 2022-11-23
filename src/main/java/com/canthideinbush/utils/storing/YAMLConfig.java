package com.canthideinbush.utils.storing;

import com.canthideinbush.utils.CHIBPlugin;
import com.canthideinbush.utils.CHIBUtils;
import com.canthideinbush.utils.Reflector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Axolotl;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class YAMLConfig extends YamlConfiguration {

    private static final HashMap<Class<?>, Function<Object, String>> serializers = new HashMap<>();
    private static final HashMap<Class<?>, Function<String, Object>> deserializers = new HashMap<>();

    private final CHIBPlugin plugin;


    static {

        //Location
        serializers.put(Location.class, location -> { Location l = (Location) location; return String.format("%s;%s;%s;%s;%s;%s", l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch()); });
        deserializers.put(Location.class, string -> {
            String[] split = string.split(";");
            ArgParser parser = new ArgParser(split);
            return new Location(parser.nextWorld(), parser.nextDouble(), parser.nextDouble(), parser.nextDouble(), parser.nextFloat(), parser.nextFloat());
        });



        //World
        serializers.put(World.class, world -> ((World) world).getName());
        deserializers.put(World.class, Bukkit::getWorld);



        //UUID
        serializers.put(UUID.class, Object::toString);
        deserializers.put(UUID.class, s -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(s);
            } catch (IllegalArgumentException e) {
                return null;
            }
            return uuid;
        });


        //Material
        serializers.put(Material.class, m -> ((Material) m).name());
        deserializers.put(Material.class, m -> Material.valueOf(m.toUpperCase()));



        //Kyori adventure component
        serializers.put(Component.class, c -> LegacyComponentSerializer.legacyAmpersand().serialize((Component) c));
        deserializers.put(Component.class, s -> LegacyComponentSerializer.legacyAmpersand().deserialize(s));


        //Axolotl Variant
        serializers.put(Axolotl.Variant.class, v -> ((Axolotl.Variant) v).name());
        deserializers.put(Axolotl.Variant.class, Axolotl.Variant::valueOf);

    }

    public static <T> Object serialize(Class<?> c, T t) {
        if (t instanceof Collection) {
            return serializeCollection((Collection<?>) t);
        }
        if (t != null && serializers.containsKey(c)) {
            return serializers.get(c).apply(t);
        }

        return t;
    }


    public static <T> Collection<?> serializeCollection(Collection<T> collection) {
        Class<T> type = (Class<T>) Reflector.getCollectionType(collection);
        if (serializers.containsKey(type)) {
            Function<Object, String> serializer = serializers.get(type);
            return collection.stream().map(serializer).collect(Collectors.toList());
        }
        return collection;
    }

    public static <T> Collection<T> deserializeCollection(Class<T> t, Collection<Object> collection) {
        Class<T> type = (Class<T>) Reflector.getCollectionType(collection);
        if (String.class.equals(t) && deserializers.containsKey(t)) {
            Function<String, Object> deserializer = deserializers.get(type);
            return collection.stream().map(o -> (T) deserializer.apply((String) o)).collect(Collectors.toList());
        }
        return (Collection<T>) collection;
    }

    public static <T> T deserialize(Class<T> t, Object data) {
        if (Collection.class.isAssignableFrom(t)) {
            return (T) deserializeCollection(t, (Collection<Object>) data);
        }
        if (data instanceof String && deserializers.containsKey(t)) {
            return (T) deserializers.get(t).apply((String) data);
        }
        return (T) data;
    }


    private final File file;


    public String getPluginsFolderPath() {
        return plugin.getDataFolder() + "/";
    }

    public YAMLConfig(CHIBPlugin plugin, String fileName, boolean copyResource) {
        this.plugin = plugin;
        file = new File(getPluginsFolderPath() + fileName + ".yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (copyResource && plugin.getResource(fileName + ".yml") != null) {

                plugin.saveResource(fileName + ".yml", false);
            }
            else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                load(file);
            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
        else if (copyResource && plugin.getResource(fileName + ".yml") != null) {
            YamlConfiguration def = null;
            try {
                load(file);
                plugin.saveResource(fileName + ".yml", true);
                def = new YamlConfiguration();
                def.load(file);

            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
            }

            new ConfigMerger(def, this).merge();
            save();

        }
        else {
            try {
                load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }



    }

    public YAMLConfig(CHIBPlugin plugin, String fileName) {
        this(plugin, fileName, false);
    }


    public void save() {
        try {
            this.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            this.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
