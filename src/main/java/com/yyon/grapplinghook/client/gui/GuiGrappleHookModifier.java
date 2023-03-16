package com.yyon.grapplinghook.client.gui;

import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.client.ClientProxyInterface;
import com.yyon.grapplinghook.client.gui.widget.BackgroundWidget;
import com.yyon.grapplinghook.client.gui.widget.CustomizationCheckbox;
import com.yyon.grapplinghook.client.gui.widget.CustomizationSlider;
import com.yyon.grapplinghook.client.gui.widget.TextWidget;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class GuiGrappleHookModifier extends Screen {

	private final OnPress actionGoBack = button -> mainScreen();

	private final int menuSizeX = 221;
	private final int menuSizeY = 221;

	protected int guiLeft;
	protected int guiTop;
	private int widgetPosYIncrementor;

	private final TileEntityGrappleModifier tileEnt;

	private HashMap<AbstractWidget, String> options;
	private GrappleCustomization customization;
	private GrappleCustomization.UpgradeCategory category = null;

	public GuiGrappleHookModifier(TileEntityGrappleModifier tileent) {
		super(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.title.desc")));

		this.tileEnt = tileent;
		this.customization = tileent.customization;
	}

	@Override
	public void init() {
		this.guiLeft = (this.width - this.menuSizeX) / 2;
		this.guiTop = (this.height - this.menuSizeY) / 2;

		this.mainScreen();
	}

	public void mainScreen() {
		this.clearScreen();

		this.addRenderableWidget(Button.builder(
				Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.close.desc")),
				button -> this.onClose())
				.pos(this.guiLeft + 10, this.guiTop + this.menuSizeY - 20 - 10)
				.size(50, 20)
				.build()
		);

		this.addRenderableWidget(Button.builder(
				Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.reset.desc")),
				button -> {
					this.customization = new GrappleCustomization();
					this.mainScreen();
				})
				.pos(this.guiLeft + this.menuSizeX - 50 - 10, this.guiTop + this.menuSizeY - 20 - 10)
				.size(50, 20)
				.build()
		);

		this.addRenderableWidget(Button.builder(
						Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.helpbutton.desc")),
						button -> this.helpScreen())
				.pos(this.guiLeft + 10 + 75, this.guiTop + this.menuSizeY - 20 - 10)
				.size(50, 20)
				.build()
		);

		int y = 0;
		int x = 0;
		for (int i = 0; i < GrappleCustomization.UpgradeCategory.size(); i++) {
			GrappleCustomization.UpgradeCategory category = GrappleCustomization.UpgradeCategory.fromInt(i);
			if (category == GrappleCustomization.UpgradeCategory.LIMITS) continue;

			if (i == GrappleCustomization.UpgradeCategory.size() / 2) {
				y = 0;
				x += 1;
			}

			this.addRenderableWidget(
					Button.builder(Component.literal(category.getName()), this.createCategoryActionHandler(category))
							.pos(this.guiLeft + 10 + 105 * x, this.guiTop + 15 + 30 * y)
							.size(95, 20)
							.build()
			);
			y += 1;
		}

		this.addRenderableWidget(new TextWidget(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.apply.desc")), this.guiLeft + 10, this.guiTop + this.menuSizeY - 20 - 10 - 10));
	}

	public void clearScreen() {
		this.category = null;
		this.widgetPosYIncrementor = 10;
		this.options = new HashMap<>();
		this.clearWidgets();
		
		this.addRenderableWidget(new BackgroundWidget(this.guiLeft, this.guiTop, this.menuSizeX, this.menuSizeY));
	}

	public void notAllowedScreen(GrappleCustomization.UpgradeCategory category) {
		this.clearScreen();
		this.addRenderableWidget(
				Button.builder(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.back.desc")), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + this.menuSizeY - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.category = category;

		this.addRenderableWidget(new TextWidget(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.unlock1.desc")), this.guiLeft + 10, this.guiTop + 10));
		this.addRenderableWidget(new TextWidget(Component.literal(this.category.getName()), this.guiLeft + 10, this.guiTop + 25));
		this.addRenderableWidget(new TextWidget(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.unlock2.desc")), this.guiLeft + 10, this.guiTop + 40));
		this.addRenderableWidget(new TextWidget(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.unlock3.desc")), this.guiLeft + 10, this.guiTop + 55));
		this.addRenderableWidget(new TextWidget(new ItemStack(this.category.getItem()).getDisplayName(), this.guiLeft + 10, this.guiTop + 70));
		this.addRenderableWidget(new TextWidget(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.unlock4.desc")), this.guiLeft + 10, this.guiTop + 85));
	}

	public void helpScreen() {
		clearScreen();

		this.addRenderableWidget(
				Button.builder(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.back.desc")), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + this.menuSizeY - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.addRenderableWidget(new TextWidget(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.help.desc")), this.guiLeft + 10, this.guiTop + 10));
	}

	public void addCheckbox(String option) {
		String text = ClientProxyInterface.proxy.localize(this.customization.getName(option));
		String desc = ClientProxyInterface.proxy.localize(this.customization.getDescription(option));
		CustomizationCheckbox checkbox = new CustomizationCheckbox(this, 10 + this.guiLeft, this.getNextYPosition(), this.menuSizeX - 20, 20, Component.literal(text), customization.getBoolean(option), option, Component.literal(desc));
		this.addRenderableWidget(checkbox);
		this.options.put(checkbox, option);
	}
	
	public void addSlider(String option) {
		double d = this.customization.getDouble(option);
		d = Math.floor(d * 10 + 0.5) / 10;
		
		double max = this.customization.getMax(option, this.getLimits());
		double min = this.customization.getMin(option, this.getLimits());
		
		String text = ClientProxyInterface.proxy.localize(this.customization.getName(option));
		String desc = ClientProxyInterface.proxy.localize(this.customization.getDescription(option));
		CustomizationSlider slider = new CustomizationSlider(this, 10 + this.guiLeft, this.getNextYPosition(), this.menuSizeX - 20, 20, Component.literal(text), min, max, d, option, Component.literal(desc));
		this.addRenderableWidget(slider);
		this.options.put(slider, option);
	}

	public void showCategoryScreen(GrappleCustomization.UpgradeCategory category) {
		this.clearScreen();

		this.addRenderableWidget(
				Button.builder(Component.literal(ClientProxyInterface.proxy.localize("grapplemodifier.back.desc")), actionGoBack)
						.pos(this.guiLeft + 10, this.guiTop + this.menuSizeY - 20 - 10)
						.size(50, 20)
						.build()
		);

		this.category = category;

		switch (this.category) {
			case ROPE -> {
				this.addSlider("maxlen");
				this.addCheckbox("phaserope");
				this.addCheckbox("sticky");
			}

			case THROW -> {
				this.addSlider("hookgravity");
				this.addSlider("throwspeed");
				this.addCheckbox("reelin");
				this.addSlider("verticalthrowangle");
				this.addSlider("sneakingverticalthrowangle");
				this.addCheckbox("detachonkeyrelease");
			}

			case MOTOR -> {
				this.addCheckbox("motor");
				this.addSlider("motormaxspeed");
				this.addSlider("motoracceleration");
				this.addCheckbox("motorwhencrouching");
				this.addCheckbox("motorwhennotcrouching");
				this.addCheckbox("smartmotor");
				this.addCheckbox("motordampener");
				this.addCheckbox("pullbackwards");
			}

			case FORCEFIELD -> {
				this.addCheckbox("repel");
				this.addSlider("repelforce");
			}

			case MAGNET -> {
				this.addCheckbox("attract");
				this.addSlider("attractradius");
			}

			case DOUBLE -> {
				this.addCheckbox("doublehook");
				this.addCheckbox("smartdoublemotor");
				this.addSlider("angle");
				this.addSlider("sneakingangle");
				this.addCheckbox("oneropepull");
			}

			case ROCKET -> {
				this.addCheckbox("rocket");
				this.addSlider("rocket_force");
				this.addSlider("rocket_active_time");
				this.addSlider("rocket_refuel_ratio");
				this.addSlider("rocket_vertical_angle");
			}

			case SWING -> this.addSlider("playermovementmult");
			case STAFF -> this.addCheckbox("enderstaff");
		}
		
		this.updateEnabled();
	}
	
	@Override
	public void onClose() {
		this.tileEnt.setCustomizationClient(customization);
		super.onClose();
	}
	
	public void updateEnabled() {
		for (AbstractWidget b : this.options.keySet()) {
			String option = this.options.get(b);
			boolean enabled = true;
			
			String desc = ClientProxyInterface.proxy.localize(this.customization.getDescription(option));
			
			if (!this.customization.isOptionValid(option)) {
				desc = ClientProxyInterface.proxy.localize("grapplemodifier.incompatability.desc") + "\n" + desc;
				enabled = false;
			}
			
			int level = this.customization.optionEnabled(option);
			if (this.getLimits() < level) {
				desc = level == 1
						? ClientProxyInterface.proxy.localize("grapplemodifier.limits.desc") + "\n" + desc
						: ClientProxyInterface.proxy.localize("grapplemodifier.locked.desc") + "\n" + desc;
				enabled = false;
			}
			
			b.active = enabled;

			if (b instanceof CustomizationSlider slide) {
				slide.setTooltip(Component.literal(desc));
				slide.setAlpha(enabled ? 1.0F : 0.5F);
			}

			if (b instanceof CustomizationCheckbox check) {
				check.setTooltip(Component.literal(desc));
				check.setAlpha(enabled ? 1.0F : 0.5F);
			}
		}
	}
	
	public int getLimits() {
		if(Minecraft.getInstance().player == null) return 0;
		return this.tileEnt.isUnlocked(GrappleCustomization.UpgradeCategory.LIMITS) || Minecraft.getInstance().player.isCreative()
				? 1
				: 0;
	}

	public GrappleCustomization getCurrentCustomizations() {
		return this.customization;
	}

	private int getNextYPosition() {
		this.widgetPosYIncrementor += 22;
		return this.guiTop + this.widgetPosYIncrementor - 22;
	}

	protected OnPress createCategoryActionHandler(GrappleCustomization.UpgradeCategory category) {
		return button -> {
			if(Minecraft.getInstance().player == null) return;

			boolean unlocked = this.tileEnt.isUnlocked(category) || Minecraft.getInstance().player.isCreative();
			if (unlocked) {
				this.showCategoryScreen(category);
				return;
			}

			this.notAllowedScreen(category);
		};
	}
}
