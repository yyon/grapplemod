package com.yyon.grapplinghook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

public class GuiModifier extends GuiScreen {
	private static final ResourceLocation texture = new ResourceLocation("grapplemod", "textures/gui/guimodifier_bg.png");
	
	int xSize;
	int ySize;

    protected int guiLeft;
    protected int guiTop;
    
	public GuiModifier() {
		super();
		xSize = 176;
		ySize = 221;
	}
	
	
	 @Override
	 public void initGui()
	 {
		super.initGui();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		
		mainscreen();
	 }
	 
	 public void mainscreen() {
		this.buttonList.add(new GuiButton( 1, this.guiLeft + this.xSize - 50 - 10, this.guiTop + this.ySize - 20 - 10, 50, 20, "Cancel"));
		this.buttonList.add(new GuiButton( 2, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, "Apply"));
		
		for (int i = 0; i < grapplemod.upgradeCategories.size(); i++) {
			grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(i);
			this.buttonList.add(new GuiButton( 99 + i, this.guiLeft + 10, this.guiTop + 5 + 22 * i, 100, 20, category.description));
		}
	 }
	 
	 public void clearscreen() {
		 this.buttonList.clear();
	 }
	 
	 public void notAllowedScreen(grapplemod.upgradeCategories category) {
			this.buttonList.add(new GuiButton( 3, this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, "Back"));
	 }
	 
	 @Override
	 protected void actionPerformed(GuiButton b)
	 {
		 if (b.id == 1) {
			 Minecraft.getMinecraft().player.closeScreen();
			 return;
		 } else if (b.id == 2) {
			 
		 } else if (b.id == 3) {
			 clearscreen();
			 mainscreen();
		 } else {
			 int categoryid = b.id - 99;
			 grapplemod.upgradeCategories category = grapplemod.upgradeCategories.fromInt(categoryid);
			 
			 System.out.println("clicked on");
			 System.out.println(category.description);
			 
			 clearscreen();
			 notAllowedScreen(category);
		 }
	 }
	 
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
		// background
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        
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
