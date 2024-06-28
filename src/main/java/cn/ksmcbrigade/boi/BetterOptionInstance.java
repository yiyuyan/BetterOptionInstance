package cn.ksmcbrigade.boi;

import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Mod(BetterOptionInstance.MODID)
public class BetterOptionInstance {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "boi";

    public static File file = new File("config/boi-config.json");

    public static Map<String,OptionInstance<Object>> options = new HashMap<>();

    public BetterOptionInstance() throws IOException, IllegalAccessException {
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println("BOI Loading...");
        new File("config").mkdirs();
        if(!file.exists()){
            Files.writeString(file.toPath(),new JsonObject().toString());
        }
        JsonElement element = JsonParser.parseString(Files.readString(file.toPath()));
        System.out.println(element.isJsonObject());
        if(element.isJsonObject()){
            for(String key:element.getAsJsonObject().keySet()){
                OptionInstance<Object> optionInstance = get(key);
                System.out.println(optionInstance!=null);
                if(optionInstance==null){
                    continue;
                }
                JsonElement data = element.getAsJsonObject().get(key);
                System.out.println(data.isJsonPrimitive());
                if(data.isJsonPrimitive()){
                    if(data.getAsJsonPrimitive()==null) continue;
                    try {
                        Object value = getValue(optionInstance,data.getAsJsonPrimitive());
                        System.out.println(value!=null);
                        if(value==null) return;
                        optionInstance.set(value);
                        System.out.println("Set a option instance in Minecraft options: " + key);
                    }
                    catch (Exception e){
                        System.out.println("error in set a option: "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("BOI Loaded!");
    }

    public static @Nullable OptionInstance<Object> get(String name) throws IllegalAccessException {
        if(options.isEmpty()){
            Options instance = Minecraft.getInstance().options;
            for(Field field:Options.class.getDeclaredFields()){
                if(field.getType().equals(OptionInstance.class)){
                    field.setAccessible(true);
                    options.put(field.getName(),(OptionInstance<Object>) field.get(instance));
                }
            }
        }
        if(options.containsKey(name)){
            return options.get(name);
        }
        return null;
    }

    public static @Nullable Object getValue(OptionInstance<Object> ins, JsonPrimitive primitive) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        Field field = JsonPrimitive.class.getDeclaredField("value");
        field.setAccessible(true);
        Object ret = field.get(primitive);
        if(ret instanceof LazilyParsedNumber lpn){
            Field lpn_value = LazilyParsedNumber.class.getDeclaredField("value");
            lpn_value.setAccessible(true);
            String temp = (String) lpn_value.get(lpn);
            Class<?> clazz = ins.get().getClass();
            if (clazz == Integer.class) {
                return Integer.parseInt(temp);
            } else if (clazz == Double.class) {
                return Double.parseDouble(temp);
            } else if (clazz == Long.class) {
                return Long.parseLong(temp);
            } else if (clazz == Float.class) {
                return Float.parseFloat(temp);
            } else if (clazz == Short.class) {
                return Short.parseShort(temp);
            } else if (clazz == Byte.class) {
                return Byte.valueOf(temp);
            }
            else {
                return null;
            }
        }
        return ret;
    }
}
