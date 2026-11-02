package com.majruszsdifficulty.effects;

import net.minecraft.server.level.ServerLevel;
import com.majruszlibrary.entity.EffectHelper;
import com.majruszlibrary.events.OnEntityEffectCheck;
import com.majruszsdifficulty.MajruszsDifficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class BleedingImmunity extends MobEffect {
	static {
		OnEntityEffectCheck.listen( OnEntityEffectCheck::cancelEffect )
			.addCondition( data->EffectHelper.has( MajruszsDifficulty.BLEEDING_IMMUNITY_EFFECT, data.entity ) )
			.addCondition( data->data.effect.equals( MajruszsDifficulty.BLEEDING_EFFECT.get() ) );
	}

	public BleedingImmunity() {
		super( MobEffectCategory.BENEFICIAL, 0xff990000 );
	}

	@Override
	public boolean applyEffectTick( ServerLevel level, LivingEntity entity, int amplifier ) {
		return true;
	}

	@Override
	public void applyInstantaneousEffect( ServerLevel level, @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity entity, int amplifier, double health ) {
		entity.removeEffect( EffectHelper.toHolder( MajruszsDifficulty.BLEEDING_EFFECT.get() ) );
	}

	@Override
	public boolean shouldApplyEffectTickThisTick( int duration, int amplifier ) {
		return false;
	}
}
