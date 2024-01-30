package com.example.mixin.client;

import com.example.SuperflatCustomizeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.LevelScreenProvider;
import net.minecraft.client.world.GeneratorOptionsHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelScreenProvider.class)
public interface ExampleClientMixin {

	@Inject(method = "method_41863", at = @At(value = "RETURN"), cancellable = true)
	private static void redirectSuperflatCustomization(CreateWorldScreen parent, GeneratorOptionsHolder generatorOptionsHolder, CallbackInfoReturnable<Screen> cir) {
		cir.setReturnValue(new SuperflatCustomizeScreen(parent));
//		cir.setReturnValue(new SuperflatCustomizationScreen(parent));
	}

}