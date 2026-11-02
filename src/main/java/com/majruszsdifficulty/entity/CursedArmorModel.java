package com.majruszsdifficulty.entity;

import com.majruszlibrary.animations.ModelDef;
import com.majruszlibrary.animations.ModelParts;
import com.majruszlibrary.modhelper.Resource;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

@Environment( EnvType.CLIENT )
public class CursedArmorModel extends HumanoidModel< CursedArmorRenderer.State > {
	public static Resource< ModelDef > MODEL = MajruszsDifficulty.HELPER.loadClient( "cursed_armor_model", ModelDef.class );
	public final ModelParts modelParts;

	public CursedArmorModel( ModelPart modelPart ) {
		super( modelPart );

		this.modelParts = new ModelParts( modelPart, MODEL.get() );
	}

	@Override
	public void setupAnim( CursedArmorRenderer.State state ) {
		this.modelParts.reset();

		super.setupAnim( state );

		if( state.animations != null ) {
			state.animations.forEach( animation->animation.apply( this.modelParts, state.ageInTicks ) );
		}
	}
}
