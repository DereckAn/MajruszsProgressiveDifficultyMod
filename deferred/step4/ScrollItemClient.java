	private static class Client {
		static {
			OnEntityModelSetup.listen( Client::modify )
				.addCondition( data->data.entity.isUsingItem() )
				.addCondition( data->data.entity.getUseItem().getItem() instanceof ScrollItem );

			OnItemRendered.listen( Client::modify )
				.addCondition( data->data.player.isUsingItem() )
				.addCondition( data->data.player.getUseItem().equals( data.itemStack ) )
				.addCondition( data->data.itemStack.getItem() instanceof ScrollItem );
		}

		private static void modify( OnEntityModelSetup data ) {
			float ticks = data.entity.getTicksUsingItem() + TimeHelper.getPartialTicks();
			data.model.leftArm.x = 5.0f;
			data.model.leftArm.z = 0.0f;
			data.model.leftArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
			data.model.leftArm.yRot = 0.0f;
			data.model.leftArm.zRot = -2.3561945f;
			data.model.rightArm.x = -5.0f;
			data.model.rightArm.z = 0.0f;
			data.model.rightArm.xRot = Mth.cos( ticks * 0.6662f ) * 0.25f;
			data.model.rightArm.yRot = 0.0f;
			data.model.rightArm.zRot = 2.3561945f;
		}

		private static void modify( OnItemRendered data ) {
			data.poseStack.translate( 0.0, - 0.1 * Math.sin( Side.getLocalPlayer().getTicksUsingItem() + TimeHelper.getPartialTicks() ), 0.0 );
		}
	}
}
