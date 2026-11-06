package compat.modmenu;

import com.majruszsdifficulty.client.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

// Outside com.majruszsdifficulty on purpose: Majrusz Library's ClassFinder loads every class there, and this one
// can't load when Mod Menu isn't installed.
public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory< ? > getModConfigScreenFactory() {
		return ConfigScreen::new;
	}
}
