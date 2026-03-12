package com.tyrsguard;

import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TyrsGuardPanel extends PluginPanel
{
    private final TyrsGuardConfig config;
    private final TyrsGuardPlugin plugin;

    private JLabel xpLabel;
    private JLabel rankLabel;
    private JProgressBar rankProgressBar;
    private JLabel progressLabel;
    private JButton refreshXpButton;

    private JComboBox<String> categoryDropdown;
    private JTextField donationGpField;
    private JLabel donationLabel;
    private JCheckBox staffPresentCheckbox;
    private JTextField staffNameField;
    private JLabel staffNameLabel;
    private JTextArea detailsArea;

    private BufferedImage capturedScreenshot;
    private JLabel screenshotPreviewLabel;
    private JButton screenshotButton;
    private JButton submitButton;
    private JLabel statusLabel;

    private static final int PAD = 8;
    private static final Color BG_DARK  = new Color(30, 30, 30);
    private static final Color BG_PANEL = new Color(42, 42, 42);
    private static final Color BG_INPUT = new Color(55, 55, 55);
    private static final Color COL_GOLD = new Color(200, 160, 80);
    private static final Color COL_TEXT = new Color(220, 220, 220);
    private static final Color COL_DIM  = new Color(140, 140, 140);

    private static final String[] CATEGORIES = {
        "Select a category...",
        "Events Participation",
        "Events Win",
        "Recruiting",
        "Donation",
        "Hosting a Mass",
        "Hosting an Event",
        "Teaching a Raid",
        "Participating in a Mass"
    };

    public TyrsGuardPanel(TyrsGuardConfig config, TyrsGuardPlugin plugin)
    {
        this.config = config;
        this.plugin = plugin;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // Wrapper that forces full viewport width
        JPanel wrapper = new JPanel(new GridBagLayout())
        {
            
            @Override public Dimension getPreferredSize()
            {
                Dimension d = super.getPreferredSize();
                d.width = Math.max(d.width, PluginPanel.PANEL_WIDTH);
                return d;
            }
        };
        wrapper.setBackground(BG_DARK);
        wrapper.setBorder(new EmptyBorder(PAD, PAD, PAD, PAD));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 6, 0);

        wrapper.add(section("Tyrs Guard Clan", headerPanel()), gbc);
        wrapper.add(section("Your XP & Rank",          xpPanel()),     gbc);
        wrapper.add(section("Submission Details",       formPanel()),   gbc);
        wrapper.add(section("Screenshot",               shotPanel()),   gbc);
        wrapper.add(submitPanel(),                                       gbc);

        // filler
        gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        wrapper.add(Box.createVerticalGlue(), gbc);

        JScrollPane scroll = new JScrollPane(wrapper,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);
        add(scroll, BorderLayout.CENTER);
    }

    // ── Section card ──────────────────────────────────────────────────────────

    private JPanel section(String title, JPanel inner)
    {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            new EmptyBorder(6, PAD, PAD, PAD)));

        JLabel hdr = new JLabel(title);
        hdr.setForeground(COL_GOLD);
        hdr.setFont(hdr.getFont().deriveFont(Font.BOLD, 12f));
        hdr.setBorder(new EmptyBorder(0, 0, 6, 0));
        card.add(hdr, BorderLayout.NORTH);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ── Inner panels (all use GridBagLayout so they fill width) ───────────────

    private JPanel headerPanel()
    {
        JPanel p = inner();
        GridBagConstraints g = fillGbc();
        JLabel sub = lbl("Submit proof & earn clan XP", COL_DIM, 11f);
        p.add(sub, g);
        return p;
    }

    private JPanel xpPanel()
    {
        JPanel p = inner();
        GridBagConstraints g = fillGbc();

        rankLabel = lbl("Rank: —", COL_TEXT, Font.BOLD, 12f);
        xpLabel   = lbl("XP: —",   COL_DIM,  11f);

        rankProgressBar = new JProgressBar(0, 100);
        rankProgressBar.setValue(0);
        rankProgressBar.setForeground(new Color(180, 0, 0));
        rankProgressBar.setBackground(new Color(55, 55, 55));
        rankProgressBar.setPreferredSize(new Dimension(0, 6));

        progressLabel = lbl("Set Discord ID in config", COL_DIM, 10f);

        refreshXpButton = btn("Refresh XP", new Color(60, 60, 60));
        refreshXpButton.addActionListener(e -> refreshXp());

        p.add(rankLabel,        g);
        g.insets = new Insets(2, 0, 0, 0);
        p.add(xpLabel,          g);
        g.insets = new Insets(6, 0, 0, 0);
        p.add(rankProgressBar,  g);
        g.insets = new Insets(2, 0, 0, 0);
        p.add(progressLabel,    g);
        g.insets = new Insets(8, 0, 0, 0);
        p.add(refreshXpButton,  g);
        return p;
    }

    private JPanel formPanel()
    {
        JPanel p = inner();
        GridBagConstraints g = fillGbc();

        categoryDropdown = new JComboBox<>(CATEGORIES);
        styleCombo(categoryDropdown);
        categoryDropdown.addActionListener(e -> onCategoryChanged());

        donationLabel  = lbl("Donation Amount (GP):", COL_TEXT, 11f);
        donationGpField = input("e.g. 5000000");
        donationLabel.setVisible(false);
        donationGpField.setVisible(false);

        staffPresentCheckbox = new JCheckBox("Staff was present");
        staffPresentCheckbox.setBackground(BG_PANEL);
        staffPresentCheckbox.setForeground(COL_TEXT);
        staffPresentCheckbox.setFocusPainted(false);
        staffPresentCheckbox.addActionListener(e -> onStaffChanged());

        staffNameLabel = lbl("Staff Member Name:", COL_TEXT, 11f);
        staffNameField = input("Enter staff name");
        staffNameLabel.setVisible(false);
        staffNameField.setVisible(false);

        detailsArea = new JTextArea(3, 0);
        detailsArea.setBackground(BG_INPUT);
        detailsArea.setForeground(COL_TEXT);
        detailsArea.setCaretColor(COL_TEXT);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setBorder(new EmptyBorder(4, 4, 4, 4));
        JScrollPane ds = new JScrollPane(detailsArea);
        ds.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

        p.add(lbl("Category:", COL_TEXT, 11f), g);
        g.insets = new Insets(3, 0, 0, 0);
        p.add(categoryDropdown,    g);
        g.insets = new Insets(6, 0, 0, 0);
        p.add(donationLabel,       g);
        g.insets = new Insets(3, 0, 0, 0);
        p.add(donationGpField,     g);
        g.insets = new Insets(6, 0, 0, 0);
        p.add(staffPresentCheckbox, g);
        g.insets = new Insets(4, 0, 0, 0);
        p.add(staffNameLabel,      g);
        g.insets = new Insets(3, 0, 0, 0);
        p.add(staffNameField,      g);
        g.insets = new Insets(6, 0, 0, 0);
        p.add(lbl("Additional Details (optional):", COL_TEXT, 11f), g);
        g.insets = new Insets(3, 0, 0, 0);
        p.add(ds,                  g);
        return p;
    }

    private JPanel shotPanel()
    {
        JPanel p = inner();
        GridBagConstraints g = fillGbc();

        screenshotPreviewLabel = lbl("No screenshot captured", COL_DIM, 11f);
        screenshotButton = btn("Capture Game Window", new Color(0, 110, 0));
        screenshotButton.addActionListener(e -> captureScreenshot());

        p.add(screenshotPreviewLabel, g);
        g.insets = new Insets(6, 0, 0, 0);
        p.add(screenshotButton, g);
        return p;
    }

    private JPanel submitPanel()
    {
        JPanel p = inner();
        p.setBackground(BG_DARK);
        GridBagConstraints g = fillGbc();

        statusLabel = lbl(" ", COL_DIM, 11f);
        submitButton = btn("Submit to Tyrs Guard Clan", new Color(160, 0, 0));
        submitButton.setFont(submitButton.getFont().deriveFont(Font.BOLD, 13f));
        submitButton.addActionListener(e -> submit());

        p.add(statusLabel,  g);
        g.insets = new Insets(4, 0, 0, 0);
        p.add(submitButton, g);
        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JPanel inner()
    {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_PANEL);
        return p;
    }

    private GridBagConstraints fillGbc()
    {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = GridBagConstraints.RELATIVE;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets = new Insets(0, 0, 0, 0);
        return g;
    }

    private JLabel lbl(String text, Color color, float size)
    {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(l.getFont().deriveFont(size));
        return l;
    }

    private JLabel lbl(String text, Color color, int style, float size)
    {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(l.getFont().deriveFont(style, size));
        return l;
    }

    private JTextField input(String tooltip)
    {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(COL_TEXT);
        f.setCaretColor(COL_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            new EmptyBorder(3, 5, 3, 5)));
        f.setToolTipText(tooltip);
        return f;
    }

    private void styleCombo(JComboBox<?> c)
    {
        c.setBackground(BG_INPUT);
        c.setForeground(COL_TEXT);
        c.setFont(c.getFont().deriveFont(11f));
    }

    private JButton btn(String text, Color bg)
    {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(b.getFont().deriveFont(12f));
        return b;
    }

    // ── Events ────────────────────────────────────────────────────────────────

    private void onCategoryChanged()
    {
        boolean isDonation = "Donation".equals(categoryDropdown.getSelectedItem());
        donationLabel.setVisible(isDonation);
        donationGpField.setVisible(isDonation);
        revalidate(); repaint();
    }

    private void onStaffChanged()
    {
        boolean checked = staffPresentCheckbox.isSelected();
        staffNameLabel.setVisible(checked);
        staffNameField.setVisible(checked);
        revalidate(); repaint();
    }

    private void captureScreenshot()
    {
        screenshotButton.setEnabled(false);
        status("Capturing...", Color.YELLOW);
        plugin.requestScreenshot(img -> SwingUtilities.invokeLater(() -> {
            capturedScreenshot = img;
            if (img != null)
            {
                Image thumb = img.getScaledInstance(PluginPanel.PANEL_WIDTH - PAD * 4, -1, Image.SCALE_SMOOTH);
                screenshotPreviewLabel.setIcon(new ImageIcon(thumb));
                screenshotPreviewLabel.setText(null);
                status("Screenshot captured!", new Color(0, 200, 0));
            }
            else { status("Log in to the game first.", Color.RED); }
            screenshotButton.setEnabled(true);
        }));
    }

    private void submit()
    {
        String category = (String) categoryDropdown.getSelectedItem();
        if (category == null || category.equals("Select a category...")) { status("Please select a category.", Color.RED); return; }
        if (capturedScreenshot == null) { status("Please capture a screenshot first.", Color.RED); return; }

        String gpStr = null;
        if ("Donation".equals(category))
        {
            String raw = donationGpField.getText().trim().replaceAll("[^0-9]", "");
            if (raw.isEmpty()) { status("Enter donation amount in GP.", Color.RED); return; }
            if (Long.parseLong(raw) < 100_000) { status("Minimum donation is 100,000 GP.", Color.RED); return; }
            gpStr = raw;
        }

        String discordId = config.discordId().trim();
        if (discordId.isEmpty()) { status("Set your Discord ID in config.", Color.RED); return; }
        String apiUrl = config.botApiUrl().trim();
        if (apiUrl.isEmpty()) { status("Set Bot API URL in config.", Color.RED); return; }

        String rsn      = plugin.getLocalPlayerName();
        String staffName = staffPresentCheckbox.isSelected() ? staffNameField.getText().trim() : null;
        String details   = detailsArea.getText().trim();

        submitButton.setEnabled(false);
        status("Submitting...", Color.YELLOW);

        final String fCat   = category;
        final String fGp    = gpStr;
        final String fStaff = (staffName != null && !staffName.isEmpty()) ? staffName : null;
        final String fDets  = details.isEmpty() ? "Submitted via RuneLite plugin" : details;
        final BufferedImage fShot = capturedScreenshot;

        new Thread(() -> {
            try
            {
                String boundary = UUID.randomUUID().toString().replace("-", "");
                HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl + "/submit").openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                conn.setRequestProperty("X-Plugin-Secret", config.pluginApiSecret());

                try (OutputStream out = conn.getOutputStream())
                {
                    field(out, boundary, "discordId", discordId);
                    field(out, boundary, "rsn",       rsn != null ? rsn : "");
                    field(out, boundary, "category",  fCat);
                    field(out, boundary, "details",   fDets);
                    field(out, boundary, "staffPresent", fStaff != null ? "true" : "false");
                    if (fStaff != null) field(out, boundary, "staffName", fStaff);
                    if (fGp    != null) field(out, boundary, "donationGp", fGp);
                    ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                    ImageIO.write(fShot, "png", imgBytes);
                    filePart(out, boundary, "screenshot", "screenshot.png", "image/png", imgBytes.toByteArray());
                    out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                SwingUtilities.invokeLater(() -> {
                    if (code == 200)
                    {
                        status("Submitted! Staff will review soon.", new Color(0, 200, 0));
                        capturedScreenshot = null;
                        screenshotPreviewLabel.setIcon(null);
                        screenshotPreviewLabel.setText("No screenshot captured");
                        detailsArea.setText("");
                        donationGpField.setText("");
                        staffPresentCheckbox.setSelected(false);
                        staffNameField.setText("");
                        staffNameLabel.setVisible(false);
                        staffNameField.setVisible(false);
                        categoryDropdown.setSelectedIndex(0);
                    }
                    else { status("Failed (HTTP " + code + "). Check config.", Color.RED); }
                    submitButton.setEnabled(true);
                });
            }
            catch (Exception e)
            {
                log.error("Submit error", e);
                SwingUtilities.invokeLater(() -> { status("Error: " + e.getMessage(), Color.RED); submitButton.setEnabled(true); });
            }
        }, "TyrsGuardClan-Submit").start();
    }

    public void refreshXp()
    {
        String discordId = config.discordId().trim();
        if (discordId.isEmpty()) { rankLabel.setText("Rank: —"); progressLabel.setText("Set Discord ID in config"); return; }
        refreshXpButton.setEnabled(false);
        new Thread(() -> {
            try
            {
                HttpURLConnection conn = (HttpURLConnection) new URL(config.botApiUrl().trim() + "/xp/" + discordId).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("X-Plugin-Secret", config.pluginApiSecret());
                int code = conn.getResponseCode();
                String body = new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                if (code == 200)
                {
                    long pts  = jsonLong(body, "points");
                    String rank     = jsonStr(body, "rank");
                    String nextRank = jsonStr(body, "nextRank");
                    long toNext     = jsonLong(body, "pointsToNext");
                    long nextReq    = jsonLong(body, "nextPointsReq");
                    long curReq     = jsonLong(body, "currentPointsReq");
                    SwingUtilities.invokeLater(() -> {
                        rankLabel.setText("Rank: " + rank);
                        xpLabel.setText("XP: " + String.format("%,d", pts));
                        if (nextRank != null && !nextRank.equals("null") && nextReq > 0)
                        {
                            long range = nextReq - curReq;
                            int  pct   = range > 0 ? (int)((pts - curReq) * 100 / range) : 100;
                            rankProgressBar.setValue(pct);
                            progressLabel.setText(String.format("%,d XP to %s", toNext, nextRank));
                        }
                        else { rankProgressBar.setValue(100); progressLabel.setText("Maximum rank reached!"); }
                        refreshXpButton.setEnabled(true);
                    });
                }
                else { SwingUtilities.invokeLater(() -> { progressLabel.setText("Check Discord ID & API URL"); refreshXpButton.setEnabled(true); }); }
            }
            catch (Exception e)
            {
                log.warn("XP error", e);
                SwingUtilities.invokeLater(() -> { progressLabel.setText("Bot not reachable"); refreshXpButton.setEnabled(true); });
            }
        }, "TyrsGuardClan-XP").start();
    }

    // ── Multipart ─────────────────────────────────────────────────────────────

    private void field(OutputStream out, String b, String name, String val) throws IOException
    {
        out.write(("--" + b + "\r\nContent-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(val.getBytes(StandardCharsets.UTF_8));
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    private void filePart(OutputStream out, String b, String field, String fname, String ct, byte[] data) throws IOException
    {
        out.write(("--" + b + "\r\nContent-Disposition: form-data; name=\"" + field + "\"; filename=\"" + fname + "\"\r\nContent-Type: " + ct + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(data);
    }

    // ── JSON ──────────────────────────────────────────────────────────────────

    private long jsonLong(String json, String key)
    {
        try { String p = "\"" + key + "\":"; int i = json.indexOf(p); if (i < 0) return 0; int s = i + p.length(); while (s < json.length() && json.charAt(s)==' ') s++; StringBuilder sb = new StringBuilder(); for (int j=s;j<json.length();j++){char c=json.charAt(j);if(Character.isDigit(c)||c=='-')sb.append(c);else break;} return sb.length()>0?Long.parseLong(sb.toString()):0; } catch(Exception e){return 0;}
    }

    private String jsonStr(String json, String key)
    {
        try { String p = "\"" + key + "\":\""; int i = json.indexOf(p); if (i < 0) return null; int s = i+p.length(); int e = json.indexOf("\"",s); return e>s?json.substring(s,e):null; } catch(Exception e){return null;}
    }

    private void status(String msg, Color c) { statusLabel.setText(msg); statusLabel.setForeground(c); }
}
