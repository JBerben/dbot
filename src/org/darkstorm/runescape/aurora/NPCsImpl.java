package org.darkstorm.runescape.aurora;

import java.util.*;

import ms.aurora.rt3.Npc;

import org.darkstorm.runescape.api.NPCs;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.NPC;
import org.darkstorm.runescape.aurora.wrapper.NPCWrapper;

public final class NPCsImpl extends AbstractUtility implements NPCs {

	public NPCsImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public NPC getClosest(Filter<NPC> filter) {
		final Tile self = context.getPlayers().getSelf().getLocation();
		NPC[] npcs = getAll();
		Arrays.sort(npcs, new Comparator<NPC>() {
			@Override
			public int compare(NPC o1, NPC o2) {
				return Double.compare(self.distanceTo(o1), self.distanceTo(o2));
			}
		});
		for(NPC npc : npcs)
			if(filter.accept(npc))
				return npc;
		return null;
	}

	@Override
	public NPC[] getAll(Filter<NPC> filter) {
		List<NPC> accepted = new ArrayList<NPC>();
		for(NPC npc : getAll())
			if(filter.accept(npc))
				accepted.add(npc);
		return accepted.toArray(new NPC[accepted.size()]);
	}

	@Override
	public NPC[] getAll() {
		Npc[] npcs = client.getAllNpcs();
		List<NPC> all = new ArrayList<NPC>();
		for(Npc npc : npcs)
			if(npc != null)
				all.add(new NPCWrapper(context, npc));
		return all.toArray(new NPC[all.size()]);
	}

}
