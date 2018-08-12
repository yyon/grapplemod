package com.yyon.grapplinghook;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;

public class GuiModifier extends GuiScreen {
	private static final ResourceLocation texture = new ResourceLocation("grapplemod",
			"textures/gui/guimodifier_bg.png");

	int xSize = 221;
	int ySize = 221;

	protected int guiLeft;
	protected int guiTop;

	int posy;
	int id;
	HashMap<GuiButton, String> options;
	HashMap<GuiButton, String> tooltips;

	TileEntityGrappleModifier tileent;
	GrappleCustomization customization;

	grapplemod.upgradeCategories category = null;
	boolean allowed = false;

	public GuiModifier(TileEntityGrappleModifier tileent) {
		super();

		this.tileent = tileent;
		customization = tileent.customization;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		clearscreen();
		mainscreen();
	}

	public void mainscreen() {
		this.buttonList.add(new GuiButton(1, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10,
				50, 20, "Close"));
		this.buttonList.add(new GuiButton(2, this.guiLeft + this.xSize - 50 - 10, this.guiTop + this.ySize - 20 - 10,
				50, 20, "Reset"));

		for (int i = 0; i < grapplemod.upgradeCategories.size(); i++) {
			grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(i);
			this.buttonList.add(
					new GuiButton(99 + i, this.guiLeft + 10, this.guiTop + 5 + 22 * i, 100, 20, category.description));
		}
	}

	public void clearscreen() {
		this.buttonList.clear();
		this.category = null;
		this.allowed = false;
		posy = 10;
		id = 4;
		options = new HashMap<GuiButton, String>();
		tooltips = new HashMap<GuiButton, String>();
	}

	public void notAllowedScreen(grapplemod.upgradeCategories category) {
		this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, "Back"));
		this.category = category;
		this.allowed = false;
	}

	public void addCheckbox(String option, String text, String desc) {
		GuiCheckBox checkbox = new GuiCheckBox(id++, 10 + this.guiLeft, posy + this.guiTop, text, customization.getBoolean(option));
		posy += 20;
		this.buttonList.add(checkbox);
		options.put(checkbox, option);
		tooltips.put(checkbox, desc);
	}
	
	public void addSlider(String option, String text, String desc, double max) {
		double d = customization.getDouble(option);
		d = Math.floor(d * 100 + 0.5) / 100;
		GuiSlider slider = new GuiSlider(id++, 10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, text + ": ", "", 0, max, d, true, true);
		slider.precision = 2;
		posy += 25;
		this.buttonList.add(slider);
		options.put(slider, option);
		tooltips.put(slider, desc);
	}

	public void showCategoryScreen(grapplemod.upgradeCategories category) {
		this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, "Back"));
		this.category = category;
		this.allowed = true;

		if (category == grapplemod.upgradeCategories.ROPE) {
			addSlider("maxlen", "Rope Length", "The length of the rope", 200);
			addCheckbox("phaserope", "Phase Rope", "Allows rope to phase through blocks");
		} else if (category == grapplemod.upgradeCategories.THROW) {
			addSlider("hookgravity", "Gravity on hook", "Gravity on hook when thrown", 1);
			addSlider("throwspeed", "Throw Speed", "Speed of hook when thrown", 20);
			addCheckbox("reelin", "Crouch to Reel In", "Before the hook is attached, crouching will stop the hook from moving farther and slowly reel it in");
		} else if (category == grapplemod.upgradeCategories.MOTOR) {
			addCheckbox("motor", "Motor Enabled", "Pulls player towards hook");
			addSlider("motormaxspeed", "Motor Maximum Speed", "Maximum speed of motor", 10);
			addSlider("motoracceleration", "Motor Acceleration", "Acceleration of motor", 1);
			addCheckbox("motorwhencrouching", "Motor when crouching", "Motor is active when crouching");
			addCheckbox("motorwhennotcrouching", "Motor when not crouching", "Motor is active when crouching");
			addCheckbox("smartmotor", "Smart Motor", "Adjusts motor speed so that player moves towards crosshairs (up/down)");
			addCheckbox("motordampener", "Sideways Motion Dampener", "Reduces motion perpendicular to the rope so that the rope pulls straighter");
			addCheckbox("pullbackwards", "Pull Backwards", "Motor pulls even if you are facing the other way");
		} else if (category == grapplemod.upgradeCategories.SWING) {
			addSlider("playermovementmult", "Swing speed", "Acceleration of player when using movement keys while swinging", 5);
		} else if (category == grapplemod.upgradeCategories.STAFF) {
			addCheckbox("enderstaff", "Ender Staff", "Left click launches player forwards");
		} else if (category == grapplemod.upgradeCategories.FORCEFIELD) {
			addCheckbox("repel", "Forcefield Enabled", "Player is repelled from nearby blocks when swinging");
			addSlider("repelforce", "Repel Force", "Force nearby blocks exert on the player", 5);
		} else if (category == grapplemod.upgradeCategories.MAGNET) {
			addCheckbox("attract", "Magnet Enabled", "Hook is attracted to nearby blocks when thrown");
			addSlider("attractradius", "Attraction Radius", "Radius of attraction", 10);
		} else if (category == grapplemod.upgradeCategories.DOUBLE) {
			addCheckbox("doublehook", "Double Hook", "Two hooks are thrown at once");
			addCheckbox("smartdoublemotor", "Smart Motor", "Adjusts motor speed so that player moves towards crosshairs (left/right) when used with motor");
			addSlider("angle", "Angle", "Angle that each hook is thrown from center", 90);
			addSlider("sneakingangle", "Angle when crouching", "Angle that each hook is thrown from center when crouching (don't have 'crouch to reel in' enabled if you want to use this)", 90);
		}
	}
	
	GuiButton buttonpressed = null;
	
	@Override
	public void onGuiClosed() {
		this.updateOptions();
		this.tileent.setCustomizationClient(customization);
		
		super.onGuiClosed();
	}
	
	public void updateOptions() {
		for (GuiButton b : this.options.keySet()) {
			this.updateOption(b);
		}
	}
	
	public void updateOption(GuiButton b) {
		if (b instanceof GuiCheckBox) {
			boolean checked = ((GuiCheckBox) b).isChecked();
			String option = options.get(b);
			customization.setBoolean(option, checked);
		} else if (b instanceof GuiSlider) {
			double d = ((GuiSlider) b).getValue();
			d = Math.floor(d * 100 + 0.5) / 100;
			String option = options.get(b);
			customization.setDouble(option, d);
		}
	}
	
	@Override
    public void updateScreen() {
		if (buttonpressed != null) {
			GuiButton b = buttonpressed;
			buttonpressed = null;
			
			if (b.id == 1) {
				Minecraft.getMinecraft().player.closeScreen();
				return;
			} else if (b.id == 2) {
				this.customization = new GrappleCustomization();
			} else if (b.id == 3) {
				this.updateOptions();
				clearscreen();
				mainscreen();
			} else if (options.containsKey(b)) {
				this.updateOption(b);
			} else {
				int categoryid = b.id - 99;
				grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(categoryid);

				clearscreen();

				boolean unlocked = this.tileent.isUnlocked(category) || Minecraft.getMinecraft().player.capabilities.isCreativeMode;

				if (unlocked) {
					showCategoryScreen(category);
				} else {
					notAllowedScreen(category);
				}
			}
		}
		
		super.updateScreen();
    }

	@Override
	protected void actionPerformed(GuiButton b) {
		buttonpressed = b;
		
		try {
			super.actionPerformed(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (fontRenderer == null) {
			return;
		}
		
		// background
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		GlStateManager.translate(guiLeft, guiTop, 0.0F);

		if (this.category != null) {
			if (!this.allowed) {
				fontRenderer.drawString("Category not unlocked:", 10, 10, Color.darkGray.getRGB());
				fontRenderer.drawString(this.category.description, 10, 25, Color.darkGray.getRGB());
				fontRenderer.drawString("Please right click this block", 10, 40, Color.darkGray.getRGB());
				fontRenderer.drawString("with the item:", 10, 55, Color.darkGray.getRGB());
				fontRenderer.drawString(new ItemStack(this.category.getItem()).getDisplayName(), 10, 70,
						Color.darkGray.getRGB());
				fontRenderer.drawString("to unlock", 10, 85, Color.darkGray.getRGB());
			} else {

			}
		} else {
			fontRenderer.drawString("Right click with grappling hook to apply", 10, this.ySize - 20 - 10 - 10, Color.darkGray.getRGB());
		}
		
		GlStateManager.translate(-guiLeft, -guiTop, 0.0F);

		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();

		super.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableStandardItemLighting();
		
		for (GuiButton b : this.tooltips.keySet()) {
			if (mouseX >= b.x && mouseY >= b.y && mouseX <= b.x + b.width && mouseY <= b.y + b.height) {
				this.drawHoveringText(this.tooltips.get(b), mouseX, mouseY);
			}
		}
	}
}
