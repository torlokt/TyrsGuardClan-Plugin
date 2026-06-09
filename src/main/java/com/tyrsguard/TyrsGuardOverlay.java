package com.tyrsguard;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TyrsGuardOverlay extends Overlay
{
    // ── Layout ────────────────────────────────────────────────────────────────
    private static final int   ICON_SIZE = 22;   // plugin icon rendered size
    private static final int   PAD       = 3;    // padding around icon
    private static final int   W         = ICON_SIZE + PAD * 2;  // total overlay width
    private static final int   H         = ICON_SIZE + PAD * 2;  // total overlay height
    private static final int   DOT_SIZE  = 9;    // status indicator dot

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color COL_BG       = new Color(15,  15,  15,  190);
    private static final Color COL_LISTED   = new Color(45,  210, 45);
    private static final Color COL_UNLISTED = new Color(210, 45,  45);

    // ── State ─────────────────────────────────────────────────────────────────
    private volatile boolean geActive = false;
    private volatile String  geLister = null;

    // ── Deps ──────────────────────────────────────────────────────────────────
    private final Client          client;
    private final TyrsGuardConfig config;
    private final TooltipManager  tooltipManager;

    /** Cached scaled version of /com/tyrsguard/icon.png */
    private Image scaledIcon;

    // ─────────────────────────────────────────────────────────────────────────

    @Inject
    public TyrsGuardOverlay(Client client, TyrsGuardConfig config, TooltipManager tooltipManager)
    {
        this.client         = client;
        this.config         = config;
        this.tooltipManager = tooltipManager;

        // Load the same icon used on the nav button
        try
        {
            BufferedImage raw = ImageUtil.loadImageResource(TyrsGuardOverlay.class,
                "/com/tyrsguard/icon.png");
            if (raw != null)
                scaledIcon = raw.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
        }
        catch (Exception ignored) { /* overlay still works without the icon */ }

        setPosition(OverlayPosition.TOP_LEFT); // groups with infoboxes; player can drag anywhere
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.MED);
        setMovable(true);
        setResizable(false);
    }

    // ── State update (called from plugin) ─────────────────────────────────────

    public void setListingState(boolean active, String lister)
    {
        this.geActive = active;
        this.geLister = lister;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public Dimension render(Graphics2D g)
    {
        // Respect the two config toggles
        if (!config.geListingOverlayEnabled()) return null;
        if (config.geListingOverlayHideWhenUnlisted() && !geActive) return null;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ── Dark background pill ──────────────────────────────────────────────
        g.setColor(COL_BG);
        g.fillRoundRect(0, 0, W, H, 8, 8);

        // ── Plugin icon ───────────────────────────────────────────────────────
        if (scaledIcon != null)
        {
            g.drawImage(scaledIcon, PAD, PAD, null);
        }
        else
        {
            // Fallback text if icon didn't load
            g.setColor(new Color(200, 160, 80));
            g.setFont(new Font("SansSerif", Font.BOLD, 9));
            g.drawString("TG", PAD + 2, H - PAD - 3);
        }

        // ── Status dot (bottom-right corner, badge-style) ─────────────────────
        Color dot = geActive ? COL_LISTED : COL_UNLISTED;
        int dotX  = W - DOT_SIZE - 1;
        int dotY  = H - DOT_SIZE - 1;

        // Dark ring so the dot is readable over any icon colour
        g.setColor(new Color(5, 5, 5, 220));
        g.fillOval(dotX - 2, dotY - 2, DOT_SIZE + 4, DOT_SIZE + 4);

        // Soft glow behind dot
        g.setColor(new Color(dot.getRed(), dot.getGreen(), dot.getBlue(), 70));
        g.fillOval(dotX - 1, dotY - 1, DOT_SIZE + 2, DOT_SIZE + 2);

        // Dot itself
        g.setColor(dot);
        g.fillOval(dotX, dotY, DOT_SIZE, DOT_SIZE);

        // ── Tooltip on hover ──────────────────────────────────────────────────
        Point mouse = client.getMouseCanvasPosition();
        if (getBounds().contains(mouse.getX(), mouse.getY()))
        {
            String tip = (geActive && geLister != null && !geLister.isEmpty())
                ? "Clan is listed by: " + geLister
                : "Clan is not currently listed at the GE";
            tooltipManager.add(new Tooltip(tip));
        }

        return new Dimension(W, H);
    }
}
