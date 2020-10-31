package org.thirdreality.guinness.gui.design.classic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.thirdreality.guinness.draw.DrawToolkit;
import org.thirdreality.guinness.feature.GIDimension;
import org.thirdreality.guinness.feature.GIPoint;
import org.thirdreality.guinness.feature.shape.ShapeMaker;
import org.thirdreality.guinness.feature.shape.ShapeTransform;
import org.thirdreality.guinness.gui.component.GComponent;
import org.thirdreality.guinness.gui.component.decoration.GPath;
import org.thirdreality.guinness.gui.component.decoration.GRectangle;
import org.thirdreality.guinness.gui.component.placeholder.GWindow;
import org.thirdreality.guinness.gui.component.selection.GCheckbox;
import org.thirdreality.guinness.gui.component.selection.list.GSelectionBox;
import org.thirdreality.guinness.gui.component.selection.list.GSelectionOption;
import org.thirdreality.guinness.gui.design.Design;
import org.thirdreality.guinness.gui.font.Font;

public class DisplayDrawAdapter
{
	private Point offset;

	private float scale;

	private Design design;

	public DisplayDrawAdapter(Design design)
	{
		this.design = design;
	}

	// Every design has its own draw method in order to know how to draw each component.
	// This is a "pre-method".
	public void drawContext(Graphics g, GComponent c, Point origin, Point offset, float scale)
	{
		this.offset = new GIPoint(offset).add(origin).toPoint();
		this.scale = scale;

		// For the case there is an image supplied to the GComponent object,
		// it is considered to be rendered.
		// The programmer needs to know how to use the features GComponent delivers and has to ensure
		// a supplied image will not get in conflict with other settings.
		switch(c.getType())
		{
			case "polybutton":
			{
				drawPolyButton(g, c);

				break;
			}

			case "description":
			{
				drawDescription(g, c);
		
				break;
			}

			case "image":
			{				
				drawImage(g, c);

				break;
			}

			case "path":
			{
				drawPath(g, c);

				break;
			}

			case "textfield":
			{
					
				drawTextfield(g, c);

				break;
			}

			case "checkbox":
			{
				drawCheckbox(g, c);

				break;
			}

			case "selectionbox":
			{
				drawSelectionBox(g, c);

				break;
			}

			case "rectangle":
			{
				drawRectangle(g, c);

				break;
			}

			case "button":
			{
				Color temp = getDesign().getDesignColor().getBorderColor();

				getDesign().getDesignColor().setBorderColor(c.getStyle().getPrimaryColor().darker().darker());

				drawGeneralField(g, c);

				getDesign().getDesignColor().setBorderColor(temp);

				break;
			}

			case "window":
			{
				drawWindow(g, c);
			}

			default:
			{
				// drawGeneralField(g, c, true);
			}
		}
	}
	
	private void drawRectangle(Graphics g, GComponent c)
	{
		// A GRectangle can do more than a usual GComponent.
		// You can define border-radiuses and more.
		if(c.getType().contentEquals("rectangle"))
		{
			GRectangle rect = (GRectangle) c;

			// Polygon rectangle = ShapeMaker.createRectangle(rect.getStyle().getLook().getBounds().getLocation(), rect.getStyle().getLook().getBounds().getSize());
			Polygon rectangle = ShapeMaker.createRectangleFrom(rect.getStyle().getPrimaryLook().getBounds(), rect.getStyle().getBorderProperties());

			g.setColor(rect.getStyle().getPrimaryColor() == null ? Color.BLACK : rect.getStyle().getPrimaryColor());

			// Uses the correct scale depending on whether Viewport scaling is generally wanted by the component.
			float scale = c.getStyle().isScalableForViewport() ? this.scale : 1f;
			
			int x = (int) ((c.getStyle().isMovableForViewport() ? rectangle.getBounds().x + getOffset().x : rectangle.getBounds().x) * scale);
			int y = (int) ((c.getStyle().isMovableForViewport() ? rectangle.getBounds().y + getOffset().y : rectangle.getBounds().y) * scale);

			if(rectangle.getBounds() != null)
			{
				g.fillPolygon(ShapeTransform.movePolygonTo(ShapeTransform.scalePolygon(rectangle, scale), x, y));
			}
		}
		// If it's not a GRectangle just draw the shape if there is one. Anyway, you can do less things here..
		else if(c.getStyle().getPrimaryLook() != null)
		{
			Rectangle shape = c.getStyle().getPrimaryLook().getBounds();
			
			g.setColor(c.getStyle().getPrimaryColor() == null ? Color.BLACK : c.getStyle().getPrimaryColor());
			
			// Uses the correct scale depending on whether Viewport scaling is generally wanted by the component.
			float scale = c.getStyle().isScalableForViewport() ? this.scale : 1f;
			
			int x = (int) ((c.getStyle().isMovableForViewport() ? shape.x + getOffset().x : shape.x) * scale);
			int y = (int) ((c.getStyle().isMovableForViewport() ? shape.y + getOffset().y : shape.y) * scale);
			
			if(shape != null)
			{
				g.fillRect(x, y, (int) (shape.width * scale), (int) (shape.height * scale));
			}
		}
	}

	private void drawDescription(Graphics g, GComponent c)
	{
		// Represents simply the outer bounds of the component.
		Rectangle bounds = c.getStyle().getPrimaryLook().getBounds();
		
		Point descLoc = new GIPoint(bounds.getLocation()).add(getDesign().getPaddingProperty().getInnerThickness()).add(getDesign().getBorderProperty().getBorderThicknessPx()).add(getOffset(), c.getStyle().isMovableForViewport()).mul(getScale(), c.getStyle().isScalableForViewport()).toPoint();
		
		Font original = c.getStyle().getFont();
		Font scaledFont = new Font(original.getName(), original.getFile().getAbsolutePath(), (int) (original.getFontSize() * scale));
		
		DrawToolkit.drawString(g, c.getValue(), descLoc, scaledFont);
	}

	private void drawImage(Graphics g, GComponent c)
	{
		// Represents simply the outer bounds of the component.
		Rectangle bounds = c.getStyle().getPrimaryLook().getBounds();
		
		// Uses the correct scale depending on whether Viewport scaling is generally wanted by the component.
		float scale = c.getStyle().isScalableForViewport() ? this.scale : 1f;
		
		int x = (int) ((bounds.getLocation().x + (c.getStyle().isMovableForViewport() ? getOffset().x : 0)) * scale);
		int y = (int) ((bounds.getLocation().y + (c.getStyle().isMovableForViewport() ? getOffset().y : 0)) * scale);
		
		g.drawImage(c.getStyle().getImage(), x, y, (int) (bounds.width * scale), (int) (bounds.height * scale), null);
	}
	
	// Needs to be updated with offset and scale ability from the Viewports settings.
	@Deprecated
	private void drawPath(Graphics g, GComponent c)
	{
		GPath path = (GPath) c;

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(path.getStyle().getPrimaryColor());

		if(path.isFill())
		{
			g2d.fill(path.getPath());
		}
		else
		{
			g2d.draw(path.getPath());
		}
	}
	
	private void drawTextfield(Graphics g, GComponent c)
	{
		drawGeneralField(g, c);
	}
	
	private void drawCheckbox(Graphics g, GComponent c)
	{
		GCheckbox checkbox = (GCheckbox) c;
		
		boolean isScalable = c.getStyle().isScalableForViewport();
		boolean isMovable = c.getStyle().isMovableForViewport();
		
		// Represents simply the outer bounds of the component.
		Rectangle bounds = c.getStyle().getPrimaryLook().getBounds();
		
		// It wouldn't matter if you use 'height' or 'width' because both values are the same.
		Dimension outerSize = new Dimension(bounds.width, bounds.width);
		Dimension innerSize = new Dimension(outerSize.width - getDesign().getPaddingProperty().getInnerThickness(), outerSize.width - getDesign().getPaddingProperty().getInnerThickness());
		
		Point locOuter = new GIPoint(bounds.getLocation()).add(getOffset(), isMovable).toPoint();
		Point locInner = new GIPoint(locOuter).add(getDesign().getBorderProperty().getBorderThicknessPx()).toPoint();
		
		
		
		Point locOuterScaled = new GIPoint(locOuter).mul(getScale(), isScalable).toPoint();
		Point locInnerScaled = new GIPoint(locInner).mul(getScale(), isScalable).toPoint();

		Dimension outerSizeScaled = new GIDimension(outerSize).mul(getScale(), isScalable);
		Dimension innerSizeScaled = new GIDimension(innerSize).mul(getScale(), isScalable);

		g.setColor(getDesign().getDesignColor().getBorderColor());

		g.fillRect(locOuterScaled.x, locOuterScaled.y, outerSizeScaled.width, outerSizeScaled.width);

		g.setColor(Color.WHITE);

		g.fillRect(locInnerScaled.x, locInnerScaled.y, innerSizeScaled.width, innerSizeScaled.width);
		
		if(checkbox.isChecked())
		{
			Image checkSymbol = c.getStyle().getImage();

			// Simply the square size of the image.
			// The image is saved with square dimensions,
			// so it doesn't matter if you take the width or height (see package core.gui.image.icon for "check_sign.png").
			int sizePx = (int) (checkSymbol.getWidth(null));
			
			if(c.getStyle().isScalableForViewport())
			{
				sizePx *= getScale();
			}
			
			Point imgLoc = new GIPoint(locInner).add(getDesign().getBorderProperty().getBorderThicknessPx()).mul(getScale(), isScalable).toPoint();
			
			g.drawImage(checkSymbol, imgLoc.x, imgLoc.y, sizePx, sizePx, null);
		}
	}
	
	private void drawSelectionBox(Graphics g, GComponent c)
	{
		boolean isScalable = c.getStyle().isScalableForViewport();
		boolean isMovable = c.getStyle().isMovableForViewport();
		
		GSelectionBox selectionBox = (GSelectionBox) c;

		drawRectangle(g, selectionBox);
		
		ArrayList<Polygon[]> shapeTable = selectionBox.getShapeTable();

		// Draws every single option from the GSelectionBox.
		for(int i = 0; i < shapeTable.size(); i++)
		{
			GSelectionOption option = selectionBox.getOptions().get(i);
			
			Polygon optionShape = new Polygon(shapeTable.get(i)[0].xpoints, shapeTable.get(i)[0].ypoints, shapeTable.get(i)[0].npoints);
			Polygon titleShape = new Polygon(shapeTable.get(i)[2].xpoints, shapeTable.get(i)[2].ypoints, shapeTable.get(i)[2].npoints);
			
			if(isMovable)
			{
				optionShape.translate(getOffset().x, getOffset().y);
				titleShape.translate(getOffset().x, getOffset().y);
			}
			
			if(isScalable)
			{
				optionShape = ShapeTransform.scalePolygon(optionShape, getScale());
				titleShape = ShapeTransform.scalePolygon(titleShape, getScale());
			}

			if(option.isChecked())
			{
				g.drawImage(selectionBox.getIcons()[1], optionShape.getBounds().x, optionShape.getBounds().y, optionShape.getBounds().width, optionShape.getBounds().height, null);
			}
			else
			{
				g.drawImage(selectionBox.getIcons()[0], optionShape.getBounds().x, optionShape.getBounds().y, optionShape.getBounds().width, optionShape.getBounds().height, null);
			}
			
			// Every option can have a background color..
			Color optionColor = option.getStyle().getPrimaryColor();

			int titleShapeWidth = titleShape.getBounds().width;
			int titleShapeHeight = titleShape.getBounds().height;
			
			// But if there is no background color, then no background will just be drawn..
			if(optionColor != null)
			{
				g.setColor(optionColor);
				g.fillRect(titleShape.getBounds().x, titleShape.getBounds().y, titleShapeWidth, titleShapeHeight);
			}
			
			Font original = c.getStyle().getFont();
			Font scaledFont = new Font(original.getName(), original.getFile().getAbsolutePath(), (int) (original.getFontSize() * scale));
			
			DrawToolkit.drawString(g, option.getValue(), titleShape.getBounds().getLocation(), scaledFont);
		}
	}
	
	protected void drawPolyButton(Graphics g, GComponent c)
	{
		// Represents simply the outer bounds of the component.
		Rectangle bounds = c.getStyle().getPrimaryLook().getBounds();

		Polygon look = c.getStyle().getPrimaryLook();

		g.setColor(c.getStyle().getPrimaryColor());

		int xButton = (int) (look.getBounds().x + (c.getStyle().isMovableForViewport() ? getOffset().x : 0));
		int yButton = (int) (look.getBounds().y + (c.getStyle().isMovableForViewport() ? getOffset().y : 0));

		// Here it is only working with a copy in order not to modify the original object (polygon and Polybutton).
		Polygon transformedCopy = ShapeTransform.scalePolygon(ShapeTransform.movePolygonTo(look, xButton, yButton), scale);
		g.fillPolygon(transformedCopy);

		// If text should be displayed in the center of the component.
		if(c.getStyle().getTextAlign() == 1)
		{
			int textLength = c.getStyle().getFont().getFontSize() * c.getValue().length();

			int centerX = bounds.getLocation().x + bounds.width / 2 - textLength / 2;
			int centerY = bounds.getLocation().y + bounds.height / 2 - c.getStyle().getFont().getFontSize() / 2;

			Point loc = new GIPoint(centerX, centerY).add(c.getStyle().getTextTransition()).add(getOffset(), c.getStyle().isMovableForViewport()).mul(getScale(), c.getStyle().isScalableForViewport()).toPoint();
			
			Font original = c.getStyle().getFont();
			Font scaledFont = new Font(original.getName(), original.getFile().getAbsolutePath(), (int) (original.getFontSize() * scale));
			
			DrawToolkit.drawString(g, c.getValue(), loc, scaledFont);
		}
		else // If text should be displayed normally (upper-left corner of the component).
		{
			Point loc = new GIPoint(bounds.getLocation()).add(c.getStyle().getTextTransition()).add(getOffset(), c.getStyle().isMovableForViewport()).mul(getScale(), c.getStyle().isScalableForViewport()).toPoint();
			
			DrawToolkit.drawString(g, c.getValue(), loc, c.getStyle().getFont());
		}
	}

	protected void drawGeneralField(Graphics g, GComponent c)
	{		
		Polygon background = c.getStyle().getPrimaryLook();

		Point backgroundLoc = new GIPoint(background.getBounds().getLocation()).add(getOffset(), c.getStyle().isMovableForViewport()).toPoint();

		background = ShapeTransform.movePolygonTo(background, backgroundLoc);
		background = ShapeTransform.scalePolygon(background, c.getStyle().isScalableForViewport() ? getScale() : 1f);

		g.setColor(getDesign().getDesignColor().getBorderColor());
		g.fillPolygon(background);

		

		Dimension frontDimension = new GIDimension(c.getStyle().getLength() * c.getStyle().getFont().getFontSize(), c.getStyle().getFont().getFontSize()).add(2*getDesign().getPaddingProperty().getInnerThickness());

		Rectangle frontRectangle = new Rectangle(new GIPoint(backgroundLoc).add(getDesign().getBorderProperty().getBorderThicknessPx()).toPoint(), frontDimension);

		Polygon front = ShapeMaker.createRectangleFrom(frontRectangle, c.getStyle().getBorderProperties());

		front = ShapeTransform.scalePolygon(front, c.getStyle().isScalableForViewport() ? getScale() : 1f);

		g.setColor(c.getStyle().getPrimaryColor());
		g.fillPolygon(front);

		

		Point text = new GIPoint(backgroundLoc).add(getDesign().getBorderProperty().getBorderThicknessPx()).add(getDesign().getPaddingProperty().getInnerThickness()).mul(getScale(), c.getStyle().isScalableForViewport()).toPoint();

		DrawToolkit.drawString(g, c.getValue(), text, c.getStyle().getFont().getScaledFont(c.getStyle().isScalableForViewport() ? getScale() : 1f));
	}
	
	public void drawWindow(Graphics g, GComponent c)
	{
		GWindow window = (GWindow) c;

		/*
		 * The GWindow currently only supports offsets yet delivered by the corresponding Viewport.
		 */
		
		// Draws the outer part of the window, including offset and scale by Viewport of course.
		{
			g.setColor(window.getFrameColor());

			Point windowOuterMoved = new GIPoint(c.getStyle().getPrimaryLook().getBounds().getLocation()).add(getOffset(), window.getStyle().isMovableForViewport()).toPoint();

			Polygon movedByOffset = ShapeTransform.movePolygonTo(c.getStyle().getPrimaryLook(), windowOuterMoved);

			/*
			 * When scaling is wanted again, you can implement it here again with this code snippet..
			 * 
			 * Polygon scaledByViewport = ShapeTransform.scalePolygon(movedByOffset, window.getStyle().isScalableForViewport() ? getScale() : 1f);
			 * 
			 * g.fillPolygon(scaledByViewport);
			 */

			g.fillPolygon(movedByOffset);
		}

		// Draws the inner part of the window, including offset and scale by Viewport of course.
		{
			g.setColor(Color.BLACK);
			
			Point secondaryLookMoved = new GIPoint(c.getStyle().getSecondaryLook().getBounds().getLocation()).add(getOffset(), window.getStyle().isMovableForViewport()).toPoint();
			
			Polygon movedByOffset = ShapeTransform.movePolygonTo(c.getStyle().getSecondaryLook(), secondaryLookMoved);
			
			g.fillPolygon(movedByOffset);
		}

		// Draws the window title
		{
			int borderTopMargin = window.getStyle().getBorderProperties().getBorderThicknessPx() + window.getTitleAreaHeightPx() / 2 - window.getStyle().getFont().getFontSize() / 2;
			
			int borderLeftMargin = window.getStyle().getBorderProperties().getBorderThicknessPx();
			
			GIPoint titlePosition = new GIPoint(c.getStyle().getPrimaryLook().getBounds().getLocation()).add(getOffset(), window.getStyle().isMovableForViewport());		
			
			titlePosition = titlePosition.addY(borderTopMargin).addX(borderLeftMargin);
			
			// titlePosition.mul(getScale(), window.getStyle().isScalableForViewport());
			
			DrawToolkit.drawString(g, window.getTitle(), titlePosition.toPoint(), window.getStyle().getFont());// window.getStyle().isScalableForViewport() ? window.getStyle().getFont().getScaledFont(getScale()) : window.getStyle().getFont());
		}

		{
			Point exitButtonMoved_Loc = new GIPoint(window.getExitButton().getStyle().getLocation()).add(getOffset(), window.getStyle().isMovableForViewport()).toPoint();
			
			Polygon exitButtonMoved = ShapeTransform.movePolygonTo(window.getExitButton().getStyle().getPrimaryLook(), exitButtonMoved_Loc);
			
			// Polygon exitButtonScaled = ShapeTransform.scalePolygon(exitButtonMoved, window.getStyle().isScalableForViewport() ? getScale() : 1f);
			
			g.setColor(window.getExitButton().getStyle().getPrimaryColor());
			g.fillPolygon(exitButtonMoved);
		}
		
		{	
			Point minimizeButtonMoved_Loc = new GIPoint(window.getMinimizeButton().getStyle().getLocation()).add(getOffset(), window.getStyle().isMovableForViewport()).toPoint();
			
			Polygon minimizeButtonMoved = ShapeTransform.movePolygonTo(window.getMinimizeButton().getStyle().getPrimaryLook(), minimizeButtonMoved_Loc);
			
			// Polygon minimizeButtonScaled = ShapeTransform.scalePolygon(minimizeButtonMoved, window.getStyle().isScalableForViewport() ? getScale() : 1f);
			
			g.setColor(window.getMinimizeButton().getStyle().getPrimaryColor());
			g.fillPolygon(minimizeButtonMoved);
		}
	}

	public Design getDesign()
	{
		return design;
	}
	
	public void setDesign(Design design)
	{
		this.design = design;
	}

	public Point getOffset()
	{
		return offset;
	}

	public float getScale()
	{
		return scale;
	}
}