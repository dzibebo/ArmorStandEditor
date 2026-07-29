package de.rapha149.armorstandeditor;

import de.rapha149.armorstandeditor.version.Axis;
import de.rapha149.armorstandeditor.version.BodyPart;
import de.rapha149.armorstandeditor.version.VersionWrapper;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.decoration.ArmorStand;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

import java.util.Optional;

public class Wrapper implements VersionWrapper {

    // Keys for storing armor stand data in PDC (PersistentDataContainer)
    private static final NamespacedKey KEY_INVISIBLE    = new NamespacedKey("armorstandeditor", "invisible");
    private static final NamespacedKey KEY_FIRE         = new NamespacedKey("armorstandeditor", "fire");
    private static final NamespacedKey KEY_SMALL        = new NamespacedKey("armorstandeditor", "small");
    private static final NamespacedKey KEY_ARMS         = new NamespacedKey("armorstandeditor", "arms");
    private static final NamespacedKey KEY_BASE_PLATE   = new NamespacedKey("armorstandeditor", "base_plate");
    private static final NamespacedKey KEY_GRAVITY      = new NamespacedKey("armorstandeditor", "gravity");
    private static final NamespacedKey KEY_GLOWING      = new NamespacedKey("armorstandeditor", "glowing");
    private static final NamespacedKey KEY_HEAD_POSE    = new NamespacedKey("armorstandeditor", "head_pose");
    private static final NamespacedKey KEY_BODY_POSE    = new NamespacedKey("armorstandeditor", "body_pose");
    private static final NamespacedKey KEY_LEFT_ARM     = new NamespacedKey("armorstandeditor", "left_arm");
    private static final NamespacedKey KEY_RIGHT_ARM    = new NamespacedKey("armorstandeditor", "right_arm");
    private static final NamespacedKey KEY_LEFT_LEG     = new NamespacedKey("armorstandeditor", "left_leg");
    private static final NamespacedKey KEY_RIGHT_LEG    = new NamespacedKey("armorstandeditor", "right_leg");
    private static final NamespacedKey KEY_HELMET       = new NamespacedKey("armorstandeditor", "helmet");
    private static final NamespacedKey KEY_CHESTPLATE   = new NamespacedKey("armorstandeditor", "chestplate");
    private static final NamespacedKey KEY_LEGGINGS     = new NamespacedKey("armorstandeditor", "leggings");
    private static final NamespacedKey KEY_BOOTS        = new NamespacedKey("armorstandeditor", "boots");
    private static final NamespacedKey KEY_HAND         = new NamespacedKey("armorstandeditor", "hand");
    private static final NamespacedKey KEY_OFFHAND      = new NamespacedKey("armorstandeditor", "offhand");

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
            case HEAD      -> handle.setHeadPose(new Rotations(0f, 0f, 0f));
            case BODY      -> handle.setBodyPose(new Rotations(0f, 0f, 0f));
            case LEFT_ARM  -> handle.setLeftArmPose(new Rotations(-10f, 0f, -10f));
            case RIGHT_ARM -> handle.setRightArmPose(new Rotations(-15f, 0f, 10f));
            case LEFT_LEG  -> handle.setLeftLegPose(new Rotations(-1f, 0f, -1f));
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
            case HEAD      -> handle.setHeadPose(newAngle);
            case BODY      -> handle.setBodyPose(newAngle);
            case LEFT_ARM  -> handle.setLeftArmPose(newAngle);
            case RIGHT_ARM -> handle.setRightArmPose(newAngle);
            case LEFT_LEG  -> handle.setLeftLegPose(newAngle);
            case RIGHT_LEG -> handle.setRightLegPose(newAngle);
        }
    }

    private static String angleToString(EulerAngle angle) {
        return angle.getX() + "," + angle.getY() + "," + angle.getZ();
    }

    private static EulerAngle angleFromString(String s) {
        String[] parts = s.split(",");
        return new EulerAngle(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }

    @Override
    public ItemStack getArmorstandItem(org.bukkit.entity.ArmorStand armorStand, NamespacedKey privateKey) {
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        // Store all armor stand state into PDC
        var pdc = meta.getPersistentDataContainer();
        pdc.set(KEY_INVISIBLE,  PersistentDataType.BYTE, armorStand.isInvisible() ? (byte) 1 : (byte) 0);
        pdc.set(KEY_FIRE,       PersistentDataType.BYTE, armorStand.isVisualFire() ? (byte) 1 : (byte) 0);
        pdc.set(KEY_SMALL,      PersistentDataType.BYTE, armorStand.isSmall() ? (byte) 1 : (byte) 0);
        pdc.set(KEY_ARMS,       PersistentDataType.BYTE, armorStand.hasArms() ? (byte) 1 : (byte) 0);
        pdc.set(KEY_BASE_PLATE, PersistentDataType.BYTE, armorStand.hasBasePlate() ? (byte) 1 : (byte) 0);
        pdc.set(KEY_GRAVITY,    PersistentDataType.BYTE, armorStand.hasGravity() ? (byte) 1 : (byte) 0);
        pdc.set(KEY_GLOWING,    PersistentDataType.BYTE, armorStand.isGlowing() ? (byte) 1 : (byte) 0);

        pdc.set(KEY_HEAD_POSE,  PersistentDataType.STRING, angleToString(armorStand.getHeadPose()));
        pdc.set(KEY_BODY_POSE,  PersistentDataType.STRING, angleToString(armorStand.getBodyPose()));
        pdc.set(KEY_LEFT_ARM,   PersistentDataType.STRING, angleToString(armorStand.getLeftArmPose()));
        pdc.set(KEY_RIGHT_ARM,  PersistentDataType.STRING, angleToString(armorStand.getRightArmPose()));
        pdc.set(KEY_LEFT_LEG,   PersistentDataType.STRING, angleToString(armorStand.getLeftLegPose()));
        pdc.set(KEY_RIGHT_LEG,  PersistentDataType.STRING, angleToString(armorStand.getRightLegPose()));

        // Store equipment
        var equipment = armorStand.getEquipment();
        serializeItemStack(pdc, KEY_HELMET,     equipment.getItem(EquipmentSlot.HEAD));
        serializeItemStack(pdc, KEY_CHESTPLATE, equipment.getItem(EquipmentSlot.CHEST));
        serializeItemStack(pdc, KEY_LEGGINGS,   equipment.getItem(EquipmentSlot.LEGS));
        serializeItemStack(pdc, KEY_BOOTS,      equipment.getItem(EquipmentSlot.FEET));
        serializeItemStack(pdc, KEY_HAND,       equipment.getItem(EquipmentSlot.HAND));
        serializeItemStack(pdc, KEY_OFFHAND,    equipment.getItem(EquipmentSlot.OFF_HAND));

        // Private key
        if (privateKey != null && armorStand.getPersistentDataContainer().has(privateKey, PersistentDataType.STRING)) {
            String owner = armorStand.getPersistentDataContainer().get(privateKey, PersistentDataType.STRING);
            pdc.set(privateKey, PersistentDataType.STRING, owner);
        }

        meta.setEnchantmentGlintOverride(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    private void serializeItemStack(org.bukkit.persistence.PersistentDataContainer pdc, NamespacedKey key, ItemStack stack) {
        if (stack != null && stack.getType() != Material.AIR) {
            pdc.set(key, PersistentDataType.BYTE_ARRAY, stack.serializeAsBytes());
        }
    }

    @Override
    public ItemStack prepareRecipeResult(ItemStack item) {
        if (item.getType() != Material.ARMOR_STAND)
            return null;
        // For the copy recipe, just remove the equipment keys from PDC so the copy doesn't have equipment
        ItemStack copy = item.clone();
        ItemMeta meta = copy.getItemMeta();
        if (meta != null) {
            var pdc = meta.getPersistentDataContainer();
            pdc.remove(KEY_HELMET);
            pdc.remove(KEY_CHESTPLATE);
            pdc.remove(KEY_LEGGINGS);
            pdc.remove(KEY_BOOTS);
            pdc.remove(KEY_HAND);
            pdc.remove(KEY_OFFHAND);
            copy.setItemMeta(meta);
        }
        return copy;
    }
}
