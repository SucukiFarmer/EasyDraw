package org.thirdreality.guinness.gui.component.placeholder.window;

import java.awt.Color;
import java.awt.Rectangle;

import org.thirdreality.guinness.Meta;
import org.thirdreality.guinness.feature.GIPoint;
import org.thirdreality.guinness.feature.shape.ShapeMaker;
import org.thirdreality.guinness.gui.component.GComponent;
import org.thirdreality.guinness.gui.component.decoration.GImage;
import org.thirdreality.guinness.gui.component.optional.GValueManager;
import org.thirdreality.guinness.gui.component.style.property.GBorderProperty;

public class GWindowButton extends GComponent
{
	private static final long serialVersionUID = Meta.serialVersionUID;

	private GImage icon;
	
	private Color defaultColor, clickColor, hoverColor;

	public GWindowButton(Rectangle rect, Color background, float opacity, GBorderProperty borderProperties, GImage icon)
	{
		super("window_button");

		getStyle().setBorderProperties(borderProperties);

		getStyle().setPrimaryLook(ShapeMaker.createRectangleFrom(new Rectangle(rect.getSize()), getStyle().getBorderProperties()));
		getStyle().setLocation(new GIPoint(rect.getLocation()).toPoint());

		getStyle().setPrimaryColor(background);
		getStyle().setOpacity(opacity);

		clickColor = getStyle().getPrimaryColor().darker().darker();
		defaultColor = getStyle().getPrimaryColor().darker();
		hoverColor = getStyle().getPrimaryColor();

		getStyle().setPrimaryColor(defaultColor);

		this.icon = icon;
	}
	
	public GImage getIcon()
	{
		return icon;
	}
	
	public Color getDefaultColor()
	{
		return defaultColor;
	}
	
	public Color getClickColor()
	{
		return clickColor;
	}

	public Color getHoverColor()
	{
		return hoverColor;
	}
}
