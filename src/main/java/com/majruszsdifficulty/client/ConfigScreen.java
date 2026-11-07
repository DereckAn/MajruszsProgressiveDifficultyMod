package com.majruszsdifficulty.client;

import com.majruszlibrary.math.Range;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.bloodmoon.BloodMoonConfig;
import com.majruszsdifficulty.data.MergeConfig;
import com.majruszsdifficulty.data.MergeConfig.Difficulty;
import com.majruszsdifficulty.undeadarmy.UndeadArmyConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Step 7e: in-game settings for the merged mod (opened from Mod Menu). Edits the same static fields as
 * config/majruszsdifficulty.json and saves that file.
 * ponytail: singleplayer only; on a dedicated server the server's own file wins and is synced back to clients.
 */
@Environment( EnvType.CLIENT )
public class ConfigScreen extends Screen {
	private static final int WIDTH = 150;
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout( this );
	private final List< Runnable > appliers = new ArrayList<>();

	public ConfigScreen( Screen parent ) {
		super( Component.literal( "Majrusz's Progressive Difficulty + Improved Mobs" ) );
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.appliers.clear();
		this.layout.addTitleHeader( this.title, this.font );

		GridLayout grid = new GridLayout().columnSpacing( 10 ).rowSpacing( 2 );
		GridLayout.RowHelper rows = grid.createRowHelper( 2 );

		this.label( rows, "Difficulty (easiest → Inferno)" );
		DifficultySlider difficulty = rows.addChild( new DifficultySlider( MergeConfig.DIFFICULTY ) );
		this.appliers.add( ()->MergeConfig.DIFFICULTY = difficulty.selected() );


		EditBox armyInterval = this.number( rows, "Army every N difficulty (0 = off)", UndeadArmyConfig.DIFFICULTY_INTERVAL );
		this.appliers.add( ()->UndeadArmyConfig.DIFFICULTY_INTERVAL = Range.of( 0, 500 ).clamp( parseInt( armyInterval, UndeadArmyConfig.DIFFICULTY_INTERVAL ) ) );

		EditBox armyKills = this.number( rows, "Army after N undead kills (0 = off)", UndeadArmyConfig.KILL_REQUIREMENT );
		this.appliers.add( ()->UndeadArmyConfig.KILL_REQUIREMENT = Range.of( 0, 1000 ).clamp( parseInt( armyKills, UndeadArmyConfig.KILL_REQUIREMENT ) ) );

		this.label( rows, "Army mobs glow" );
		CycleButton< Boolean > glow = rows.addChild( CycleButton.booleanBuilder( Component.literal( "Always" ), Component.literal( "After delay" ), UndeadArmyConfig.ALWAYS_HIGHLIGHT )
			.displayOnlyValue()
			.create( 0, 0, WIDTH, 20, Component.literal( "Army mobs glow" ) ) );
		this.appliers.add( ()->UndeadArmyConfig.ALWAYS_HIGHLIGHT = glow.getValue() );

		EditBox totemCost = this.number( rows, "Totem difficulty cost (Improved Mobs)", UndeadArmyConfig.TOTEM_DIFFICULTY_COST );
		this.appliers.add( ()->UndeadArmyConfig.TOTEM_DIFFICULTY_COST = Range.of( 0, 500 ).clamp( parseInt( totemCost, UndeadArmyConfig.TOTEM_DIFFICULTY_COST ) ) );

		this.label( rows, "Blood moon" );
		CycleButton< Boolean > bloodMoon = this.toggle( rows, BloodMoonConfig.IS_ENABLED );
		this.appliers.add( ()->BloodMoonConfig.IS_ENABLED = bloodMoon.getValue() );

		EditBox moonInterval = this.number( rows, "Blood moon every N difficulty (0 = off)", BloodMoonConfig.DIFFICULTY_INTERVAL );
		this.appliers.add( ()->BloodMoonConfig.DIFFICULTY_INTERVAL = Range.of( 0, 500 ).clamp( parseInt( moonInterval, BloodMoonConfig.DIFFICULTY_INTERVAL ) ) );

		EditBox moonChance = this.number( rows, "Random blood moon chance per night (%)", Math.round( BloodMoonConfig.NIGHT_TRIGGER_CHANCE * 1000.0f ) / 10.0f );
		this.appliers.add( ()->BloodMoonConfig.NIGHT_TRIGGER_CHANCE = Range.CHANCE.clamp( parseFloat( moonChance, BloodMoonConfig.NIGHT_TRIGGER_CHANCE * 100.0f ) / 100.0f ) );

		this.layout.addToContents( grid );

		LinearLayout footer = this.layout.addToFooter( LinearLayout.horizontal().spacing( 8 ) );
		footer.addChild( Button.builder( CommonComponents.GUI_DONE, button->this.save() ).width( WIDTH ).build() );
		footer.addChild( Button.builder( CommonComponents.GUI_CANCEL, button->this.onClose() ).width( WIDTH ).build() );

		this.layout.visitWidgets( this::addRenderableWidget );
		this.repositionElements();
	}

	@Override
	protected void repositionElements() {
		this.layout.arrangeElements();
	}

	@Override
	public void onClose() {
		this.minecraft.gui.setScreen( this.parent );
	}

	private void save() {
		this.appliers.forEach( Runnable::run );
		MajruszsDifficulty.CONFIG.save();
		this.onClose();
	}

	private void label( GridLayout.RowHelper rows, String text ) {
		rows.addChild( new StringWidget( WIDTH + 40, 20, Component.literal( text ), this.font ) );
	}

	private CycleButton< Boolean > toggle( GridLayout.RowHelper rows, boolean value ) {
		return rows.addChild( CycleButton.onOffBuilder( value ).displayOnlyValue().create( 0, 0, WIDTH, 20, Component.empty() ) );
	}

	private EditBox number( GridLayout.RowHelper rows, String label, Number value ) {
		this.label( rows, label );
		EditBox box = new EditBox( this.font, WIDTH, 20, Component.literal( label ) );
		box.setValue( value instanceof Float f && f == Math.floor( f ) ? String.valueOf( f.intValue() ) : value.toString() );

		return rows.addChild( box );
	}

	private static int parseInt( EditBox box, int fallback ) {
		try {
			return Integer.parseInt( box.getValue().trim() );
		} catch( NumberFormatException exception ) {
			return fallback; // ponytail: invalid input keeps the old value
		}
	}

	private static float parseFloat( EditBox box, float fallback ) {
		try {
			return Float.parseFloat( box.getValue().trim().replace( ",", "." ) );
		} catch( NumberFormatException exception ) {
			return fallback;
		}
	}

	private static Component difficultyName( Difficulty value ) {
		return switch( value ) {
			case MAJRUSZ -> Component.literal( "Majrusz (game stages)" );
			case GENTLE -> Component.literal( "Gentle" );
			case STANDARD -> Component.literal( "Standard" );
			case HARD -> Component.literal( "Hard" );
			case BRUTAL -> Component.literal( "Brutal" );
			case INFERNO -> Component.literal( "Inferno" ).withStyle( net.minecraft.ChatFormatting.RED, net.minecraft.ChatFormatting.BOLD );
		};
	}

	private static Component difficultyTooltip( Difficulty value ) {
		return Component.literal( switch( value ) {
			case MAJRUSZ -> "Only Majrusz's game stage bonus. Hit cap and crowd dampening on.";
			case GENTLE -> "Improved Mobs scaling with half its damage bonus. Hit cap and crowd dampening on.";
			case STANDARD -> "Improved Mobs scaling. Hit cap and crowd dampening on.";
			case HARD -> "Improved Mobs scaling. No hit cap; crowd dampening on.";
			case BRUTAL -> "Improved Mobs + Majrusz scaling stacked. Crowd dampening on.";
			case INFERNO -> "Both scalings stacked. No limits: no hit cap, no crowd dampening.";
		} );
	}

	/** Left to right, easiest to hardest (MergeConfig.Difficulty). Snaps to the steps; the tooltip explains the current one. */
	private static class DifficultySlider extends net.minecraft.client.gui.components.AbstractSliderButton {
		private static final int STEPS = Difficulty.values().length - 1;

		DifficultySlider( Difficulty value ) {
			super( 0, 0, WIDTH, 20, Component.empty(), value.ordinal() / ( double )STEPS );
			this.updateMessage();
		}

		Difficulty selected() {
			return Difficulty.values()[ ( int )Math.round( this.value * STEPS ) ];
		}

		@Override
		protected void updateMessage() {
			this.setMessage( difficultyName( this.selected() ) );
			this.setTooltip( net.minecraft.client.gui.components.Tooltip.create( difficultyTooltip( this.selected() ) ) );
		}

		@Override
		protected void applyValue() {
			this.value = this.selected().ordinal() / ( double )STEPS;
		}
	}
}
