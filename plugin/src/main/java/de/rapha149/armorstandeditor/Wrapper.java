package de.rapha149.armorstandeditor;

import de.rapha149.armorstandeditor.version.Axis;
import de.rapha149.armorstandeditor.version.BodyPart;
import de.rapha149.armorstandeditor.version.VersionWrapper;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.storage.TagValueOutput;
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
            case HEAD -> handle.setHeadPose(ArmorStand.DEFAULT_HEAD_POSE);
            case BODY -> handle.setBodyPose(ArmorStand.DEFAULT_BODY_POSE);
            case LEFT_ARM -> handle.setLeftArmPose(ArmorStand.DEFAULT_LEFT_ARM_POSE);
            case RIGHT_ARM -> handle.setRightArmPose(ArmorStand.DEFAULT_RIGHT_ARM_POSE);
            case LEFT_LEG -> handle.setLeftLegPose(ArmorStand.DEFAULT_LEFT_LEG_POSE);
            case RIGHT_LEG -> handle.setRightLegPose(ArmorStand.DEFAULT_RIGHT_LEG_POSE);
        }
    }

    @Override
    public void resetArmorStandBodyPart(org.bukkit.entity.ArmorStand armorStand, BodyPart bodyPart, Axis axis) {
        ArmorStand handle = ((CraftArmorStand) armorStand).getHandle();
        Rotations currentAngle, defaultAngle;
        switch (bodyPart) {
            case HEAD:
                currentAngle = handle.getHeadPose();
                defaultAngle = ArmorStand.DEFAULT_HEAD_POSE;
                break;
            case BODY:
                currentAngle = handle.getBodyPose();
                defaultAngle = ArmorStand.DEFAULT_BODY_POSE;
                break;
            case LEFT_ARM:
                currentAngle = handle.getLeftArmPose();
                defaultAngle = ArmorStand.DEFAULT_LEFT_ARM_POSE;
                break;
            case RIGHT_ARM:
                currentAngle = handle.getRightArmPose();
                defaultAngle = ArmorStand.DEFAULT_RIGHT_ARM_POSE;
                break;
            case LEFT_LEG:
                currentAngle = handle.getLeftLegPose();
                defaultAngle = ArmorStand.DEFAULT_LEFT_LEG_POSE;
                break;
            case RIGHT_LEG:
                currentAngle = handle.getRightLegPose();
                defaultAngle = ArmorStand.DEFAULT_RIGHT_LEG_POSE;
                break;
            default:
                return;
        }

        Rotations newAngle = switch (axis) {
            case X -> new Rotations(defaultAngle.x(), currentAngle.y(), currentAngle.z());
            case Y -> new Rotations(currentAngle.x(), defaultAngle.y(), currentAngle.z());
            case Z -> new Rotations(currentAngle.x(), currentAngle.y(), defaultAngle.z());
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
        TagValueOutput output = TagValueOutput.createWithoutContext(new ProblemReporter.Collector());
        handle.saveWithoutId(output);
        CompoundTag nbt = output.buildResult();
        nbt.putString("id", "minecraft:armor_stand");
        nbt.remove("Pos");
        nbt.remove("UUID");
        nbt.remove("WorldUUIDLeast");
        nbt.remove("WorldUUIDMost");
        nbt.remove("Passengers");

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(new ItemStack(Material.ARMOR_STAND));
        CompoundTag itemNBT = nmsItem.isEmpty() ? new CompoundTag() : (CompoundTag) net.minecraft.world.item.ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, nmsItem).getOrThrow();
        CompoundTag components = itemNBT.getCompoundOrEmpty("components");
        components.put("minecraft:entity_data", nbt);
        itemNBT.put("components", components);

        ItemStack item = CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.CODEC.parse(NbtOps.INSTANCE, itemNBT).getOrThrow());
        ItemMeta meta = item.getItemMeta();
        meta.setEnchantmentGlintOverride(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public ItemStack prepareRecipeResult(ItemStack item) {
        if (item.getType() != Material.ARMOR_STAND)
            return null;

        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        CompoundTag nbt = nmsItem.isEmpty() ? new CompoundTag() : (CompoundTag) net.minecraft.world.item.ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, nmsItem).getOrThrow();
        CompoundTag components = nbt.getCompoundOrEmpty("components");

        if (components.contains("minecraft:entity_data")) {
            CompoundTag entityNBT = components.getCompoundOrEmpty("minecraft:entity_data");
            entityNBT.remove("equipment");
        }

        return CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.CODEC.parse(NbtOps.INSTANCE, nbt).getOrThrow());
    }
}
