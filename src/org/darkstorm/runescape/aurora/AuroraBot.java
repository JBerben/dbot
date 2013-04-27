package org.darkstorm.runescape.aurora;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

import ms.aurora.core.*;
import ms.aurora.core.d.a_;
import ms.aurora.event.GlobalEventQueue;
import ms.aurora.rt3.*;

import org.darkstorm.runescape.*;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.script.*;

public class AuroraBot implements Bot {
	private final DarkBot darkbot;
	private final Session session;
	private final Logger logger;
	private final EventManager eventManager;
	private final GameContextImpl context;
	private final ScriptManager scriptManager;
	private final RandomEventManager randomEventManager;

	private final Applet tempGame;

	/*private final Map<Integer, Constructor<? extends MouseEvent>> mouseEvents;
	private final Map<Integer, Constructor<? extends KeyEvent>> keyEvents;
	private final Method setHumanInputMethod, getScriptMethod, getClientMethod,
			getCanvasMethod;*/

	public AuroraBot(DarkBot darkbot, Session session) {
		this.darkbot = darkbot;
		this.session = session;

		tempGame = new Applet();
		tempGame.setBounds(0, 0, 765, 503);

		/*mouseEvents = new HashMap<>();
		keyEvents = new HashMap<>();
		try {
			List<Class<?>> classes = getClassesInPackage(
					org.osbot.engine.Bot.class.getClassLoader(), "org.osbot");
			List<Class<? extends MouseEvent>> mouseEvents = new ArrayList<>();
			List<Class<? extends KeyEvent>> keyEvents = new ArrayList<>();
			for(Class<?> c : classes) {
				Class<?> superclass = c.getSuperclass();
				if(superclass == null)
					continue;
				if(!BotEvent.class.isAssignableFrom(superclass)
						|| Modifier.isAbstract(c.getModifiers()))
					continue;
				if(MouseEvent.class.isAssignableFrom(superclass))
					mouseEvents.add(c.asSubclass(MouseEvent.class));
				if(KeyEvent.class.isAssignableFrom(superclass))
					keyEvents.add(c.asSubclass(KeyEvent.class));
			}
			List<ClassGen> classGens = loadClassGens(
					Bot.class.getClassLoader(), "org.osbot");
			for(ClassGen classGen : classGens) {
				for(Class<? extends MouseEvent> mouseEvent : mouseEvents) {
					if(!mouseEvent.getName().equals(classGen.getClassName()))
						continue;
					for(com.sun.org.apache.bcel.internal.classfile.Method method : classGen
							.getMethods()) {
						if(!method.getName().equals("pushEvent"))
							continue;
						MethodGen methodGen = new MethodGen(method,
								classGen.getClassName(),
								classGen.getConstantPool());
						InstructionList instructionList = methodGen
								.getInstructionList();
						Instruction[] instructions = instructionList
								.getInstructions();
						for(int i = instructions.length - 1; i >= 0; i--) {
							Instruction ins = instructions[i];
							if(!(ins instanceof INVOKEINTERFACE))
								continue;
							String methodName = ((INVOKEINTERFACE) ins)
									.getMethodName(classGen.getConstantPool());
							int eventType;
							switch(methodName) {
							case "mouseMoved":
								eventType = MouseEvent.MOUSE_MOVED;
								break;
							case "mouseClicked":
								eventType = MouseEvent.MOUSE_CLICKED;
								break;
							case "mousePressed":
								eventType = MouseEvent.MOUSE_PRESSED;
								break;
							case "mouseReleased":
								eventType = MouseEvent.MOUSE_RELEASED;
								break;
							case "mouseDragged":
								eventType = MouseEvent.MOUSE_DRAGGED;
								break;
							case "mouseEntered":
								eventType = MouseEvent.MOUSE_ENTERED;
								break;
							case "mouseExited":
								eventType = MouseEvent.MOUSE_EXITED;
								break;
							default:
								continue;
							}
							Constructor<? extends MouseEvent> mouseEventConstructor = null;
							for(Constructor<?> constructor : mouseEvent
									.getConstructors())
								if(constructor.getParameterTypes()[0]
										.equals(Component.class))
									mouseEventConstructor = mouseEvent
											.getConstructor(constructor
													.getParameterTypes());
							if(mouseEventConstructor != null) {
								System.out.println("Event found: " + methodName
										+ " = " + mouseEvent.getName());
								this.mouseEvents.put(eventType,
										mouseEventConstructor);
							}
							break;
						}
					}
				}
				for(Class<? extends KeyEvent> keyEvent : keyEvents) {
					if(!keyEvent.getName().equals(classGen.getClassName()))
						continue;
					for(com.sun.org.apache.bcel.internal.classfile.Method method : classGen
							.getMethods()) {
						if(!method.getName().equals("pushEvent"))
							continue;
						MethodGen methodGen = new MethodGen(method,
								classGen.getClassName(),
								classGen.getConstantPool());
						InstructionList instructionList = methodGen
								.getInstructionList();
						Instruction[] instructions = instructionList
								.getInstructions();
						for(int i = instructions.length - 1; i >= 0; i--) {
							Instruction ins = instructions[i];
							if(!(ins instanceof INVOKEINTERFACE))
								continue;
							String methodName = ((INVOKEINTERFACE) ins)
									.getMethodName(classGen.getConstantPool());
							int eventType;
							switch(methodName) {
							case "keyPressed":
								eventType = KeyEvent.KEY_PRESSED;
								break;
							case "keyReleased":
								eventType = KeyEvent.KEY_RELEASED;
								break;
							case "keyTyped":
								eventType = KeyEvent.KEY_TYPED;
								break;
							default:
								continue;
							}
							Constructor<? extends KeyEvent> keyEventConstructor = null;
							for(Constructor<?> constructor : keyEvent
									.getConstructors())
								if(constructor.getParameterTypes()[0]
										.equals(Component.class))
									keyEventConstructor = keyEvent
											.getConstructor(constructor
													.getParameterTypes());
							if(keyEventConstructor != null) {
								System.out.println("Key event found: "
										+ methodName + " = "
										+ keyEvent.getName());
								this.keyEvents.put(eventType,
										keyEventConstructor);
							}
							break;
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		Method setHumanInputMethod = null, getScriptMethod = null, getCanvasMethod = null, getClientMethod = null;
		for(Method method : bot.getClass().getMethods()) {
			if(!Modifier.isPublic(method.getModifiers())
					|| Modifier.isStatic(method.getModifiers()))
				continue;
			if(method.getParameterTypes().length == 1
					&& method.getParameterTypes()[0] == Boolean.TYPE
					&& method.getReturnType() == Void.class)
				setHumanInputMethod = method;
			else if(method.getParameterTypes().length == 0)
				if(method.getReturnType() == BotCanvas.class)
					getCanvasMethod = method;
				else if(method.getReturnType() == Client.class)
					getClientMethod = method;
				else if(method.getReturnType() == Script.class)
					getScriptMethod = method;
		}
		System.out.println("setHumanInput: " + setHumanInputMethod);
		System.out.println("getScript: " + getScriptMethod);
		System.out.println("getCanvas: " + getCanvasMethod);
		System.out.println("getClient: " + getClientMethod);
		this.setHumanInputMethod = setHumanInputMethod;
		this.getScriptMethod = getScriptMethod;
		this.getCanvasMethod = getCanvasMethod;
		this.getClientMethod = getClientMethod;*/

		// final org.slf4j.Logger botLogger =
		// LoggerFactory.getLogger(getName());

		logger = Logger.getLogger(getName());
		/*for(Handler handler : logger.getHandlers())
			logger.removeHandler(handler);
		logger.addHandler(new Handler() {
			@Override
			public void publish(LogRecord record) {
				record.setSourceClassName(logger.getName());
				if(!logger.isLoggable(record.getLevel()))
					return;
				String message = record.getMessage();
				Throwable thrown = record.getThrown();
				if(thrown != null) {
					if(record.getLevel() == Level.INFO)
						botLogger.info(message, thrown);
					else if(record.getLevel() == Level.SEVERE)
						botLogger.error(message, thrown);
					else if(record.getLevel() == Level.CONFIG)
						botLogger.debug(message, thrown);
					else if(record.getLevel() == Level.FINE
							|| record.getLevel() == Level.FINER
							|| record.getLevel() == Level.FINEST)
						botLogger.trace(message, thrown);
				} else if(message != null) {
					if(record.getLevel() == Level.INFO)
						botLogger.info(message);
					else if(record.getLevel() == Level.SEVERE)
						botLogger.error(message);
					else if(record.getLevel() == Level.CONFIG)
						botLogger.debug(message);
					else if(record.getLevel() == Level.FINE
							|| record.getLevel() == Level.FINER
							|| record.getLevel() == Level.FINEST)
						botLogger.trace(message);
				}
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		});*/
		eventManager = new BasicEventManager();
		context = new GameContextImpl(this);
		scriptManager = new ScriptManagerImpl(this);
		randomEventManager = new RandomEventManagerImpl(this);
	}

	@Override
	public String getName() {
		return "Aurora";
	}

	@Override
	public InputState getInputState() {
		return InputState.MOUSE_KEYBOARD;
	}

	@Override
	public void setInputState(InputState state) {
		boolean toggled;
		switch(state) {
		case MOUSE_KEYBOARD:
			toggled = true;
			break;
		default:
			toggled = false;
		}
		GlobalEventQueue.blocking = !toggled;
	}

	@Override
	public boolean canPlayScript() {
		return session.getScriptManager().f() == a_.STOPPED;
	}

	@Override
	public Component getDisplay() {
		return session.getApplet().getParent();
	}

	@Override
	public Applet getGame() {
		return tempGame;
	}

	@Override
	public Canvas getCanvas() {
		return getClient().getCanvas();
	}

	public Client getClient() {
		return (Client) session.getApplet();
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void dispatchInputEvent(InputEvent event) {
		Client client = getClient();
		if(event instanceof MouseEvent) {
			Mouse mouse = client.getMouse();
			MouseEvent mouseEvent = (MouseEvent) event;
			switch(mouseEvent.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				mouse.mousePressed(mouseEvent);
				break;
			case MouseEvent.MOUSE_RELEASED:
				mouse.mouseReleased(mouseEvent);
				break;
			case MouseEvent.MOUSE_CLICKED:
				mouse.mouseClicked(mouseEvent);
				break;
			case MouseEvent.MOUSE_ENTERED:
				mouse.mouseEntered(mouseEvent);
				break;
			case MouseEvent.MOUSE_EXITED:
				mouse.mouseExited(mouseEvent);
				break;
			case MouseEvent.MOUSE_MOVED:
				mouse.mouseMoved(mouseEvent);
				break;
			case MouseEvent.MOUSE_DRAGGED:
				mouse.mouseDragged(mouseEvent);
				break;
			}
		} else if(event instanceof KeyEvent) {
			KeyEvent keyEvent = (KeyEvent) event;
			for(KeyListener listener : getCanvas().getKeyListeners()) {
				switch(keyEvent.getID()) {
				case KeyEvent.KEY_PRESSED:
					listener.keyPressed(keyEvent);
					break;
				case KeyEvent.KEY_RELEASED:
					listener.keyReleased(keyEvent);
					break;
				case KeyEvent.KEY_TYPED:
					listener.keyTyped(keyEvent);
					break;
				}
			}
		}
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public ScriptManager getScriptManager() {
		return scriptManager;
	}

	@Override
	public RandomEventManager getRandomEventManager() {
		return randomEventManager;
	}

	@Override
	public GameContextImpl getGameContext() {
		return context;
	}

	public Session getSession() {
		return session;
	}

	@Override
	public DarkBot getDarkBot() {
		return darkbot;
	}

}
