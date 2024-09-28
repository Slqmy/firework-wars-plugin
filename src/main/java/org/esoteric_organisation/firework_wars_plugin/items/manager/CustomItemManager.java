package org.esoteric_organisation.firework_wars_plugin.items.manager;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.guns.rifle.FireworkRifleItem;
import org.esoteric_organisation.firework_wars_plugin.items.guns.rifle.RifleAmmo;
import org.esoteric_organisation.firework_wars_plugin.items.guns.shotgun.FireworkShotgunItem;
import org.esoteric_organisation.firework_wars_plugin.items.guns.shotgun.ShotgunAmmo;
import org.esoteric_organisation.firework_wars_plugin.items.nms.CustomCrossbow;
import org.esoteric_organisation.firework_wars_plugin.util.ReflectUtil;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class CustomItemManager {
  private FireworkWarsPlugin plugin;
  private final ReflectUtil reflectUtil;

  private final Map<String, AbstractItem> itemRegistry = new HashMap<>();
  private final Map<String, Item> nmsItemRegistry = new HashMap<>();

  public void setPlugin(FireworkWarsPlugin plugin) {
    this.plugin = plugin;
  }

  public CustomItemManager(ReflectUtil reflectUtil) {
    this.reflectUtil = reflectUtil;
  }

  public void registerCustomItems() {
    registerItem(new FireworkRifleItem(plugin));
    registerItem(new RifleAmmo(plugin));

    registerItem(new FireworkShotgunItem(plugin));
    registerItem(new ShotgunAmmo(plugin));
  }

  public void registerNMSItems() {
    registerNMSItem(
        "crossbow",
        new CustomCrossbow(CustomCrossbow.PROPERTIES),
        Items.CROSSBOW);
  }

  public Map<String, AbstractItem> getItemRegistry() {
    return itemRegistry;
  }

  public Map<String, Item> getNMSItemRegistry() {
      return nmsItemRegistry;
  }

  public AbstractItem getItem(String itemId) {
    return itemRegistry.get(itemId);
  }

  public Item getNMSItem(String itemId) {
    return nmsItemRegistry.get(itemId);
  }

  private void registerItem(AbstractItem item) {
    itemRegistry.put(item.getItemId(), item);
  }

  private void registerNMSItem(String id, Item item, Item override) {
    overrideItemRegistryEntry(id, item, override);

    nmsItemRegistry.put(id, item);
  }

  private void overrideItemRegistryEntry(String id, Item item, Item override) {
    ResourceKey<Item> key = ResourceKey.create(
        BuiltInRegistries.ITEM.key(), ResourceLocation.withDefaultNamespace(id));

    RegistrationInfo info = RegistrationInfo.BUILT_IN;

    Holder.Reference<Item> holder = reflectUtil.reflect(MappedRegistry.class, BuiltInRegistries.ITEM, () -> {
      Map<Item, Holder.Reference<Item>> map = reflectUtil.getFieldValue("unregisteredIntrusiveHolders");
      return map.get(item);
    });

    reflectUtil.reflect(Holder.Reference.class, holder, () -> {
      reflectUtil.invokeMethod("bindKey", new Class<?>[] {ResourceKey.class}, key);
    });

    reflectUtil.reflect(MappedRegistry.class, BuiltInRegistries.ITEM, () -> {
      HashMap<ResourceKey<Item>, Holder.Reference<Item>> byKey = reflectUtil.getFieldValue("byKey");
      byKey.put(key, holder);

      HashMap<ResourceLocation, Holder.Reference<Item>> byLocation = reflectUtil.getFieldValue("byLocation");
      byLocation.put(key.location(), holder);

      IdentityHashMap<Item, Holder.Reference<Item>> byValue = reflectUtil.getFieldValue("byValue");
      byValue.put(item, holder);

      ObjectArrayList<Holder.Reference<Item>> byId = reflectUtil.getFieldValue("byId");
      byId.set(byId.indexOf(byValue.remove(override)), holder);

      Reference2IntOpenHashMap<Item> toId = reflectUtil.getFieldValue("toId");
      toId.put(item, toId.getInt(override));
      toId.removeInt(override);

      IdentityHashMap<ResourceKey<Item>, RegistrationInfo> registrationInfos = reflectUtil.getFieldValue("registrationInfos");
      registrationInfos.put(key, info);

      Lifecycle lifecycle = reflectUtil.getFieldValue("registryLifecycle");
      reflectUtil.setFieldValue("registryLifecycle", lifecycle.add(info.lifecycle()));

      Map<Item, Holder.Reference<Item>> unregisteredHolders = reflectUtil.getFieldValue("unregisteredIntrusiveHolders");
      unregisteredHolders.remove(item);
    });
  }
}