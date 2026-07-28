package de.rapha149.armorstandeditor;

import de.rapha149.armorstandeditor.version.Axis;
import de.rapha149.armorstandeditor.version.BodyPart;
import de.rapha149.armorstandeditor.version.VersionWrapper;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.decoration.ArmorStand;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class Wrapper implements VersionWrapper {

    @Override
    public Optional<String> getCustomNameJson(org.bukkit.entity.ArmorStand armorStand) {
        return Optional.ofNullable(((CraftArmorStand) armorStand).getHandle().getCustomName()).map(CraftChatMessage::toJSON);
    }

    @Override
    public void setCustomName(org.bukkit.entity.ArmorStand armorStand, String customNameJson) {
        ((CraftArmorStand) armorStand).getHandle().setCustomName(CraftChatMessage.fromJSONOrNull(customNameJson));
    }

    @Override
    public void resetArmorStandBodyPart(org.bukkit.entity.ArmorStand armorStand, BodyPart bodyPart) {
        ArmorStand handle = ((CraftArmorStand) armorStand).getHandle();
        switch (bodyPart) {
            case HEAD -> handle.setHeadPose(new Rotations(0f, 0f, 0f));
            case BODY -> handle.setBodyPose(new Rotations(0f, 0f, 0f));
            case LEFT_ARM -> handle.setLeftArmPose(new Rotations(-10f, 0f, -10f));
            case RIGHT_ARM -> handle.setRightArmPose(new Rotations(-15f, 0f, 10f));
            case LEFT_LEG -> handle.setLeftLegPose(new Rotations(-1f, 0f, -1f));
            case RIGHT_LEG -> handle.setRightLegPose(new Rotations(1f, 0f, 1f));
        }
    }

    @Override
    public void resetArmorStandBodyPart(org.bukkit.entity.ArmorStand armorStand, BodyPart bodyPart, Axis axis) {
        ArmorStand handle = ((CraftArmorStand) armorStand).getHandle();
        Rotations currentAngle, defaultAngle;
        switch (bodyPart) {
            case HEAD:
                currentAngle = handle.getHeadPose();
                defaultAngle = new Rotations(0f, 0f, 0f);
                break;
            case BODY:
                currentAngle = handle.getBodyPose();
                defaultAngle = new Rotations(0f, 0f, 0f);
                break;
            case LEFT_ARM:
                currentAngle = handle.getLeftArmPose();
                defaultAngle = new Rotations(-10f, 0f, -10f);
                break;
            case RIGHT_ARM:
                currentAngle = handle.getRightArmPose();
                defaultAngle = new Rotations(-15f, 0f, 10f);
                break;
            case LEFT_LEG:
                currentAngle = handle.getLeftLegPose();
                defaultAngle = new Rotations(-1f, 0f, -1f);
                break;
            case RIGHT_LEG:
                currentAngle = handle.getRightLegPose();
                defaultAngle = new Rotations(1f, 0f, 1f);
                break;
            default:
                return;
        }

        Rotations newAngle = switch (axis) {
            case X -> new Rotations(defaultAngle.getX(), currentAngle.getY(), currentAngle.getZ());
            case Y -> new Rotations(currentAngle.getX(), defaultAngle.getY(), currentAngle.getZ());
            case Z -> new Rotations(currentAngle.getX(), currentAngle.getY(), defaultAngle.getZ());
        };
        switch (bodyPart) {
            case HEAD -> handle.setHeadPose(newAngle);
            case BODY -> handle.setBodyPose(newAngle);
            case LEFT_ARM -> handle.setLeftArmPose(newAngle);
            case RIGHT_ARM -> handle.setRightArmPose(newAngle);
            case LEFT_LEG -> handle.setLeftLegPose(newAngle);
            case RIGHT_LEG -> handle.setRightLegPose(newAngle);
        }
    }

    @Override
    public ItemStack getArmorstandItem(org.bukkit.entity.ArmorStand armorStand, NamespacedKey privateKey) {
        ArmorStand handle = ((CraftArmorStand) armorStand).getHandle();
        CompoundTag nbt = new CompoundTag();
        try {
            handle.save(nbt);
        } catch (NoSuchMethodError e) {
            try {
                Class<?> problemReporterClass = Class.forName("net.minecraft.util.ProblemReporter");
                Object discarding = problemReporterClass.getField("DISCARDING").get(null);
                Class<?> tagValueOutputClass = Class.forName("net.minecraft.world.level.storage.TagValueOutput");
                java.lang.reflect.Method createWrappingGlobal = tagValueOutputClass.getMethod("createWrappingGlobal", problemReporterClass, CompoundTag.class);
                Object valueOutput = createWrappingGlobal.invoke(null, discarding, nbt);
                java.lang.reflect.Method saveMethod = net.minecraft.world.entity.Entity.class.getMethod("save", Class.forName("net.minecraft.world.level.storage.ValueOutput"));
                saveMethod.invoke(handle, valueOutput);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        nbt.putString("id", "minecraft:armor_stand");
        removeTag(nbt, "Pos");
        removeTag(nbt, "UUID");
        removeTag(nbt, "WorldUUIDLeast");
        removeTag(nbt, "WorldUUIDMost");
        removeTag(nbt, "Passengers");

        if (armorStand.isVisualFire()) {
            net.minecraft.nbt.ListTag tags = nbt.contains("Tags", 9) ? nbt.getList("Tags", 8) : new net.minecraft.nbt.ListTag();
            tags.add(net.minecraft.nbt.StringTag.valueOf(FIRE_TAG));
            nbt.put("Tags", tags);
        }
        if (armorStand.isInvisible()) {
            net.minecraft.nbt.ListTag tags = nbt.contains("Tags", 9) ? nbt.getList("Tags", 8) : new net.minecraft.nbt.ListTag();
            tags.add(net.minecraft.nbt.StringTag.valueOf(INVISIBLE_TAG));
            nbt.put("Tags", tags);
        }

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(new ItemStack(Material.ARMOR_STAND));
        nmsItem.set(net.minecraft.core.component.DataComponents.ENTITY_DATA, net.minecraft.world.item.component.CustomData.of(nbt));

        ItemStack item = CraftItemStack.asBukkitCopy(nmsItem);
        ItemMeta meta = item.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    private static void removeTag(CompoundTag tag, String key) {
        try {
            java.lang.reflect.Method removeMethod = CompoundTag.class.getMethod("remove", String.class);
            removeMethod.invoke(tag, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack prepareRecipeResult(ItemStack item) {
        if (item.getType() != Material.ARMOR_STAND)
            return null;

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        net.minecraft.world.item.component.CustomData customData = nmsItem.get(net.minecraft.core.component.DataComponents.ENTITY_DATA);
        if (customData != null) {
            CompoundTag entityNBT = customData.copyTag();
            removeTag(entityNBT, "equipment");
            nmsItem.set(net.minecraft.core.component.DataComponents.ENTITY_DATA, net.minecraft.world.item.component.CustomData.of(entityNBT));
        }

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
}
