import net.minecraft.world.entity.decoration.ArmorStand;

public class Test {
    public static void main(String[] args) {
        for (java.lang.reflect.Method m : ArmorStand.class.getMethods()) {
            System.out.println(m.getName() + " " + m.getReturnType().getName() + " " + java.util.Arrays.toString(m.getParameterTypes()));
        }
    }
}
