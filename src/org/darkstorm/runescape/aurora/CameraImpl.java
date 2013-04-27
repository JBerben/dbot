package org.darkstorm.runescape.aurora;

import java.awt.event.KeyEvent;

import org.darkstorm.runescape.api.Camera;
import org.darkstorm.runescape.api.input.TileMouseTarget;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.Player;

public final class CameraImpl extends AbstractUtility implements Camera {

	public CameraImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public double getAngleX() {
		double mapAngle = client.getCameraYaw();
		mapAngle /= 2048;
		mapAngle *= 360;
		return mapAngle;
	}

	@Override
	public double getAngleY() {
		double mapAngle = client.getCameraPitch();
		mapAngle /= 2048;
		mapAngle *= 360;
		return mapAngle;
	}

	@Override
	public double getAngleXTo(Locatable locatable) {
		Tile location = locatable.getLocation();
		Tile self = context.getPlayers().getSelf().getLocation();
		int x1 = self.getX();
		int y1 = self.getY();
		int x = x1 - location.getX();
		int y = y1 - location.getY();
		double angle = Math.toDegrees(Math.atan2(x, y));
		if(x == 0 && y > 0)
			angle = 180;
		if(x < 0 && y == 0)
			angle = 90;
		if(x == 0 && y < 0)
			angle = 0;
		if(x < 0 && y == 0)
			angle = 270;
		if(x < 0 && y > 0)
			angle += 270;
		if(x > 0 && y > 0)
			angle += 90;
		if(x < 0 && y < 0)
			angle = Math.abs(angle) - 180;
		if(x > 0 && y < 0)
			angle = Math.abs(angle) + 270;
		if(angle < 0)
			angle = 360 + angle;
		if(angle >= 360)
			angle -= 360;
		return (int) angle;
	}

	@Override
	public double getAngleYTo(Locatable locatable) {
		Player self = context.getPlayers().getSelf();
		Tile location = self.getLocation();
		Tile objectLocation = locatable.getLocation();
		double height = calculations.getTileHeight(location) / 128D + 1.0;
		double objectHeight = calculations.getTileHeight(objectLocation) / 128D;
		return Math.toDegrees(-Math.atan2(height + objectHeight,
				location.distanceTo(objectLocation)));
	}

	@Override
	public void setAngleX(double angle) {
		char left = KeyEvent.VK_LEFT;
		char right = KeyEvent.VK_RIGHT;
		char whichDir = left;
		double start = getAngleX();

		start = start < 180 ? start + 360 : start;
		angle = angle < 180 ? angle + 360 : angle;

		if(angle > start) {
			if(angle - 180 < start) {
				whichDir = right;
			}
		} else if(start > angle) {
			if(start - 180 >= angle) {
				whichDir = right;
			}
		}
		angle %= 360;

		keyboard.pressKey(whichDir);
		int timeWaited = 0;
		int turnTime = 0;
		while((getAngleX() > angle + 5 || getAngleX() < angle - 5)
				&& turnTime < 6000) {
			calculations.sleep(10);
			turnTime += 10;
			timeWaited += 10;
			if(timeWaited > 500) {
				int time = timeWaited - 500;
				if(time == 0) {
					keyboard.pressKey(whichDir);
				} else if(time % 40 == 0) {
					keyboard.pressKey(whichDir);
				}
			}
		}
		keyboard.releaseKey(whichDir);
	}

	@Override
	public void setAngleXTo(Locatable locatable) {
		setAngleX(getAngleXTo(locatable));
	}

	@Override
	public void setAngleY(double angle) {
		double percentage = 100 * ((angle - 22D) / 45D);
		if(percentage < 0)
			percentage = 0;
		else if(percentage > 100)
			percentage = 100;
		setAngleYPercentage(percentage);
	}

	@Override
	public void setAngleYTo(Locatable locatable) {
		setAngleY(getAngleYTo(locatable));
	}

	@Override
	public double getAngleYPercentage() {
		return 100 * ((getAngleY() - 22D) / 45D);
	}

	@Override
	public void setAngleYPercentage(double percent) {
		int current = (int) getAngleYPercentage();
		int last = 0;
		if(current < percent) {
			keyboard.pressKey((char) KeyEvent.VK_UP);
			long start = System.currentTimeMillis();
			while(current < percent
					&& System.currentTimeMillis() - start < calculations
							.random(50, 100)) {
				if(last != current)
					start = System.currentTimeMillis();
				last = current;

				calculations.sleep(calculations.random(5, 10));
				current = (int) getAngleYPercentage();
			}
			keyboard.releaseKey((char) KeyEvent.VK_UP);
		} else if(current > percent) {
			keyboard.pressKey((char) KeyEvent.VK_DOWN);
			long start = System.currentTimeMillis();
			while(current > percent
					&& System.currentTimeMillis() - start < calculations
							.random(50, 100)) {
				if(last != current)
					start = System.currentTimeMillis();
				last = current;
				calculations.sleep(calculations.random(5, 10));
				current = (int) getAngleYPercentage();
			}
			keyboard.releaseKey((char) KeyEvent.VK_DOWN);
		}
	}

	@Override
	public void turnTo(Locatable locatable) {
		setAngleXTo(locatable);
		if(calculations.random(0, 10) != 2) {
			if(locatable instanceof ScreenLocatable) {
				if(((ScreenLocatable) locatable).isOnScreen())
					return;
			} else if(new TileMouseTarget(context, locatable.getLocation())
					.getTarget() == null)
				return;
		}
		setAngleYTo(locatable);
	}

	@Override
	public void setCompassDirection(Direction direction) {
		switch(direction) {
		case NORTH:
			setAngleX(359);
			break;
		case EAST:
			setAngleX(89);
			break;
		case SOUTH:
			setAngleX(179);
			break;
		case WEST:
			setAngleX(269);
			break;
		}
	}

}
