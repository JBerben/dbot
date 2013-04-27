package org.darkstorm.runescape.aurora.wrapper;

import ms.aurora.rt3.PlayerComposite;

import org.darkstorm.runescape.api.wrapper.Player;
import org.darkstorm.runescape.aurora.GameContextImpl;

public class PlayerWrapper extends CharacterWrapper implements Player {
	private ms.aurora.rt3.Player handle;

	public PlayerWrapper(GameContextImpl context, ms.aurora.rt3.Player handle) {
		super(context, handle);
		this.handle = handle;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getName() {
		return handle.getName();
	}

	@Override
	public int getNpcId() {
		return -1;// handle.().getNpcTrans();
	}

	@Override
	public int getTeam() {
		return -1;
	}

	@Override
	public int[] getAppearance() {
		PlayerComposite definition = handle.getComposite();
		if(definition == null)
			return new int[0];
		return definition.getEquipment();
	}

	@Override
	public int[] getColor() {
		return new int[0];// handle.getComposite().getColors();
	}

	@Override
	public boolean isMale() {
		return false;// handle.getComposite().isMale();
	}

	@Override
	public int getLevel() {
		return -1;// handle.get;
	}

	@Override
	public int getPrayerIcon() {
		return -1;// handle.getPrayerIcon();
	}

	@Override
	public int getSkullIcon() {
		return -1;// handle.getSkullIcon();
	}

}
