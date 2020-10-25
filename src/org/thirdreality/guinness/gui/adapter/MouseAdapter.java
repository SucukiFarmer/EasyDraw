package org.thirdreality.guinness.gui.adapter;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import org.thirdreality.guinness.exec.LoopedThread;
import org.thirdreality.guinness.feature.GIPoint;
import org.thirdreality.guinness.feature.Timer;
import org.thirdreality.guinness.feature.shape.ShapeTransform;
import org.thirdreality.guinness.gui.Display;
import org.thirdreality.guinness.gui.Viewport;
import org.thirdreality.guinness.gui.component.GComponent;
import org.thirdreality.guinness.gui.layer.GLayer;

public class MouseAdapter extends LoopedThread implements MouseMotionListener, MouseListener
{
	/*
	 *  'context' is the variable to use
	 *  for calculating mouse movement and additional data.
	 *  The data related to the given Display can then be used in real-time.
	 *  To do so,
	 *  you can use the given methods below.
	 */
	private Display context;
	
	// The variable is used to calculate the mouse speed below.
	private Point cursorLast = null;
	
	// Keeps the current relative location of the Viewport from the cursor.
	// Assumed to be at (0|0) in the beginning but is refreshed afterwards.
	private Point cursorLocation = new Point(0, 0);
	
	// Self-explaining: the mouse speed related to pixels per cycle.
	private volatile double mouseSpeed = 0f;
	
	/* 
	 * The 'action' variable below tells how the user interacts with the components on the Display.
	 * 
	 * Explanation of the states:
	 * 
	 * false = move
	 * true = click
	 * null = no action or reaction from the mouse.
	 */
	private Boolean action = null;

	public MouseAdapter(Display context)
	{
		this.context = context;
		
		/*
		 *  'context' is the variable to use
		 *  for calculating mouse movement and additional data.
		 *  The data related to the given Display can then be used in real-time.
		 *  To do so,
		 *  you can use the given methods below.
		 */
	}
	
	// Calculates the mouse speed with a delay of 10ms to have a difference.
	private double calcMouseSpeed()
	{
		cursorLast = getCursorLocation();
		
		Timer.pauseMillisecond(10);
		
		double distance = cursorLast.distance(getCursorLocation());
		
		return distance;
	}
	
	// Returns the general and current mouse speed on screen.
	// It is related to no component or such.
	public double getMouseSpeed()
	{
		return mouseSpeed;
	}
	
	// Tells when the mouse is active, so doing something,
	// like moving the cursor etc.
	private boolean isActive()
	{	
		return isClicking() || mouseSpeed > 0;
	}

	// Tells when the mouse is inactive, so doing nothing.
	public boolean isInactive()
	{		
		return !isActive();
	}

	@Override
	public void loop()
	{
		mouseSpeed = calcMouseSpeed();
		
		if(!isActive())
		{
			action = null;
		}
	}

	@Override
	public void mouseDragged(MouseEvent mouseEvent)
	{
		// The cursor was generally just dragged (still recognized as a click action) somewhere on the frame (true).
		action = true;

		// Update the current cursor location relative to the Viewport.
		// The boundaries of the Display (JFrame) are disregarded in this retrieved location.
		cursorLocation = mouseEvent.getPoint();
	}

	@Override
	public void mouseMoved(MouseEvent mouseEvent)
	{
		// The cursor was generally just moved somewhere on the frame (false).
		action = false;

		// Update the current cursor location relative to the Viewport.
		// The boundaries of the Display (JFrame) are disregarded in this retrieved location.
		cursorLocation = mouseEvent.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent mouseEvent)
	{
		// Not used.
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// A general click was performed somewhere on the frame (true).
		action = true;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		action = false;
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// When the cursor enter the area of the Display.
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// When the cursor exits the area of the Display.
	}
	
	// Returns the current action.
	public Boolean getAction()
	{
		return action;
	}
	
	// Tells whether the mouse cursor is moving.
	public boolean isMoving()
	{
		return getAction() != null && getAction() == false;
	}
	
	// Tells whether the mouse is being clicked.
	public Boolean isClicking()
	{
		return (getAction() != null) ? getAction() : false;
	}
	
	// Returns the absolute current cursor location.
	public Point getCursorLocation()
	{
		return new Point(cursorLocation);
	}
	
	// Tests if the cursor is on the position of a component.
	// Meaning: Tests whether the mouse cursor (relative to the Display) is inside the given component.
	// Returns 'false' if target is 'null'.
	public boolean isFocusing(Viewport source, GComponent target)
	{
		// If there is no component given,
		// this method assumes no component was found,
		// so the cursor is not over a component.
		if(target == null)
		{
			return false;
		}
		
		boolean isViewportAvailable = source != null;
		
		// Loads the viewports offset if a Viewport is actually given by the Display yet.
		Point viewportOffset = isViewportAvailable ? source.getOffset() : new Point();
		
		float scale = isViewportAvailable && target.getStyle().isScalableForViewport() ? source.getScale() : 1f;
		
		/*
		 *  This is just the relative component position in the JPanel (Viewport) which also regards the offset.
		 * 	Thus, this has a large effect on the component detection in the ComponentHander.
		 * 
		 * 	If you wouldn't regard / include the offset,
		 *  there would be a difference between the real components position and what is displayed graphically with a transition on screen.
		 */
		
		Point absoluteComponentLocation = target.getStyle().getPrimaryLook().getBounds().getLocation();
		
		Point relativeComponentLocation = new GIPoint(absoluteComponentLocation).add(viewportOffset).add(source.getOrigin()).toPoint();		
		
		return ShapeTransform.scalePolygon(ShapeTransform.movePolygonTo(target.getStyle().getPrimaryLook(), target.getStyle().isMovableForViewport() ? relativeComponentLocation : absoluteComponentLocation), scale).contains(getCursorLocation());
	}
	
	// Tests if the user is clicking a component.
	public boolean isClicking(Viewport source, GComponent component)
	{
		return isFocusing(source, component) && isClicking();
	}
	
	// Returns the first component which is focused by the cursor.
	// Makes the UI more efficient by breaking at the first component already.
	// Returns null if there is no such component.
	public GComponent getFocusedComponent(Viewport source)
	{
		GComponent firstMatch = null;
		
		if(source != null)
		{
			for(GLayer layer : source.getLayers())
			{
				for(GComponent selected : layer.getComponentBuffer())
				{
					boolean insideComponent = isFocusing(source, selected);
					
					// Returns the first component which is focused by the mouse cursor.
					if(insideComponent)
					{
						// Make sure, if the component is ignored / unfocusable it is not recognized by its click or hover behavior.
						if(selected.getLogic().isFocusable())
						{
							firstMatch = selected;
						}
						
						break;
					}
				}
			}
		}
		
		// Returns the first component which is focused by the mouse cursor.
		return firstMatch;
	}
	
	// Checks whether the cursor is over any GUInness component.
	// Should be avoided if used too often because of performance reasons.
	public boolean isFocusingAny(Viewport source, ArrayList<String> exceptionalTypes)
	{
		GComponent focused = getFocusedComponent(source);
		
		boolean assigned = focused != null;
		
		for(String type : exceptionalTypes)
		{
			if(assigned && (focused.getType().contentEquals(type)))
			{
				return false;
			}
		}
		
		return assigned;
	}
}
