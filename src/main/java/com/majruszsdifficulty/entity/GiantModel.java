package com.majruszsdifficulty.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;

@Environment( EnvType.CLIENT )
public class GiantModel< Type extends Giant > extends AbstractZombieModel< Type > {
	public GiantModel( ModelPart root ) {
		super( root );
	}

	@Override
	public boolean isAggressive( Giant giant ) {
		return false;
	}

	@Override
	public void setupAnim( Type giant, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
		super.setupAnim( giant, 0.25f * limbSwing, 0.5f * limbSwingAmount, ageInTicks, netHeadYaw, headPitch );
	}
}

