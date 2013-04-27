package org.darkstorm.runescape.aurora;

import java.util.*;

import org.darkstorm.runescape.api.Players;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.Player;
import org.darkstorm.runescape.aurora.wrapper.PlayerWrapper;

public final class PlayersImpl extends AbstractUtility implements Players {

	public PlayersImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public Player getSelf() {
		// ms.aurora.rt3.Player[] players = client.getAllPlayers();
		// if(players == null || players.length != 2048)
		// return null;
		ms.aurora.rt3.Player local = client.getLocalPlayer();// players[2047];
		if(local == null)
			return null;
		return new PlayerWrapper(context, local);
	}

	@Override
	public Player getClosest(Filter<Player> filter) {
		Player closest = null;
		double minDist = 0;
		Tile self = getSelf().getLocation();
		for(Player player : getAll()) {
			if(filter.accept(player)) {
				double dist = self.distanceTo(player.getLocation());
				if(dist < minDist || closest == null) {
					closest = player;
					minDist = dist;
				}
			}
		}
		return closest;
	}

	@Override
	public Player[] getAll(Filter<Player> filter) {
		List<Player> accepted = new ArrayList<Player>();
		for(Player player : getAll())
			if(filter.accept(player))
				accepted.add(player);
		return accepted.toArray(new Player[accepted.size()]);
	}

	@Override
	public Player[] getAll() {
		ms.aurora.rt3.Player[] players = client.getAllPlayers();
		List<Player> all = new ArrayList<Player>();
		for(ms.aurora.rt3.Player player : players)
			if(player != null)
				all.add(new PlayerWrapper(context, player));
		return all.toArray(new Player[all.size()]);
	}

}
