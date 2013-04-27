import java.awt.Graphics;

import ms.aurora.api.script.ScriptManifest;
import ms.aurora.event.listeners.PaintListener;

import org.darkstorm.runescape.*;
import org.darkstorm.runescape.aurora.DarkBotAurora;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.game.PaintEvent;
import org.darkstorm.runescape.event.script.ScriptStopEvent;
import org.darkstorm.runescape.script.*;

@ScriptManifest(name = "<name>", author = "<author>",
		shortDescription = "<description>", version = 1.337,
		category = "<category>")
public class AuroraScript extends ms.aurora.api.script.Script implements
		EventListener, PaintListener {
	private Script script;
	private boolean started;

	public AuroraScript() {
	}

	@Override
	public void onStart() {
		DarkBot darkbot = new DarkBotAurora(this);
		Bot bot = darkbot.createBot(GameType.OLDSCHOOL);
		ScriptManager manager = bot.getScriptManager();
		try {
			Class<? extends Script> scriptClass = Class.forName("<script>")
					.asSubclass(Script.class);
			script = manager.loadScript(scriptClass);
			script.start();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(ScriptLoadException e) {
			e.printStackTrace();
		}
		started = true;
	}

	@Override
	public int tick() {
		if(!started)
			return 500;
		if(script == null || !script.isActive())
			return -1;
		return 500;
	}

	@Override
	public void onRepaint(Graphics g) {
		if(script == null)
			return;
		Bot bot = script.getBot();
		EventManager eventManager = bot.getEventManager();
		int width = bot.getGame().getWidth(), height = bot.getGame()
				.getHeight();
		eventManager.sendEvent(new PaintEvent(g, width, height));
	}

	/*@Override
	public void onMessage(String message) throws InterruptedException {
		if(script == null)
			return;
		Bot bot = script.getBot();
		EventManager eventManager = bot.getEventManager();
		eventManager.sendEvent(new ServerMessageEvent(message));
	}*/

	@Override
	public void onFinish() {
		System.out.println("Finishing! "
				+ (script == null ? "Script is null." : "Script active: "
						+ script.isActive()));
		if(script != null && script.isActive())
			script.stop();
	}

	@EventHandler
	public void onScriptStop(ScriptStopEvent event) {
		if(script == null || script != event.getScript())
			return;
		destroy();
	}
}
