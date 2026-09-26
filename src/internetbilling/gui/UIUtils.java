package internetbilling.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Common UI Styling and Component Utility for Internet Billing Management System.
 * Follows a clean, modern, professional ISP-style color palette.
 * Provides 100% Java Swing-native vector ImageIcons to eliminate all missing-glyph square boxes.
 */
public class UIUtils {

    // =========================================================================
    // EXACT MODERN PROFESSIONAL ISP-STYLE COLOUR PALETTE
    // =========================================================================
    public static final Color PRIMARY_DARK = new Color(0x0F, 0x27, 0x47);     // Dark Navy Blue #0F2747 (Primary / Sidebar)
    public static final Color PRIMARY = new Color(0x0F, 0x27, 0x47);          // Dark Navy Blue #0F2747
    public static final Color SECONDARY_BLUE = new Color(0x19, 0x76, 0xD2);   // Professional Blue #1976D2
    public static final Color ACCENT_BLUE = SECONDARY_BLUE;                  // Alias
    public static final Color HOVER_BLUE = new Color(0x15, 0x65, 0xC0);       // Dark Blue #1565C0
    public static final Color ACCENT_HOVER = HOVER_BLUE;                     // Alias
    public static final Color BG_LIGHT = new Color(0xF4, 0xF7, 0xFB);         // Very Light Blue-Gray #F4F7FB
    public static final Color CARD_BG = Color.WHITE;                          // Cards/Panels White #FFFFFF
    public static final Color HEADER_NAVY = new Color(0x12, 0x35, 0x5B);      // Header Navy Blue #12355B
    public static final Color TEXT_DARK = new Color(0x17, 0x2B, 0x4D);        // Primary Text Dark Navy #172B4D
    public static final Color TEXT_MUTED = new Color(0x66, 0x70, 0x85);       // Secondary Text Gray #667085
    public static final Color BORDER_COLOR = new Color(0xD9, 0xE2, 0xEC);     // Light Gray #D9E2EC
    public static final Color SUCCESS_GREEN = new Color(0x16, 0xA3, 0x4A);    // Success/Paid Green #16A34A
    public static final Color WARNING_ORANGE = new Color(0xF5, 0x9E, 0x0B);   // Warning/Pending Orange #F59E0B
    public static final Color DANGER_RED = new Color(0xDC, 0x26, 0x26);       // Error/Overdue Red #DC2626

    // =========================================================================
    // TYPOGRAPHY (Segoe UI standard)
    // =========================================================================
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_REGULAR_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_STAT_NUMBER = new Font("Segoe UI", Font.BOLD, 24);

    // =========================================================================
    // BUTTON STYLES
    // =========================================================================

    /**
     * Styles a standard button with interactive hover effects.
     */
    public static void styleButton(JButton btn, Color bg, Color hoverBg, Color fg) {
        btn.setFont(FONT_REGULAR_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1, true),
            BorderFactory.createEmptyBorder(9, 18, 9, 18)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                }
            }
        });
    }

    /**
     * Primary action button (Professional Blue #1976D2 with #1565C0 hover).
     */
    public static void stylePrimaryButton(JButton btn) {
        styleButton(btn, SECONDARY_BLUE, HOVER_BLUE, Color.WHITE);
    }

    /**
     * Success action button (Green #16A34A).
     */
    public static void styleSuccessButton(JButton btn) {
        styleButton(btn, SUCCESS_GREEN, new Color(0x15, 0x80, 0x3D), Color.WHITE);
    }

    /**
     * Danger / Delete / Exit button (Red #DC2626).
     */
    public static void styleDangerButton(JButton btn) {
        styleButton(btn, DANGER_RED, new Color(0xB9, 0x1C, 0x1C), Color.WHITE);
    }

    /**
     * Warning / Pending button (Orange #F59E0B).
     */
    public static void styleWarningButton(JButton btn) {
        styleButton(btn, WARNING_ORANGE, new Color(0xD9, 0x77, 0x06), Color.WHITE);
    }

    /**
     * Secondary / Cancel / Refresh button (White background with #D9E2EC border).
     */
    public static void styleSecondaryButton(JButton btn) {
        btn.setFont(FONT_REGULAR_BOLD);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(new Color(0xEB, 0xF1, 0xF8));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(Color.WHITE);
                }
            }
        });
    }

    /**
     * Sidebar navigation button with Icon support (Dark Navy #0F2747 with highlight).
     */
    public static void styleSidebarButton(JButton btn, Icon icon, boolean active) {
        btn.setFont(FONT_REGULAR_BOLD);
        btn.setBackground(active ? HEADER_NAVY : PRIMARY_DARK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(210, 42));

        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(10);
        }

        Border leftHighlight = BorderFactory.createMatteBorder(0, active ? 4 : 0, 0, 0, SECONDARY_BLUE);
        Border padding = new EmptyBorder(10, active ? 16 : 20, 10, 15);
        btn.setBorder(BorderFactory.createCompoundBorder(leftHighlight, padding));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active) {
                    btn.setBackground(HEADER_NAVY);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active) {
                    btn.setBackground(PRIMARY_DARK);
                }
            }
        });
    }

    public static void styleSidebarButton(JButton btn, boolean active) {
        styleSidebarButton(btn, null, active);
    }

    /**
     * Styles standard input fields with focus highlight.
     */
    public static void styleTextField(JTextField tf) {
        tf.setFont(FONT_REGULAR);
        tf.setForeground(TEXT_DARK);
        tf.setBackground(Color.WHITE);
        tf.setCaretColor(TEXT_DARK);

        Border normalBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        );
        Border focusBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_BLUE, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        );

        tf.setBorder(normalBorder);
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(focusBorder);
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(normalBorder);
            }
        });
    }

    /**
     * Creates a styled Card Panel (White background #FFFFFF with subtle #D9E2EC border).
     */
    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));
        return card;
    }

    /**
     * Styles and formats a JTable with ISP-style Navy header and alternating row colors.
     */
    public static void formatTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(30);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(0xE3, 0xF2, 0xFD)); // Soft Blue highlight
        table.setSelectionForeground(TEXT_DARK);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_REGULAR_BOLD);
        header.setBackground(HEADER_NAVY);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF8, 0xFA, 0xFC));
                    c.setForeground(TEXT_DARK);

                    // Contextual status coloring
                    String strVal = String.valueOf(value);
                    if ("PAID".equalsIgnoreCase(strVal) || "ACTIVE".equalsIgnoreCase(strVal) || "SUCCESS".equalsIgnoreCase(strVal)) {
                        c.setForeground(SUCCESS_GREEN);
                        setFont(FONT_REGULAR_BOLD);
                    } else if ("UNPAID".equalsIgnoreCase(strVal) || "PENDING".equalsIgnoreCase(strVal)) {
                        c.setForeground(WARNING_ORANGE);
                        setFont(FONT_REGULAR_BOLD);
                    } else if ("OVERDUE".equalsIgnoreCase(strVal) || "INACTIVE".equalsIgnoreCase(strVal) || "FAILED".equalsIgnoreCase(strVal)) {
                        c.setForeground(DANGER_RED);
                        setFont(FONT_REGULAR_BOLD);
                    }
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }

    /**
     * Creates a standard ISP Header Panel (Navy #12355B to Dark Navy #0F2747).
     */
    public static JPanel createHeaderPanel(String title, String subtitle) {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, HEADER_NAVY, w, 0, PRIMARY_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setLayout(new javax.swing.BoxLayout(headerPanel, javax.swing.BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(16, 26, 16, 26));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(FONT_SUBHEADER);
        lblSubtitle.setForeground(new Color(0xD9, 0xE2, 0xEC)); // Light Gray #D9E2EC

        headerPanel.add(lblTitle);
        if (subtitle != null && !subtitle.isEmpty()) {
            headerPanel.add(javax.swing.Box.createVerticalStrut(4));
            headerPanel.add(lblSubtitle);
        }

        return headerPanel;
    }

    /**
     * Creates an info/stat card panel for dashboards.
     */
    public static JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new java.awt.BorderLayout(6, 6));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(14, 18, 14, 18)
        ));

        // Top accent indicator line
        JPanel line = new JPanel();
        line.setBackground(accentColor);
        line.setPreferredSize(new Dimension(0, 4));
        card.add(line, java.awt.BorderLayout.NORTH);

        JPanel content = new JPanel(new java.awt.GridLayout(2, 1, 3, 3));
        content.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_STAT_NUMBER);
        lblValue.setForeground(TEXT_DARK);

        content.add(lblTitle);
        content.add(lblValue);
        card.add(content, java.awt.BorderLayout.CENTER);

        return card;
    }

    public static final String CURRENCY_SYMBOL;
    static {
        // Automatically check if the system font supports the Indian Rupee symbol (\u20B9).
        // If supported, use '\u20B9'. If unsupported on older/minimal fonts, safely fall back to 'Rs. ' to prevent any square box.
        Font checkFont = new Font("Segoe UI", Font.PLAIN, 12);
        if (checkFont.canDisplay('\u20B9')) {
            CURRENCY_SYMBOL = "\u20B9";
        } else {
            CURRENCY_SYMBOL = "Rs. ";
        }
    }

    /**
     * Formats a BigDecimal amount as Indian Rupees (e.g. Rs. 1,873.00 or \u20B9 1,873.00).
     */
    public static String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) {
            return CURRENCY_SYMBOL + "0.00";
        }
        return String.format(CURRENCY_SYMBOL + "%,.2f", amount);
    }

    /**
     * Formats a double amount as Indian Rupees (e.g. Rs. 699.00 or \u20B9 699.00).
     */
    public static String formatCurrency(double amount) {
        return String.format(CURRENCY_SYMBOL + "%,.2f", amount);
    }

    // =========================================================================
    // 100% NATIVE JAVA SWING VECTOR ICONS (Zero Unicode/Emoji Square Boxes)
    // =========================================================================

    /**
     * Dashboard icon: 2x2 grid representing analytics and dashboard panels.
     */
    public static ImageIcon getDashboardIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int pad = 2;
        int gap = 2;
        int w = (size - pad * 2 - gap) / 2;
        g.fillRoundRect(pad, pad, w, w, 2, 2);
        g.fillRoundRect(pad + w + gap, pad, w, w, 2, 2);
        g.fillRoundRect(pad, pad + w + gap, w, w, 2, 2);
        g.fillRoundRect(pad + w + gap, pad + w + gap, w, w, 2, 2);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Customers icon: Clean dual user silhouette.
     */
    public static ImageIcon getCustomersIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        // Primary user
        int r1 = size / 5;
        int cx1 = size * 4 / 10;
        int cy1 = size / 3;
        g.fillOval(cx1 - r1, cy1 - r1, r1 * 2, r1 * 2);
        g.fillArc(cx1 - size / 3, size / 2, size * 2 / 3, size * 2 / 3, 0, 180);
        // Offset user (right)
        int r2 = size / 6;
        int cx2 = size * 7 / 10;
        int cy2 = size * 3 / 10;
        g.fillOval(cx2 - r2, cy2 - r2, r2 * 2, r2 * 2);
        g.fillArc(cx2 - size / 4, size * 5 / 10, size / 2, size / 2, 0, 180);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Single user profile icon.
     */
    public static ImageIcon getUserIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int r = size / 4;
        int cx = size / 2;
        int cy = size * 3 / 8;
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.fillArc(cx - size * 3 / 8, size * 5 / 8, size * 3 / 4, size * 3 / 4, 0, 180);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Plans / High-speed internet lightning bolt icon.
     */
    public static ImageIcon getPlansIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int midX = size / 2;
        int[] xp = {midX + 1, size / 4, midX, size / 4 + 1, size * 3 / 4, midX - 1, size * 3 / 5};
        int[] yp = {2, size / 2, size / 2, size - 2, size * 5 / 12, size * 5 / 12, 2};
        g.fillPolygon(xp, yp, 7);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Monthly Usage / Bandwidth bar chart icon.
     */
    public static ImageIcon getUsageIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int pad = 2;
        int barW = (size - pad * 2 - 4) / 3;
        int b = size - pad;
        g.fillRoundRect(pad, b - size / 3, barW, size / 3, 2, 2);
        g.fillRoundRect(pad + barW + 2, b - size * 2 / 3, barW, size * 2 / 3, 2, 2);
        g.fillRoundRect(pad + (barW + 2) * 2, b - (size - 4), barW, size - 4, 2, 2);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Invoices / Bills document sheet icon.
     */
    public static ImageIcon getBillsIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int left = 3;
        int top = 2;
        int w = size - 6;
        int h = size - 4;
        g.drawRoundRect(left, top, w, h, 2, 2);
        g.drawLine(left + 3, top + h / 3, left + w - 3, top + h / 3);
        g.drawLine(left + 3, top + h / 2, left + w - 3, top + h / 2);
        g.drawLine(left + 3, top + h * 2 / 3, left + w - 5, top + h * 2 / 3);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Payments / Credit Card icon.
     */
    public static ImageIcon getPaymentsIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        int x = 2;
        int y = size / 4;
        int w = size - 4;
        int h = size / 2;
        g.drawRoundRect(x, y, w, h, 3, 3);
        g.fillRect(x, y + 3, w, h / 4);
        g.fillRect(x + 3, y + h * 2 / 3, 3, 2);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Payment History / Clock icon.
     */
    public static ImageIcon getHistoryIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(2, 2, size - 5, size - 5);
        int cx = size / 2;
        int cy = size / 2;
        g.drawLine(cx, cy, cx, cy - size / 4);
        g.drawLine(cx, cy, cx + size / 5, cy);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * 24/7 Support / Headset icon.
     */
    public static ImageIcon getSupportIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.5f));
        g.drawArc(3, 2, size - 7, size - 7, 30, 120);
        g.fillRoundRect(1, size / 2 - 2, 4, size / 3, 2, 2);
        g.fillRoundRect(size - 6, size / 2 - 2, 4, size / 3, 2, 2);
        g.drawLine(size - 4, size * 2 / 3, size / 2, size - 2);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Refresh data icon (circular arrow).
     */
    public static ImageIcon getRefreshIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.6f));
        g.drawArc(3, 3, size - 6, size - 6, 45, 270);
        int cx = size - 3;
        int cy = size / 2;
        int[] xp = {cx - 3, cx + 3, cx};
        int[] yp = {cy - 3, cy - 3, cy + 3};
        g.fillPolygon(xp, yp, 3);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Globe / ISP network brand icon.
     */
    public static ImageIcon getGlobeIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(2, 2, size - 4, size - 4);
        g.drawLine(2, size / 2, size - 2, size / 2);
        g.drawOval(size / 4, 2, size / 2, size - 4);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Back navigation arrow icon.
     */
    public static ImageIcon getBackIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.8f));
        int midY = size / 2;
        g.drawLine(size - 3, midY, 4, midY);
        g.drawLine(4, midY, size / 2, midY - size / 4);
        g.drawLine(4, midY, size / 2, midY + size / 4);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Database configuration icon.
     */
    public static ImageIcon getDatabaseIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.2f));
        int w = size - 6;
        int x = 3;
        g.drawOval(x, 2, w, 5);
        g.drawArc(x, size / 2 - 2, w, 5, 180, 180);
        g.drawArc(x, size - 7, w, 5, 180, 180);
        g.drawLine(x, 4, x, size - 5);
        g.drawLine(x + w, 4, x + w, size - 5);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Search magnifying glass icon.
     */
    public static ImageIcon getSearchIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.6f));
        int r = size * 3 / 8;
        g.drawOval(2, 2, r * 2, r * 2);
        g.drawLine(2 + r * 2 - 1, 2 + r * 2 - 1, size - 2, size - 2);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Checkmark icon.
     */
    public static ImageIcon getCheckIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(size / 5, size / 2, size * 2 / 5, size * 4 / 5);
        g.drawLine(size * 2 / 5, size * 4 / 5, size * 4 / 5, size / 4);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Logout / exit door icon.
     */
    public static ImageIcon getLogoutIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.5f));
        // Door frame
        g.drawRect(2, 2, size / 2, size - 4);
        // Arrow pointing right
        int midY = size / 2;
        g.drawLine(size / 3, midY, size - 2, midY);
        g.drawLine(size - 5, midY - 3, size - 2, midY);
        g.drawLine(size - 5, midY + 3, size - 2, midY);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Plus / Add icon.
     */
    public static ImageIcon getPlusIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int mid = size / 2;
        g.drawLine(mid, 3, mid, size - 3);
        g.drawLine(3, mid, size - 3, mid);
        g.dispose();
        return new ImageIcon(img);
    }

    /**
     * Printer icon.
     */
    public static ImageIcon getPrintIcon(int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(1.4f));
        // Top paper
        g.drawRect(size / 4, 2, size / 2, size / 4);
        // Printer body
        g.drawRoundRect(2, size / 4 + 1, size - 4, size / 2 - 2, 2, 2);
        // Bottom paper
        g.drawRect(size / 4, size * 2 / 3, size / 2, size / 4);
        g.dispose();
        return new ImageIcon(img);
    }
}
