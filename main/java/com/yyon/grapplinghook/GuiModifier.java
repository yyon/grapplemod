package com.yyon.grapplinghook;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

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
	boolean showinghelpscreen = false;

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
				50, 20, grapplemod.proxy.localize("grapplemodifier.close.desc")));
		this.buttonList.add(new GuiButton(2, this.guiLeft + this.xSize - 50 - 10, this.guiTop + this.ySize - 20 - 10,
				50, 20, grapplemod.proxy.localize("grapplemodifier.reset.desc")));
		this.buttonList.add(new GuiButton(4, this.guiLeft + 10 + 75, this.guiTop + this.ySize - 20 - 10,
				50, 20, grapplemod.proxy.localize("grapplemodifier.helpbutton.desc")));

		int y = 0;
		int x = 0;
		for (int i = 0; i < grapplemod.upgradeCategories.size(); i++) {
			grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(i);
			if (category != grapplemod.upgradeCategories.LIMITS) {
				if (i == grapplemod.upgradeCategories.size()/2) {
					y = 0;
					x += 1;
				}
				this.buttonList.add(
						new GuiButton(99 + i, this.guiLeft + 10 + 105*x, this.guiTop + 15 + 30 * y, 95, 20, category.description));
				y += 1;
			}
		}
	}

	public void clearscreen() {
		this.buttonList.clear();
		this.category = null;
		this.allowed = false;
		posy = 10;
		id = 10;
		options = new HashMap<GuiButton, String>();
		tooltips = new HashMap<GuiButton, String>();
	}

	public void notAllowedScreen(grapplemod.upgradeCategories category) {
		this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.back.desc")));
		this.category = category;
		this.allowed = false;
	}

	public void helpscreen() {
		this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.back.desc")));
	}

	public void addCheckbox(String option) {
		String text = grapplemod.proxy.localize(this.customization.getName(option));
		String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
		GuiCheckBox checkbox = new GuiCheckBox(id++, 10 + this.guiLeft, posy + this.guiTop, text, customization.getBoolean(option));
		posy += 20;
		this.buttonList.add(checkbox);
		options.put(checkbox, option);
		tooltips.put(checkbox, desc);
	}
	
	public void addSlider(String option) {
		double d = customization.getDouble(option);
		d = Math.floor(d * 10 + 0.5) / 10;
		
		double max = customization.getMax(option, this.getLimits());
		double min = customization.getMin(option, this.getLimits());
		
		String text = grapplemod.proxy.localize(this.customization.getName(option));
		GuiSlider slider = new GuiSlider(id++, 10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, text + ": ", "", min, max, d, true, true);
		
		slider.displayString = text + ": " + Double.toString(d);
		slider.precision = 1;
		
		posy += 25;
		this.buttonList.add(slider);
		options.put(slider, option);
		
		String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
		tooltips.put(slider, desc);
	}

	public void showCategoryScreen(grapplemod.upgradeCategories category) {
		this.buttonList.add(new GuiButton(3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, grapplemod.proxy.localize("grapplemodifier.back.desc")));
		this.category = category;
		this.allowed = true;

		if (category == grapplemod.upgradeCategories.ROPE) {
			addSlider("maxlen");
			addCheckbox("phaserope");
			addCheckbox("sticky");
			addCheckbox("climbkey");
		} else if (category == grapplemod.upgradeCategories.THROW) {
			addSlider("hookgravity");
			addSlider("throwspeed");
			addCheckbox("reelin");
			addSlider("verticalthrowangle");
			addSlider("sneakingverticalthrowangle");
			addCheckbox("detachonkeyrelease");
		} else if (category == grapplemod.upgradeCategories.MOTOR) {
			addCheckbox("motor");
			addSlider("motormaxspeed");
			addSlider("motoracceleration");
			addCheckbox("motorwhencrouching");
			addCheckbox("motorwhennotcrouching");
			addCheckbox("smartmotor");
			addCheckbox("motordampener");
			addCheckbox("pullbackwards");
		} else if (category == grapplemod.upgradeCategories.SWING) {
			addSlider("playermovementmult");
		} else if (category == grapplemod.upgradeCategories.STAFF) {
			addCheckbox("enderstaff");
		} else if (category == grapplemod.upgradeCategories.FORCEFIELD) {
			addCheckbox("repel");
			addSlider("repelforce");
		} else if (category == grapplemod.upgradeCategories.MAGNET) {
			addCheckbox("attract");
			addSlider("attractradius");
		} else if (category == grapplemod.upgradeCategories.DOUBLE) {
			addCheckbox("doublehook");
			addCheckbox("smartdoublemotor");
			addSlider("angle");
			addSlider("sneakingangle");
			addCheckbox("oneropepull");
		} else if (category == grapplemod.upgradeCategories.ROCKET) {
			addCheckbox("rocket");
			addSlider("rocket_force");
			addSlider("rocket_active_time");
			addSlider("rocket_refuel_ratio");
			addSlider("rocket_vertical_angle");
		}
		
		this.updateEnabled();
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
			d = Math.floor(d * 10 + 0.5) / 10;
			String option = options.get(b);
			customization.setDouble(option, d);
		}
		this.updateEnabled();
	}
	
	public void updateEnabled() {
		for (GuiButton b : this.options.keySet()) {
			String option = this.options.get(b);
			boolean enabled = true;
			
			String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
			
			if (this.customization.isoptionvalid(option)) {
			} else {
				desc = grapplemod.proxy.localize("grapplemodifier.incompatability.desc") + "\n" + desc;
				enabled = false;
			}
			
			int level = this.customization.optionEnabled(option);
			if (this.getLimits() < level) {
				if (level == 1) {
					desc = grapplemod.proxy.localize("grapplemodifier.limits.desc") + "\n" + desc;
				} else {
					desc = grapplemod.proxy.localize("grapplemodifier.locked.desc") + "\n" + desc;
				}
				enabled = false;
			}
			
			b.enabled = enabled;

			tooltips.put(b, desc);
		}
	}
	
	public int getLimits() {
		if (this.tileent.isUnlocked(grapplemod.upgradeCategories.LIMITS) || Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
			return 1;
		}
		return 0;
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
				showinghelpscreen = false;
				this.updateOptions();
				clearscreen();
				mainscreen();
			} else if (b.id == 4) {
				showinghelpscreen = true;
				clearscreen();
				helpscreen();
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
				fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock1.desc"), 10, 10, Color.darkGray.getRGB());
				fontRenderer.drawString(this.category.description, 10, 25, Color.darkGray.getRGB());
				fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock2.desc"), 10, 40, Color.darkGray.getRGB());
				fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock3.desc"), 10, 55, Color.darkGray.getRGB());
				fontRenderer.drawString(new ItemStack(this.category.getItem()).getDisplayName(), 10, 70,
						Color.darkGray.getRGB());
				fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.unlock4.desc"), 10, 85, Color.darkGray.getRGB());
			} else {

			}
		} else {
			if (showinghelpscreen) {
				String helptext =  grapplemod.proxy.localize("grapplemodifier.help.desc");
				int linenum = 0;
				for (String line : helptext.split(Pattern.quote("\\n"))) {
					fontRenderer.drawString(line, 10, 10 + 15 * linenum, Color.darkGray.getRGB());
					linenum++;
				}
			} else {
				fontRenderer.drawString(grapplemod.proxy.localize("grapplemodifier.apply.desc"), 10, this.ySize - 20 - 10 - 10, Color.darkGray.getRGB());
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
		
		for (GuiButton b : this.tooltips.keySet()) {
			if (mouseX >= b.x && mouseY >= b.y && mouseX <= b.x + b.width && mouseY <= b.y + b.height) {
				this.drawHoveringText(this.tooltips.get(b), mouseX, mouseY);
			}
		}
	}
}
