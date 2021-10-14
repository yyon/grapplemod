package com.yyon.grapplinghook;

import java.util.HashMap;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GuiModifier extends Screen {
	private static final ResourceLocation texture = new ResourceLocation("grapplemod",
			"textures/gui/guimodifier_bg.png");

	int xSize = 221;
	int ySize = 221;

	protected int guiLeft;
	protected int guiTop;

	int posy;
	int id;
	HashMap<Widget, String> options;

	TileEntityGrappleModifier tileent;
	GrappleCustomization customization;

	grapplemod.upgradeCategories category = null;
	boolean allowed = false;
	boolean showinghelpscreen = false;

	public GuiModifier(TileEntityGrappleModifier tileent) {
		super(new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.title.desc")));

		this.tileent = tileent;
		customization = tileent.customization;
	}

	@Override
	public void init(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
		super.init(p_231158_1_, p_231158_2_, p_231158_3_);
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		clearscreen();
		mainscreen();
	}
	
	class PressCategory implements IPressable {
		grapplemod.upgradeCategories category;
		public PressCategory(grapplemod.upgradeCategories category) {
			this.category = category;
		}
		
		public void onPress(Button p_onPress_1_) {
			clearscreen();

			boolean unlocked = tileent.isUnlocked(category) || Minecraft.getInstance().player.isCreative();

			if (unlocked) {
				showCategoryScreen(category);
			} else {
				notAllowedScreen(category);
			}
		}
	}
	
	class PressBack implements IPressable {
		public void onPress(Button p_onPress_1_) {
			showinghelpscreen = false;
			clearscreen();
			mainscreen();
		}
	}
	public void mainscreen() {
		this.addButton(new Button(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10,
			50, 20, new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.close.desc")), new IPressable() {
				public void onPress(Button p_onPress_1_) {
					onClose();
				}
			}));
		this.addButton(new Button(this.guiLeft + this.xSize - 50 - 10, this.guiTop + this.ySize - 20 - 10,
			50, 20, new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.reset.desc")), new IPressable() {
				public void onPress(Button p_onPress_1_) {
					customization = new GrappleCustomization();
					showinghelpscreen = false;
					clearscreen();
					mainscreen();
				}
			}));
		this.addButton(new Button(this.guiLeft + 10 + 75, this.guiTop + this.ySize - 20 - 10,
			50, 20, new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.helpbutton.desc")), new IPressable() {
				public void onPress(Button p_onPress_1_) {
					showinghelpscreen = true;
					clearscreen();
					helpscreen();
				}
			}));

		int y = 0;
		int x = 0;
		for (int i = 0; i < grapplemod.upgradeCategories.size(); i++) {
			grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(i);
			if (category != grapplemod.upgradeCategories.LIMITS) {
				if (i == grapplemod.upgradeCategories.size()/2) {
					y = 0;
					x += 1;
				}
				this.addButton(
						new Button(this.guiLeft + 10 + 105*x, this.guiTop + 15 + 30 * y, 95, 20, new StringTextComponent(category.description), new PressCategory(category)));
				y += 1;
			}
		}

		this.addButton(new TextWidget(new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.apply.desc")), this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10 - 10));
	}

	class BackgroundWidget extends Widget {
		public BackgroundWidget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_,
				ITextComponent p_i232254_5_) {
			super(p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, p_i232254_5_);
			this.active = false;
		}
		
		public BackgroundWidget(int x, int y, int w, int h) {
			this(x, y, w, h, new StringTextComponent(""));
		}
		
	   public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
		Minecraft.getInstance().getTextureManager().bind(texture);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.blit(p_230431_1_, this.x, this.y, 0, 0, this.width, this.height);
	   }
	}

	public void clearscreen() {
		this.buttons.clear();
		this.category = null;
		this.allowed = false;
		posy = 10;
		id = 10;
		options = new HashMap<>();
		this.children.clear();
		
		this.addButton(new BackgroundWidget(this.guiLeft, this.guiTop, this.xSize, this.ySize));
	}
	
	class TextWidget extends Widget {
		public TextWidget(int p_i232254_1_, int p_i232254_2_, int p_i232254_3_, int p_i232254_4_,
				ITextComponent p_i232254_5_) {
			super(p_i232254_1_, p_i232254_2_, p_i232254_3_, p_i232254_4_, p_i232254_5_);
		}
		
		public TextWidget(ITextComponent text, int x, int y) {
			this(x, y, 50, 15 * text.getString().split("\n").length + 5, text);
		}
		
	   public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
	      Minecraft minecraft = Minecraft.getInstance();
	      FontRenderer fontrenderer = minecraft.font;
	      minecraft.getTextureManager().bind(WIDGETS_LOCATION);
	      RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
	      int i = this.getYImage(this.isHovered());
	      RenderSystem.enableBlend();
	      RenderSystem.defaultBlendFunc();
	      RenderSystem.enableDepthTest();
	      int j = this.getFGColor();
	      int lineno = 0;
	      for (String s : this.getMessage().getString().split("\n")) {
		      drawString(p_230431_1_, fontrenderer, new StringTextComponent(s), this.x, this.y + lineno*15, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
	    	  lineno++;
	      }
	   }
	}

	public void notAllowedScreen(grapplemod.upgradeCategories category) {
		this.addButton(new Button(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.back.desc")), new PressBack()));
		this.category = category;
		this.allowed = false;
		this.addButton(new TextWidget(new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.unlock1.desc")), this.guiLeft + 10, this.guiTop + 10));
		this.addButton(new TextWidget(new StringTextComponent(this.category.description), 10, 25));
		this.addButton(new TextWidget(new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.unlock2.desc")), this.guiLeft + 10, this.guiTop + 40));
		this.addButton(new TextWidget(new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.unlock3.desc")), this.guiLeft + 10, this.guiTop + 55));
		this.addButton(new TextWidget(new ItemStack(this.category.getItem()).getDisplayName(), this.guiLeft + 10, this.guiTop + 70));
		this.addButton(new TextWidget(new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.unlock4.desc")), this.guiLeft + 10, this.guiTop + 85));
	}

	public void helpscreen() {
		this.addButton(new Button(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.back.desc")), new PressBack()));

		this.addButton(new TextWidget(new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.help.desc")), this.guiLeft + 10, this.guiTop + 10));
		
	}
	
	class GuiCheckbox extends CheckboxButton {
		String option;
		public ITextComponent tooltip;

		public GuiCheckbox(int x, int y, int w, int h,
				ITextComponent text, boolean val, String option, ITextComponent tooltip) {
			super(x, y, w, h, text, val);
			this.option = option;
			this.tooltip = tooltip;
		}
		
		@Override
		public void onPress() {
			super.onPress();
			
			customization.setBoolean(option, this.selected());
			
			updateEnabled();
		}
		
		@Override
		public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
			super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
			
			if (this.isHovered()) {
				renderTooltip(p_230431_1_, tooltip, p_230431_2_, p_230431_3_);
			}
		}
	}

	public void addCheckbox(String option) {
		String text = grapplemod.proxy.localize(this.customization.getName(option));
		String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
		GuiCheckbox checkbox = new GuiCheckbox(10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, new StringTextComponent(text), customization.getBoolean(option), option, new StringTextComponent(desc));
		posy += 20;
		this.addButton(checkbox);
		options.put(checkbox, option);
	}
	
	class GuiSlider extends AbstractSlider {
		double min, max, val;
		String text, option;
		public ITextComponent tooltip;
		public GuiSlider(int x, int y, int w, int h,
				ITextComponent text, double min, double max, double val, String option, ITextComponent tooltip) {
			super(x, y, w, h, text, (val - min) / (max - min));
			this.min = min;
			this.max = max;
			this.val = val;
			this.text = text.getString();
			this.option = option;
			this.tooltip = tooltip;
			
			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(new StringTextComponent(text + ": " + String.format("%.1f", this.val)));
		}

		@Override
		protected void applyValue() {
			this.val = (this.value * (this.max - this.min)) + this.min;
//			d = Math.floor(d * 10 + 0.5) / 10;
			customization.setDouble(option, this.val);
		}
		
		@Override
		public void renderButton(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
			super.renderButton(p_230431_1_, p_230431_2_, p_230431_3_, p_230431_4_);
			
			if (this.isHovered()) {
				renderTooltip(p_230431_1_, tooltip, p_230431_2_, p_230431_3_);
			}
		}
	}
	
	public void addSlider(String option) {
		double d = customization.getDouble(option);
		d = Math.floor(d * 10 + 0.5) / 10;
		
		double max = customization.getMax(option, this.getLimits());
		double min = customization.getMin(option, this.getLimits());
		
		String text = grapplemod.proxy.localize(this.customization.getName(option));
		String desc = grapplemod.proxy.localize(this.customization.getDescription(option));
		GuiSlider slider = new GuiSlider(10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, new StringTextComponent(text), min, max, d, option, new StringTextComponent(desc));
		
		posy += 25;
		this.addButton(slider);
		options.put(slider, option);
	}

	public void showCategoryScreen(grapplemod.upgradeCategories category) {
		this.addButton(new Button(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, new StringTextComponent(grapplemod.proxy.localize("grapplemodifier.back.desc")), new PressBack()));
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
	
	Button buttonpressed = null;
	
	@Override
	public void onClose() {
//		this.updateOptions();
		this.tileent.setCustomizationClient(customization);
		
		super.onClose();
	}
	
	public void updateEnabled() {
		for (Widget b : this.options.keySet()) {
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
			
			b.active = enabled;

			if (b instanceof GuiSlider) {
				((GuiSlider) b).tooltip = new StringTextComponent(desc);
			}
			if (b instanceof GuiCheckbox) {
				((GuiCheckbox) b).tooltip = new StringTextComponent(desc);
			}
		}
	}
	
	public int getLimits() {
		if (this.tileent.isUnlocked(grapplemod.upgradeCategories.LIMITS) || Minecraft.getInstance().player.isCreative()) {
			return 1;
		}
		return 0;
	}
}
