package com.yyon.grapplinghook;

import java.awt.Color;
import java.lang.reflect.Field;
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

	int xSize;
	int ySize;

	protected int guiLeft;
	protected int guiTop;

	int posy;
	int id;
	HashMap<GuiButton, String> options;

	TileEntityGrappleModifier tileent;
	GrappleCustomization customization;

	grapplemod.upgradeCategories category = null;
	boolean allowed = false;

	public GuiModifier(TileEntityGrappleModifier tileent) {
		super();
		xSize = 176;
		ySize = 221;

		this.tileent = tileent;
		customization = tileent.customization.copy();
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
	}
	
	public void addSlider(String option, String text, String desc, double max) {
		GuiSlider slider = new GuiSlider(id++, 10 + this.guiLeft, posy + this.guiTop, 150, 20, text + ": ", "", 0, max, customization.getDouble(option), true, true);
		posy += 25;
		this.buttonList.add(slider);
		options.put(slider, option);
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
		} else if (category == grapplemod.upgradeCategories.MOTOR) {
			addCheckbox("motor", "Motor Enabled", "Pulls player towards hook");
			addSlider("motormaxspeed", "Motor Maximum Speed", "Maximum speed of motor", 10);
			addSlider("motoracceleration", "Motor Acceleration", "Acceleration of motor", 1);
			addCheckbox("motorwhencrouching", "Motor when crouching", "Motor is active when crouching");
			addCheckbox("motorwhennotcrouching", "Motor when not crouching", "Motor is active when crouching");
			addCheckbox("smartmotor", "Smart Motor", "Adjusts motor speed so that player moves towards crosshairs (up/down)");
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
			addSlider("sneakingangle", "Angle when crouching", "Angle that each hook is thrown from center when crouching", 90);
		}
	}
	
	GuiButton buttonpressed = null;
	
	public void onGuiClosed() {
		this.tileent.setCustomization(customization);
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
				clearscreen();
				mainscreen();
			} else if (options.containsKey(b)) {
				if (b instanceof GuiCheckBox) {
					boolean checked = ((GuiCheckBox) b).isChecked();
					String option = options.get(b);
					customization.setBoolean(option, checked);
				} else if (b instanceof GuiSlider) {
					double d = ((GuiSlider) b).getValue();
					String option = options.get(b);
					customization.setDouble(option, d);
				}
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
    }

	@Override
	protected void actionPerformed(GuiButton b) {
		buttonpressed = b;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
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
	}
}
