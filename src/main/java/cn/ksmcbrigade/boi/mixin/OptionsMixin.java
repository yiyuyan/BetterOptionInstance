package cn.ksmcbrigade.boi.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow protected abstract void processOptions(Options.FieldAccess p_168428_);

    @Shadow public abstract void broadcastOptions();

    @Shadow @Final
    static Logger LOGGER;

    @Shadow @Final private File optionsFile;

    /**
     * @author KSmc_brigade
     * @reason make options no error
     */
    @Overwrite
    public void save() {
        try (final PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
            printwriter.println("version:" + SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            this.processOptions(new Options.FieldAccess() {
                public void writePrefix(String p_168491_) {
                    printwriter.print(p_168491_);
                    printwriter.print(':');
                }

                public <T> void process(String p_232135_, OptionInstance<T> p_232136_) {
                    try {
                        DataResult<JsonElement> dataresult = p_232136_.codec().encodeStart(JsonOps.INSTANCE, p_232136_.get());
                        this.writePrefix(p_232135_);
                        printwriter.println(new Gson().toJson(dataresult.getOrThrow(true,(s)->{})));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                public int process(String p_168499_, int p_168500_) {
                    this.writePrefix(p_168499_);
                    printwriter.println(p_168500_);
                    return p_168500_;
                }

                public boolean process(String p_168515_, boolean p_168516_) {
                    this.writePrefix(p_168515_);
                    printwriter.println(p_168516_);
                    return p_168516_;
                }

                public String process(String p_168512_, String p_168513_) {
                    this.writePrefix(p_168512_);
                    printwriter.println(p_168513_);
                    return p_168513_;
                }

                public float process(String p_168496_, float p_168497_) {
                    this.writePrefix(p_168496_);
                    printwriter.println(p_168497_);
                    return p_168497_;
                }

                public <T> T process(String p_168502_, T p_168503_, Function<String, T> p_168504_, Function<T, String> p_168505_) {
                    this.writePrefix(p_168502_);
                    printwriter.println(p_168505_.apply(p_168503_));
                    return p_168503_;
                }
            });
            if (Minecraft.getInstance().getWindow().getPreferredFullscreenVideoMode().isPresent()) {
                printwriter.println("fullscreenResolution:" + Minecraft.getInstance().getWindow().getPreferredFullscreenVideoMode().get().write());
            }
        } catch (Exception exception) {
            LOGGER.error("Failed to save options", (Throwable)exception);
        }

        this.broadcastOptions();
    }
}
